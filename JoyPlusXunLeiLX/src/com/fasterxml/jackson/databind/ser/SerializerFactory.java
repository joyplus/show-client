package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

/**
 * Abstract class that defines API used by {@link SerializerProvider}
 * to obtain actual
 * {@link JsonSerializer} instances from multiple distinct factories.
 */
public abstract class SerializerFactory
{
    /*
    /**********************************************************
    /* Additional configuration methods
    /**********************************************************
     */

    /**
     * Convenience method for creating a new factory instance with additional serializer
     * provider; equivalent to calling
     *<pre>
     *   withConfig(getConfig().withAdditionalSerializers(additional));
     *<pre>
     */
    public abstract SerializerFactory withAdditionalSerializers(Serializers additional);

    public abstract SerializerFactory withAdditionalKeySerializers(Serializers additional);
    
    /**
     * Convenience method for creating a new factory instance with additional bean
     * serializer modifier; equivalent to calling
     *<pre>
     *   withConfig(getConfig().withSerializerModifier(modifier));
     *<pre>
     */
    public abstract SerializerFactory withSerializerModifier(BeanSerializerModifier modifier);
    
    /*
    /**********************************************************
    /* Basic SerializerFactory API:
    /**********************************************************
     */

    /**
     * @deprecated Since 2.1: need to use the new variant without 'property'
     *    argument (since one won't be passed)
     */
    @Deprecated
    public JsonSerializer<Object> createSerializer(SerializerProvider prov,
            JavaType baseType, BeanProperty property)
        throws JsonMappingException {
        return createSerializer(prov, baseType);
    }
    
    /**
      * Method called to create (or, for immutable serializers, reuse) a serializer for given type. 
      * 
      * @param prov Provider that needs to be used to resolve annotation-provided
      *    serializers (but NOT for others)
      *    
      * @since 2.1 (earlier versions had method with different signature)
      */
    public abstract JsonSerializer<Object> createSerializer(SerializerProvider prov,
            JavaType baseType)
        throws JsonMappingException;
    
    /**
     * Method called to create a type information serializer for given base type,
     * if one is needed. If not needed (no polymorphic handling configured), should
     * return null.
     *
     * @param baseType Declared type to use as the base type for type information serializer
     * 
     * @return Type serializer to use for the base type, if one is needed; null if not.
     */
    public abstract TypeSerializer createTypeSerializer(SerializationConfig config,
            JavaType baseType)
        throws JsonMappingException;

    /**
     * Method called to create serializer to use for serializing JSON property names (which must
     * be output as <code>JsonToken.FIELD_NAME</code>) for Map that has specified declared
     * key type, and is for specified property (or, if property is null, as root value)
     * 
     * @param baseType Declared type for Map keys
     * 
     * @return Serializer to use, if factory knows it; null if not (in which case default
     *   serializer is to be used)
     */
    public abstract JsonSerializer<Object> createKeySerializer(SerializationConfig config,
            JavaType baseType)
        throws JsonMappingException;
}
