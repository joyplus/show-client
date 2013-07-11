package com.fasterxml.jackson.databind.jsontype.impl;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.util.JsonParserSequence;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.util.TokenBuffer;

/**
 * Type deserializer used with {@link As#PROPERTY}
 * inclusion mechanism.
 * Uses regular form (additional key/value entry before actual data)
 * when typed object is expressed as JSON Object; otherwise behaves similar to how
 * {@link As#WRAPPER_ARRAY} works.
 * Latter is used if JSON representation is polymorphic
 * 
 * @author tatu
 */
public class AsPropertyTypeDeserializer extends AsArrayTypeDeserializer
{
    private static final long serialVersionUID = 1L;

    public AsPropertyTypeDeserializer(JavaType bt, TypeIdResolver idRes,
            String typePropertyName, boolean typeIdVisible, Class<?> defaultImpl)
    {
        super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
    }

    public AsPropertyTypeDeserializer(AsPropertyTypeDeserializer src, BeanProperty property) {
        super(src, property);
    }
    
    @Override
    public TypeDeserializer forProperty(BeanProperty prop) {
        if (prop == _property) { // usually if it's null
            return this;
        }
        return new AsPropertyTypeDeserializer(this, prop);
    }
    
    @Override
    public As getTypeInclusion() {
        return As.PROPERTY;
    }

    /**
     * This is the trickiest thing to handle, since property we are looking
     * for may be anywhere...
     */
    @Override
    public Object deserializeTypedFromObject(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        // but first, sanity check to ensure we have START_OBJECT or FIELD_NAME
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = jp.nextToken();
        } else if (t == JsonToken.START_ARRAY) {
            /* This is most likely due to the fact that not all Java types are
             * serialized as JSON Objects; so if "as-property" inclusion is requested,
             * serialization of things like Lists must be instead handled as if
             * "as-wrapper-array" was requested.
             * But this can also be due to some custom handling: so, if "defaultImpl"
             * is defined, it will be asked to handle this case.
             */
            return _deserializeTypedUsingDefaultImpl(jp, ctxt, null);
        } else if (t != JsonToken.FIELD_NAME) {
            return _deserializeTypedUsingDefaultImpl(jp, ctxt, null);
        }
        // Ok, let's try to find the property. But first, need token buffer...
        TokenBuffer tb = null;

        for (; t == JsonToken.FIELD_NAME; t = jp.nextToken()) {
            String name = jp.getCurrentName();
            jp.nextToken(); // to point to the value
            if (_typePropertyName.equals(name)) { // gotcha!
                return _deserializeTypedForId(jp, ctxt, tb);
            }
            if (tb == null) {
                tb = new TokenBuffer(null);
            }
            tb.writeFieldName(name);
            tb.copyCurrentStructure(jp);
        }
        return _deserializeTypedUsingDefaultImpl(jp, ctxt, tb);
    }

    protected final Object _deserializeTypedForId(JsonParser jp, DeserializationContext ctxt,
            TokenBuffer tb)
        throws IOException, JsonProcessingException
    {
        String typeId = jp.getText();
        JsonDeserializer<Object> deser = _findDeserializer(ctxt, typeId);
        if (_typeIdVisible) { // need to merge id back in JSON input?
            if (tb == null) {
                tb = new TokenBuffer(null);
            }
            tb.writeFieldName(jp.getCurrentName());
            tb.writeString(typeId);
        }
        if (tb != null) { // need to put back skipped properties?
            jp = JsonParserSequence.createFlattened(tb.asParser(jp), jp);
        }
        // Must point to the next value; tb had no current, jp pointed to VALUE_STRING:
        jp.nextToken(); // to skip past String value
        // deserializer should take care of closing END_OBJECT as well
        return deser.deserialize(jp, ctxt);
    }
    
    // off-lined to keep main method lean and mean...
    protected Object _deserializeTypedUsingDefaultImpl(JsonParser jp,
            DeserializationContext ctxt, TokenBuffer tb)
        throws IOException, JsonProcessingException
    {
        // As per [JACKSON-614], may have default implementation to use
        if (_defaultImpl != null) { 
            JsonDeserializer<Object> deser = _findDefaultImplDeserializer(ctxt);
            if (tb != null) {
                tb.writeEndObject();
                jp = tb.asParser(jp);
                // must move to point to the first token:
                jp.nextToken();
            }
            return deser.deserialize(jp, ctxt);
        }
        // or, perhaps we just bumped into a "natural" value (boolean/int/double/String)?
        Object result = _deserializeIfNatural(jp, ctxt);
        if (result != null) {
            return result;
        }
        // or, something for which "as-property" won't work, changed into "wrapper-array" type:
        if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
            return super.deserializeTypedFromAny(jp, ctxt);
        }
        throw ctxt.wrongTokenException(jp, JsonToken.FIELD_NAME,
                "missing property '"+_typePropertyName+"' that is to contain type id  (for class "+baseTypeName()+")");
    }

    /* As per [JACKSON-352], also need to re-route "unknown" version. Need to think
     * this through bit more in future, but for now this does address issue and has
     * no negative side effects (at least within existing unit test suite).
     */
    @Override
    public Object deserializeTypedFromAny(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        /* [JACKSON-387]: Sometimes, however, we get an array wrapper; specifically
         *   when an array or list has been serialized with type information.
         */
        if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
            return super.deserializeTypedFromArray(jp, ctxt);
        }
        return deserializeTypedFromObject(jp, ctxt);
    }    
    
    // These are fine from base class:
    //public Object deserializeTypedFromArray(JsonParser jp, DeserializationContext ctxt)
    //public Object deserializeTypedFromScalar(JsonParser jp, DeserializationContext ctxt)    

    /**
     * Helper method used to check if given parser might be pointing to
     * a "natural" value, and one that would be acceptable as the
     * result value (compatible with declared base type)
     */
    protected Object _deserializeIfNatural(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        switch (jp.getCurrentToken()) {
        case VALUE_STRING:
            if (_baseType.getRawClass().isAssignableFrom(String.class)) {
                return jp.getText();
            }
            break;
        case VALUE_NUMBER_INT:
            if (_baseType.getRawClass().isAssignableFrom(Integer.class)) {
                return jp.getIntValue();
            }
            break;

        case VALUE_NUMBER_FLOAT:
            if (_baseType.getRawClass().isAssignableFrom(Double.class)) {
                return Double.valueOf(jp.getDoubleValue());
            }
            break;
        case VALUE_TRUE:
            if (_baseType.getRawClass().isAssignableFrom(Boolean.class)) {
                return Boolean.TRUE;
            }
            break;
        case VALUE_FALSE:
            if (_baseType.getRawClass().isAssignableFrom(Boolean.class)) {
                return Boolean.FALSE;
            }
            break;
        }
        return null;
    }
}
