package com.fasterxml.jackson.databind.ser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.impl.BeanAsArraySerializer;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.ser.impl.UnwrappingBeanSerializer;
import com.fasterxml.jackson.databind.ser.impl.WritableObjectId;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.fasterxml.jackson.databind.util.NameTransformer;

/**
 * Serializer class that can serialize Java objects that map
 * to JSON Object output. Internally handling is mostly dealt with
 * by a sequence of {@link BeanPropertyWriter}s that will handle
 * access value to serialize and call appropriate serializers to
 * write out JSON.
 *<p>
 * Implementation note: we will post-process resulting serializer,
 * to figure out actual serializers for final types. This must be
 * done from {@link #resolve} method, and NOT from constructor;
 * otherwise we could end up with an infinite loop.
 */
public class BeanSerializer
    extends BeanSerializerBase
{
    /*
    /**********************************************************
    /* Life-cycle: constructors
    /**********************************************************
     */

    /**
     * @param builder Builder object that contains collected information
     *   that may be needed for serializer
     * @param properties Property writers used for actual serialization
     */
    public BeanSerializer(JavaType type, BeanSerializerBuilder builder,
            BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties)
    {
        super(type, builder, properties, filteredProperties);
    }
    
    /**
     * Alternate copy constructor that can be used to construct
     * standard {@link BeanSerializer} passing an instance of
     * "compatible enough" source serializer.
     */
    protected BeanSerializer(BeanSerializerBase src) {
        super(src);
    }

    protected BeanSerializer(BeanSerializerBase src, ObjectIdWriter objectIdWriter) {
        super(src, objectIdWriter);
    }

    protected BeanSerializer(BeanSerializerBase src, String[] toIgnore) {
        super(src, toIgnore);
    }
    
    /*
    /**********************************************************
    /* Life-cycle: factory methods, fluent factories
    /**********************************************************
     */

    /**
     * Method for constructing dummy bean serializer; one that
     * never outputs any properties
     */
    public static BeanSerializer createDummy(JavaType forType)
    {
        return new BeanSerializer(forType, null, NO_PROPS, null);
    }

    @Override
    public JsonSerializer<Object> unwrappingSerializer(NameTransformer unwrapper) {
        return new UnwrappingBeanSerializer(this, unwrapper);
    }

    @Override
    public BeanSerializer withObjectIdWriter(ObjectIdWriter objectIdWriter) {
        return new BeanSerializer(this, objectIdWriter);
    }

    @Override
    protected BeanSerializer withIgnorals(String[] toIgnore) {
        return new BeanSerializer(this, toIgnore);
    }

    /**
     * Implementation has to check whether as-array serialization
     * is possible reliably; if (and only if) so, will construct
     * a {@link BeanAsArraySerializer}, otherwise will return this
     * serializer as is.
     */
    @Override
    protected BeanSerializerBase asArraySerializer()
    {
        /* Can not:
         * 
         * - have Object Id (may be allowed in future)
         * - have any getter
         * 
         */
        if ((_objectIdWriter == null)
                && (_anyGetterWriter == null)
                && (_propertyFilterId == null)
                ) {
            return new BeanAsArraySerializer(this);
        }
        // already is one, so:
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
        jgen.writeStartObject();
        if (_propertyFilterId != null) {
            serializeFieldsFiltered(bean, jgen, provider);
        } else {
            serializeFields(bean, jgen, provider);
        }
        jgen.writeEndObject();
    }

    private final void serializeWithObjectId(Object bean,
            JsonGenerator jgen, SerializerProvider provider)
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
        // If not, need to inject the id:
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
        return "BeanSerializer for "+handledType().getName();
    }
}
