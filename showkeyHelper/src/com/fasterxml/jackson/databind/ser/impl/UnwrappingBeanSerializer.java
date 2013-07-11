package com.fasterxml.jackson.databind.ser.impl;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.*;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.fasterxml.jackson.databind.util.NameTransformer;

public class UnwrappingBeanSerializer
    extends BeanSerializerBase
{
    /**
     * Transformer used to add prefix and/or suffix for properties
     * of unwrapped POJO.
     */
    protected final NameTransformer _nameTransformer;
    
    /*
    /**********************************************************
    /* Life-cycle: constructors
    /**********************************************************
     */

    /**
     * Constructor used for creating unwrapping instance of a
     * standard <code>BeanSerializer</code>
     */
    public UnwrappingBeanSerializer(BeanSerializerBase src, NameTransformer transformer) {
        super(src, transformer);
        _nameTransformer = transformer;
    }

    public UnwrappingBeanSerializer(UnwrappingBeanSerializer src, ObjectIdWriter objectIdWriter) {    
        super(src, objectIdWriter);
        _nameTransformer = src._nameTransformer;
    }

    protected UnwrappingBeanSerializer(UnwrappingBeanSerializer src, String[] toIgnore) {
        super(src, toIgnore);
        _nameTransformer = src._nameTransformer;
    }
    
    /*
    /**********************************************************
    /* Life-cycle: factory methods, fluent factories
    /**********************************************************
     */

    @Override
    public JsonSerializer<Object> unwrappingSerializer(NameTransformer transformer) {
        // !!! 23-Jan-2012, tatu: Should we chain transformers?
        return new UnwrappingBeanSerializer(this, transformer);
    }

    @Override
    public boolean isUnwrappingSerializer() {
        return true; // sure is
    }

    @Override
    public UnwrappingBeanSerializer withObjectIdWriter(ObjectIdWriter objectIdWriter) {
        return new UnwrappingBeanSerializer(this, objectIdWriter);
    }

    @Override
    protected UnwrappingBeanSerializer withIgnorals(String[] toIgnore) {
        return new UnwrappingBeanSerializer(this, toIgnore);
    }

    /**
     * JSON Array output can not be done if unwrapping operation is
     * requested; so implementation will simply return 'this'.
     */
    @Override
    protected BeanSerializerBase asArraySerializer() {
        return this;
    }
    
    /*
    /**********************************************************
    /* JsonSerializer implementation that differs between impls
    /**********************************************************
     */

    /**
     * Main serialization method that will delegate actual output to
     * configured
     * {@link BeanPropertyWriter} instances.
     */
    @Override
    public final void serialize(Object bean, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        if (_objectIdWriter != null) {
            serializeWithObjectId(bean, jgen, provider);
            return;
        }
        if (_propertyFilterId != null) {
            serializeFieldsFiltered(bean, jgen, provider);
        } else {
            serializeFields(bean, jgen, provider);
        }
    }

    private final void serializeWithObjectId(Object bean, JsonGenerator jgen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        final ObjectIdWriter w = _objectIdWriter;
        WritableObjectId oid = provider.findObjectId(bean, w.generator);
        Object id = oid.id;
        
        if (id != null) { // have seen before; serialize just id
            oid.serializer.serialize(id, jgen, provider);
            return;
        }
        // if not, bit more work:
        oid.serializer = w.serializer;
        oid.id = id = oid.generator.generateId(bean);
        // possibly. Or maybe not:
        if (w.alwaysAsId) { 
            oid.serializer.serialize(id, jgen, provider);
            return;
        }
        
        jgen.writeStartObject();
        SerializedString name = w.propertyName;
        if (name != null) {
            jgen.writeFieldName(name);
            w.serializer.serialize(id, jgen, provider);
        }
        if (_propertyFilterId != null) {
            serializeFieldsFiltered(bean, jgen, provider);
        } else {
            serializeFields(bean, jgen, provider);
        }
        jgen.writeEndObject();
    }
    
    /*
    /**********************************************************
    /* Standard methods
    /**********************************************************
     */

    @Override public String toString() {
        return "UnwrappingBeanSerializer for "+handledType().getName();
    }
}
