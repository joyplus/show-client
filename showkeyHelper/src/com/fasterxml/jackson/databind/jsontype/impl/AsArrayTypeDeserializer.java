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
 * Type deserializer used with {@link As#WRAPPER_ARRAY}
 * inclusion mechanism. Simple since JSON structure used is always
 * the same, regardless of structure used for actual value: wrapping
 * is done using a 2-element JSON Array where type id is the first
 * element, and actual object data as second element.
 * 
 * @author tatu
 */
public class AsArrayTypeDeserializer
    extends TypeDeserializerBase
    implements java.io.Serializable
{
    private static final long serialVersionUID = 5345570420394408290L;

    public AsArrayTypeDeserializer(JavaType bt, TypeIdResolver idRes,
            String typePropertyName, boolean typeIdVisible, Class<?> defaultImpl)
    {
        super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
    }

    public AsArrayTypeDeserializer(AsArrayTypeDeserializer src, BeanProperty property) {
        super(src, property);
    }
    
    @Override
    public TypeDeserializer forProperty(BeanProperty prop) {
        if (prop == _property) { // usually if it's null
            return this;
        }
        return new AsArrayTypeDeserializer(this, prop);
    }
    
    @Override
    public As getTypeInclusion() {
        return As.WRAPPER_ARRAY;
    }

    /**
     * Method called when actual object is serialized as JSON Array.
     */
    @Override
    public Object deserializeTypedFromArray(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        return _deserialize(jp, ctxt);
    }

    /**
     * Method called when actual object is serialized as JSON Object
     */
    @Override
    public Object deserializeTypedFromObject(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        return _deserialize(jp, ctxt);
    }
    
    @Override
    public Object deserializeTypedFromScalar(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        return _deserialize(jp, ctxt);
    }    

    @Override
    public Object deserializeTypedFromAny(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        return _deserialize(jp, ctxt);
    }    
    
    /*
    /***************************************************************
    /* Internal methods
    /***************************************************************
     */

    /**
     * Method that handles type information wrapper, locates actual
     * subtype deserializer to use, and calls it to do actual
     * deserialization.
     */
    private final Object _deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        boolean hadStartArray = jp.isExpectedStartArrayToken();
        String typeId = _locateTypeId(jp, ctxt);
        JsonDeserializer<Object> deser = _findDeserializer(ctxt, typeId);
        // Minor complication: we may need to merge type id in?
        if (_typeIdVisible && jp.getCurrentToken() == JsonToken.START_OBJECT) {
            // but what if there's nowhere to add it in? Error? Or skip? For now, skip.
            TokenBuffer tb = new TokenBuffer(null);
            tb.writeStartObject(); // recreate START_OBJECT
            tb.writeFieldName(_typePropertyName);
            tb.writeString(typeId);
            jp = JsonParserSequence.createFlattened(tb.asParser(jp), jp);
            jp.nextToken();
        }
        Object value = deser.deserialize(jp, ctxt);
        // And then need the closing END_ARRAY
        if (hadStartArray && jp.nextToken() != JsonToken.END_ARRAY) {
            throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY,
                    "expected closing END_ARRAY after type information and deserialized value");
        }
        return value;
    }    
    
    protected final String _locateTypeId(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        if (!jp.isExpectedStartArrayToken()) {
            // [JACKSON-712] Need to allow even more customized handling, if something unexpected seen...
            // but should there be a way to limit this to likely success cases?
            if (_defaultImpl != null) {
                return _idResolver.idFromBaseType();
            }
            throw ctxt.wrongTokenException(jp, JsonToken.START_ARRAY, "need JSON Array to contain As.WRAPPER_ARRAY type information for class "+baseTypeName());
        }
        // And then type id as a String
        if (jp.nextToken() != JsonToken.VALUE_STRING) {
            if (_defaultImpl != null) {
                return _idResolver.idFromBaseType();
            }
            throw ctxt.wrongTokenException(jp, JsonToken.VALUE_STRING, "need JSON String that contains type id (for subtype of "+baseTypeName()+")");
        }
        String result = jp.getText();
        jp.nextToken();
        return result;
    }
}
