package com.fasterxml.jackson.databind;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.*;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.*;
import com.fasterxml.jackson.databind.ser.impl.*;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.RootNameLookup;

/**
 * Class that defines API used by {@link ObjectMapper} and
 * {@link JsonSerializer}s to obtain serializers capable of serializing
 * instances of specific types; as well as the default implementation
 * of the functionality.
 *<p>
 * Provider handles caching aspects of serializer handling; all construction
 * details are delegated to {@link SerializerFactory} instance.
 *<p>
 * Object life-cycle is such that an initial instance ("blueprint") is created
 * and referenced by {@link ObjectMapper} and {@link ObjectWriter} intances;
 * but for actual usage, a configured instance is created by using
 * a create method in sub-class
 * {@link com.fasterxml.jackson.databind.ser.DefaultSerializerProvider}.
 * Only this instance can be used for actual serialization calls; blueprint
 * object is only to be used for creating instances.
 */
public abstract class SerializerProvider
{
    protected final static JavaType TYPE_OBJECT = TypeFactory.defaultInstance().uncheckedSimpleType(Object.class);

    /**
     * Setting for determining whether mappings for "unknown classes" should be
     * cached for faster resolution. Usually this isn't needed, but maybe it
     * is in some cases?
     */
    protected final static boolean CACHE_UNKNOWN_MAPPINGS = false;

    public final static JsonSerializer<Object> DEFAULT_NULL_KEY_SERIALIZER =
        new FailingSerializer("Null key for a Map not allowed in JSON (use a converting NullKeySerializer?)");

    public final static JsonSerializer<Object> DEFAULT_UNKNOWN_SERIALIZER = new UnknownSerializer();

    /*
    /**********************************************************
    /* Configuration, general
    /**********************************************************
     */
    
    /**
     * Serialization configuration to use for serialization processing.
     */
    final protected SerializationConfig _config;

    /**
     * View used for currently active serialization, if any.
     */
    final protected Class<?> _serializationView;
    
    /*
    /**********************************************************
    /* Configuration, factories
    /**********************************************************
     */

    /**
     * Factory used for constructing actual serializer instances.
     */
    final protected SerializerFactory _serializerFactory;

    /*
    /**********************************************************
    /* Helper objects for caching
    /**********************************************************
     */
    
    /**
     * Cache for doing type-to-value-serializer lookups.
     */
    final protected SerializerCache _serializerCache;

    /**
     * Helper object for keeping track of introspected root names
     */
    final protected RootNameLookup _rootNames;
    
    /*
    /**********************************************************
    /* Configuration, specialized serializers
    /**********************************************************
     */

    /**
     * Serializer that gets called for values of types for which no
     * serializers can be constructed.
     *<p>
     * The default serializer will simply thrown an exception.
     */
    protected JsonSerializer<Object> _unknownTypeSerializer = DEFAULT_UNKNOWN_SERIALIZER;

    /**
     * Serializer used to output non-null keys of Maps (which will get
     * output as JSON Objects), if not null; if null, us the standard
     * default key serializer.
     */
    protected JsonSerializer<Object> _keySerializer;

    /**
     * Serializer used to output a null value. Default implementation
     * writes nulls using {@link JsonGenerator#writeNull}.
     */
    protected JsonSerializer<Object> _nullValueSerializer = NullSerializer.instance;

    /**
     * Serializer used to (try to) output a null key, due to an entry of
     * {@link java.util.Map} having null key.
     * The default implementation will throw an exception if this happens;
     * alternative implementation (like one that would write an Empty String)
     * can be defined.
     */
    protected JsonSerializer<Object> _nullKeySerializer = DEFAULT_NULL_KEY_SERIALIZER;

    /*
    /**********************************************************
    /* State, for non-blueprint instances: generic
    /**********************************************************
     */
    
    /**
     * For fast lookups, we will have a local non-shared read-only
     * map that contains serializers previously fetched.
     */
    protected final ReadOnlyClassToSerializerMap _knownSerializers;

    /**
     * Lazily acquired and instantiated formatter object: initialized
     * first time it is needed, reused afterwards. Used via instances
     * (not blueprints), so that access need not be thread-safe.
     */
    protected DateFormat _dateFormat;

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    /**
     * Constructor for creating master (or "blue-print") provider object,
     * which is only used as the template for constructing per-binding
     * instances.
     */
    public SerializerProvider()
    {
        _config = null;
        _serializerFactory = null;
        _serializerCache = new SerializerCache();
        // Blueprints doesn't have access to any serializers...
        _knownSerializers = null;
        _rootNames = new RootNameLookup();

        _serializationView = null;
    }

    /**
     * "Copy-constructor", used by sub-classes.
     *
     * @param src Blueprint object used as the baseline for this instance
     */
    protected SerializerProvider(SerializerProvider src,
            SerializationConfig config, SerializerFactory f)
    {
        if (config == null) {
            throw new NullPointerException();
        }
        _serializerFactory = f;
        _config = config;

        _serializerCache = src._serializerCache;
        _unknownTypeSerializer = src._unknownTypeSerializer;
        _keySerializer = src._keySerializer;
        _nullValueSerializer = src._nullValueSerializer;
        _nullKeySerializer = src._nullKeySerializer;
        _rootNames = src._rootNames;

        /* Non-blueprint instances do have a read-only map; one that doesn't
         * need synchronization for lookups.
         */
        _knownSerializers = _serializerCache.getReadOnlyLookupMap();

        _serializationView = config.getActiveView();
    }
    
    /*
    /**********************************************************
    /* Methods for configuring default settings
    /**********************************************************
     */

    /**
     * Method that can be used to specify serializer that will be
     * used to write JSON property names matching null keys for Java
     * Maps (which will throw an exception if try write such property
     * name)
     */
    public void setDefaultKeySerializer(JsonSerializer<Object> ks)
    {
        if (ks == null) {
            throw new IllegalArgumentException("Can not pass null JsonSerializer");
        }
        _keySerializer = ks;
    }

    /**
     * Method that can be used to specify serializer that will be
     * used to write JSON values matching Java null values
     * instead of default one (which simply writes JSON null)
     */
    public void setNullValueSerializer(JsonSerializer<Object> nvs)
    {
        if (nvs == null) {
            throw new IllegalArgumentException("Can not pass null JsonSerializer");
        }
        _nullValueSerializer = nvs;
    }

    /**
     * Method that can be used to specify serializer to use for serializing
     * all non-null JSON property names, unless more specific key serializer
     * is found (i.e. if not custom key serializer has been registered for
     * Java type).
     *<p>
     * Note that key serializer registration are different from value serializer
     * registrations.
     */
    public void setNullKeySerializer(JsonSerializer<Object> nks)
    {
        if (nks == null) {
            throw new IllegalArgumentException("Can not pass null JsonSerializer");
        }
        _nullKeySerializer = nks;
    }
        
    /*
    /**********************************************************
    /* Access to general configuration
    /**********************************************************
     */

    /**
     * Method for accessing configuration for the serialization processing.
     */
    public final SerializationConfig getConfig() { return _config; }

    /**
     * Convenience method for checking whether specified serialization
     * feature is enabled or not.
     * Shortcut for:
     *<pre>
     *  getConfig().isEnabled(feature);
     *</pre>
     */
    public final boolean isEnabled(MapperFeature feature) {
        return _config.isEnabled(feature);
    }

    /**
     * Convenience method for checking whether specified serialization
     * feature is enabled or not.
     * Shortcut for:
     *<pre>
     *  getConfig().isEnabled(feature);
     *</pre>
     */
    public final boolean isEnabled(SerializationFeature feature) {
        return _config.isEnabled(feature);
    }

    /**
     * Convenience method for accessing serialization view in use (if any); equivalent to:
     *<pre>
     *   getConfig().canOverrideAccessModifiers();
     *</pre>
     */
    public final boolean canOverrideAccessModifiers() {
        return _config.canOverrideAccessModifiers();
    }
    
    /**
     * Convenience method for accessing serialization view in use (if any); equivalent to:
     *<pre>
     *   getConfig().getAnnotationIntrospector();
     *</pre>
     */
    public final AnnotationIntrospector getAnnotationIntrospector() {
        return _config.getAnnotationIntrospector();
    }
    
    /**
     * Convenience method for accessing serialization view in use (if any); equivalent to:
     *<pre>
     *   getConfig().getSerializationView();
     *</pre>
     */
    public final Class<?> getSerializationView() { return _serializationView; }

    /**
     * Convenience method for accessing provider to find serialization filters used,
     * equivalent to calling:
     *<pre>
     *   getConfig().getFilterProvider();
     *</pre>
     */
    public final FilterProvider getFilterProvider() {
        return _config.getFilterProvider();
    }

    /**
     * Method for accessing default Locale to use: convenience method for
     *<pre>
     *   getConfig().getLocale();
     *</pre>
     */
    public Locale getLocale() {
        return _config.getLocale();
    }

    /**
     * Method for accessing default TimeZone to use: convenience method for
     *<pre>
     *   getConfig().getTimeZone();
     *</pre>
     */
    public TimeZone getTimeZone() {
        return _config.getTimeZone();
    }
    
    /*
    /**********************************************************
    /* Access to type handling
    /**********************************************************
     */
    
    /**
     * Convenience method for constructing {@link JavaType} for given JDK
     * type (usually {@link java.lang.Class})
     */
    public JavaType constructType(Type type) {
         return _config.getTypeFactory().constructType(type);
    }

    /**
     * Convenience method for constructing subtypes, retaining generic
     * type parameter (if any)
     */
    public JavaType constructSpecializedType(JavaType baseType, Class<?> subclass) {
        return _config.constructSpecializedType(baseType, subclass);
    }

    public TypeFactory getTypeFactory() {
        return _config.getTypeFactory();
    }

    /*
    /**********************************************************
    /* Access to Object Id aspects
    /**********************************************************
     */

    /**
     * Method called to find the Object Id for given POJO, if one
     * has been generated. Will always return a non-null Object;
     * contents vary depending on whether an Object Id already
     * exists or not.
     */
    public abstract WritableObjectId findObjectId(Object forPojo,
            ObjectIdGenerator<?> generatorType);
    
    /*
    /**********************************************************
    /* General serializer locating functionality
    /**********************************************************
     */

    /**
     * Method called to get hold of a serializer for a value of given type;
     * or if no such serializer can be found, a default handler (which
     * may do a best-effort generic serialization or just simply
     * throw an exception when invoked).
     *<p>
     * Note: this method is only called for non-null values; not for keys
     * or null values. For these, check out other accessor methods.
     *<p>
     * Note that starting with version 1.5, serializers should also be type-aware
     * if they handle polymorphic types. That means that it may be necessary
     * to also use a {@link TypeSerializer} based on declared (static) type
     * being serializer (whereas actual data may be serialized using dynamic
     * type)
     *
     * @throws JsonMappingException if there are fatal problems with
     *   accessing suitable serializer; including that of not
     *   finding any serializer
     */
    public JsonSerializer<Object> findValueSerializer(Class<?> valueType,
            BeanProperty property)
        throws JsonMappingException
    {
        // Fast lookup from local lookup thingy works?
        JsonSerializer<Object> ser = _knownSerializers.untypedValueSerializer(valueType);
        if (ser == null) {
            // If not, maybe shared map already has it?
            ser = _serializerCache.untypedValueSerializer(valueType);
            if (ser == null) {
                // ... possibly as fully typed?
                ser = _serializerCache.untypedValueSerializer(_config.constructType(valueType));
                if (ser == null) {
                    // If neither, must create
                    ser = _createAndCacheUntypedSerializer(valueType);
                    // Not found? Must use the unknown type serializer
                    /* Couldn't create? Need to return the fallback serializer, which
                     * most likely will report an error: but one question is whether
                     * we should cache it?
                     */
                    if (ser == null) {
                        ser = getUnknownTypeSerializer(valueType);
                        // Should this be added to lookups?
                        if (CACHE_UNKNOWN_MAPPINGS) {
                            _serializerCache.addAndResolveNonTypedSerializer(valueType, ser, this);
                        }
                        return ser;
                    }
                }
            }
        }
        // at this point, resolution has occured, but not contextualization
        return _handleContextual(ser, property);
    }

    /**
     * Similar to {@link #findValueSerializer(Class,BeanProperty)}, but takes
     * full generics-aware type instead of raw class.
     * This is necessary for accurate handling of external type information,
     * to handle polymorphic types.
     * 
     * @param property When creating secondary serializers, property for which
     *   serializer is needed: annotations of the property (or bean that contains it)
     *   may be checked to create contextual serializers.
     */
    public JsonSerializer<Object> findValueSerializer(JavaType valueType, BeanProperty property)
        throws JsonMappingException
    {
        // Fast lookup from local lookup thingy works?
        JsonSerializer<Object> ser = _knownSerializers.untypedValueSerializer(valueType);
        if (ser == null) {
            // If not, maybe shared map already has it?
            ser = _serializerCache.untypedValueSerializer(valueType);
            if (ser == null) {
                // If neither, must create
                ser = _createAndCacheUntypedSerializer(valueType);
                // Not found? Must use the unknown type serializer
                /* Couldn't create? Need to return the fallback serializer, which
                 * most likely will report an error: but one question is whether
                 * we should cache it?
                 */
                if (ser == null) {
                    ser = getUnknownTypeSerializer(valueType.getRawClass());
                    // Should this be added to lookups?
                    if (CACHE_UNKNOWN_MAPPINGS) {
                        _serializerCache.addAndResolveNonTypedSerializer(valueType, ser, this);
                    }
                    return ser;
                }
            }
        }
        return _handleContextual(ser, property);
    }
    
    /**
     * Method called to locate regular serializer, matching type serializer,
     * and if both found, wrap them in a serializer that calls both in correct
     * sequence. This method is currently only used for root-level serializer
     * handling to allow for simpler caching. A call can always be replaced
     * by equivalent calls to access serializer and type serializer separately.
     * 
     * @param valueType Type for purpose of locating a serializer; usually dynamic
     *   runtime type, but can also be static declared type, depending on configuration
     * @param cache Whether resulting value serializer should be cached or not; this is just
     *    a hint
     * @param property When creating secondary serializers, property for which
     *   serializer is needed: annotations of the property (or bean that contains it)
     *   may be checked to create contextual serializers.
     */
    public JsonSerializer<Object> findTypedValueSerializer(Class<?> valueType,
            boolean cache, BeanProperty property)
        throws JsonMappingException
    {
        // Two-phase lookups; local non-shared cache, then shared:
        JsonSerializer<Object> ser = _knownSerializers.typedValueSerializer(valueType);
        if (ser != null) {
            return ser;
        }
        // If not, maybe shared map already has it?
        ser = _serializerCache.typedValueSerializer(valueType);
        if (ser != null) {
            return ser;
        }

        // Well, let's just compose from pieces:
        ser = findValueSerializer(valueType, property);
        TypeSerializer typeSer = _serializerFactory.createTypeSerializer(_config,
                _config.constructType(valueType));
        if (typeSer != null) {
            typeSer = typeSer.forProperty(property);
            ser = new TypeWrappedSerializer(typeSer, ser);
        }
        if (cache) {
            _serializerCache.addTypedSerializer(valueType, ser);
        }
        return ser;
    }

    /**
     * Method called to locate regular serializer, matching type serializer,
     * and if both found, wrap them in a serializer that calls both in correct
     * sequence. This method is currently only used for root-level serializer
     * handling to allow for simpler caching. A call can always be replaced
     * by equivalent calls to access serializer and type serializer separately.
     * 
     * @param valueType Declared type of value being serialized (which may not
     *    be actual runtime type); used for finding both value serializer and
     *    type serializer to use for adding polymorphic type (if any)
     * @param cache Whether resulting value serializer should be cached or not; this is just
     *    a hint 
     * @param property When creating secondary serializers, property for which
     *   serializer is needed: annotations of the property (or bean that contains it)
     *   may be checked to create contextual serializers.
     */
    public JsonSerializer<Object> findTypedValueSerializer(JavaType valueType, boolean cache,
            BeanProperty property)
        throws JsonMappingException
    {
        // Two-phase lookups; local non-shared cache, then shared:
        JsonSerializer<Object> ser = _knownSerializers.typedValueSerializer(valueType);
        if (ser != null) {
            return ser;
        }
        // If not, maybe shared map already has it?
        ser = _serializerCache.typedValueSerializer(valueType);
        if (ser != null) {
            return ser;
        }

        // Well, let's just compose from pieces:
        ser = findValueSerializer(valueType, property);
        TypeSerializer typeSer = _serializerFactory.createTypeSerializer(_config, valueType);
        if (typeSer != null) {
            typeSer = typeSer.forProperty(property);
            ser = new TypeWrappedSerializer(typeSer, ser);
        }
        if (cache) {
            _serializerCache.addTypedSerializer(valueType, ser);
        }
        return ser;
    }

    /**
     * Method called to get the serializer to use for serializing
     * non-null Map keys. Separation from regular
     * {@link #findValueSerializer} method is because actual write
     * method must be different (@link JsonGenerator#writeFieldName};
     * but also since behavior for some key types may differ.
     *<p>
     * Note that the serializer itself can be called with instances
     * of any Java object, but not nulls.
     */
    public JsonSerializer<Object> findKeySerializer(JavaType keyType,
            BeanProperty property)
        throws JsonMappingException
    {
        JsonSerializer<Object> ser = _serializerFactory.createKeySerializer(_config, keyType);
    
        // First things first: maybe there are registered custom implementations
        // if not, use default one:
        if (ser == null) {
            if (_keySerializer == null) {
                ser = StdKeySerializers.getStdKeySerializer(keyType);
            } else {
                ser = _keySerializer;
            }
        }
        // 25-Feb-2011, tatu: As per [JACKSON-519], need to ensure contextuality works here, too
        return _handleContextualResolvable(ser, property);
    }
    
    /*
    /********************************************************
    /* Accessors for specialized serializers
    /********************************************************
     */

    /**
     * @since 2.0
     */
    public JsonSerializer<Object> getDefaultNullKeySerializer() {
        return _nullKeySerializer;
    }

    /**
     * @since 2.0
     */
    public JsonSerializer<Object> getDefaultNullValueSerializer() {
        return _nullValueSerializer;
    }
    
    /**
     * Method called to get the serializer to use for serializing
     * Map keys that are nulls: this is needed since JSON does not allow
     * any non-String value as key, including null.
     *<p>
     * Typically, returned serializer
     * will either throw an exception, or use an empty String; but
     * other behaviors are possible.
     */
    /**
     * Method called to find a serializer to use for null values for given
     * declared type. Note that type is completely based on declared type,
     * since nulls in Java have no type and thus runtime type can not be
     * determined.
     * 
     * @since 2.0
     */
    public JsonSerializer<Object> findNullKeySerializer(JavaType serializationType,
            BeanProperty property)
        throws JsonMappingException {
        return getDefaultNullKeySerializer();
    }

    /**
     * Method called to get the serializer to use for serializing null
     * property values.
     *<p>
     * Default implementation simply calls {@link #getDefaultNullValueSerializer()};
     * can be overridden to add custom null serialization for properties
     * of certain type or name.
     * 
     * @since 2.0
     */
    public JsonSerializer<Object> findNullValueSerializer(BeanProperty property)
        throws JsonMappingException {
        return getDefaultNullValueSerializer();
    }

    /**
     * Method called to get the serializer to use if provider
     * can not determine an actual type-specific serializer
     * to use; typically when none of {@link SerializerFactory}
     * instances are able to construct a serializer.
     *<p>
     * Typically, returned serializer will throw an exception,
     * although alternatively {@link com.fasterxml.jackson.databind.ser.std.ToStringSerializer}
     * could be returned as well.
     *
     * @param unknownType Type for which no serializer is found
     */
    public JsonSerializer<Object> getUnknownTypeSerializer(Class<?> unknownType) {
        return _unknownTypeSerializer;
    }

    /*
    /**********************************************************
    /* Methods for creating instances based on annotations
    /**********************************************************
     */

    /**
     * Method that can be called to construct and configure serializer instance,
     * either given a {@link Class} to instantiate (with default constructor),
     * or an uninitialized serializer instance.
     * Either way, serialize will be properly resolved
     * (via {@link com.fasterxml.jackson.databind.ser.ResolvableSerializer}) and/or contextualized
     * (via {@link com.fasterxml.jackson.databind.ser.ContextualSerializer}) as necessary.
     * 
     * @param annotated Annotated entity that contained definition
     * @param serDef Serializer definition: either an instance or class
     */
    public abstract JsonSerializer<Object> serializerInstance(Annotated annotated,
            Object serDef)
        throws JsonMappingException;

    public abstract ObjectIdGenerator<?> objectIdGeneratorInstance(Annotated annotated,
            ObjectIdInfo objectIdInfo)
        throws JsonMappingException;
    
    /*
    /********************************************************
    /* Convenience methods
    /********************************************************
     */

    /**
     * Convenience method that will serialize given value (which can be
     * null) using standard serializer locating functionality. It can
     * be called for all values including field and Map values, but usually
     * field values are best handled calling
     * {@link #defaultSerializeField} instead.
     */
    public final void defaultSerializeValue(Object value, JsonGenerator jgen)
        throws IOException, JsonProcessingException
    {
        if (value == null) {
            getDefaultNullValueSerializer().serialize(null, jgen, this);
        } else {
            Class<?> cls = value.getClass();
            findTypedValueSerializer(cls, true, null).serialize(value, jgen, this);
        }
    }
    
    /**
     * Convenience method that will serialize given field with specified
     * value. Value may be null. Serializer is done using the usual
     * null) using standard serializer locating functionality.
     */
    public final void defaultSerializeField(String fieldName, Object value, JsonGenerator jgen)
        throws IOException, JsonProcessingException
    {
        jgen.writeFieldName(fieldName);
        if (value == null) {
            /* Note: can't easily check for suppression at this point
             * any more; caller must check it.
             */
            getDefaultNullValueSerializer().serialize(null, jgen, this);
        } else {
            Class<?> cls = value.getClass();
            findTypedValueSerializer(cls, true, null).serialize(value, jgen, this);
        }
    }

    /*
    /**********************************************************
    /* Convenience methods
    /**********************************************************
     */

    /**
     * Method that will handle serialization of Date(-like) values, using
     * {@link SerializationConfig} settings to determine expected serialization
     * behavior.
     * Note: date here means "full" date, that is, date AND time, as per
     * Java convention (and not date-only values like in SQL)
     */
    public final void defaultSerializeDateValue(long timestamp, JsonGenerator jgen)
        throws IOException, JsonProcessingException
    {
        // [JACKSON-87]: Support both numeric timestamps and textual
        if (isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)) {
            jgen.writeNumber(timestamp);
        } else {
            jgen.writeString(_dateFormat().format(new Date(timestamp)));
        }
    }

    /**
     * Method that will handle serialization of Date(-like) values, using
     * {@link SerializationConfig} settings to determine expected serialization
     * behavior.
     * Note: date here means "full" date, that is, date AND time, as per
     * Java convention (and not date-only values like in SQL)
     */
    public final void defaultSerializeDateValue(Date date, JsonGenerator jgen)
        throws IOException, JsonProcessingException
    {
        // [JACKSON-87]: Support both numeric timestamps and textual
        if (isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)) {
            jgen.writeNumber(date.getTime());
        } else {
            jgen.writeString(_dateFormat().format(date));
        }
    }

    /**
     * Method that will handle serialization of Dates used as {@link java.util.Map} keys,
     * based on {@link SerializationFeature#WRITE_DATE_KEYS_AS_TIMESTAMPS}
     * value (and if using textual representation, configured date format)
     */
    public void defaultSerializeDateKey(long timestamp, JsonGenerator jgen)
        throws IOException, JsonProcessingException
    {
        if (isEnabled(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)) {
            jgen.writeFieldName(String.valueOf(timestamp));
        } else {
            jgen.writeFieldName(_dateFormat().format(new Date(timestamp)));
        }
    }

    /**
     * Method that will handle serialization of Dates used as {@link java.util.Map} keys,
     * based on {@link SerializationFeature#WRITE_DATE_KEYS_AS_TIMESTAMPS}
     * value (and if using textual representation, configured date format)
     */
    public void defaultSerializeDateKey(Date date, JsonGenerator jgen)
        throws IOException, JsonProcessingException
    {
        if (isEnabled(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)) {
            jgen.writeFieldName(String.valueOf(date.getTime()));
        } else {
            jgen.writeFieldName(_dateFormat().format(date));
        }
    }
    
    public final void defaultSerializeNull(JsonGenerator jgen)
        throws IOException, JsonProcessingException
    {
        getDefaultNullValueSerializer().serialize(null, jgen, this);
    }

    /*
    /********************************************************
    /* Helper methods
    /********************************************************
     */
    
    protected void _reportIncompatibleRootType(Object value, JavaType rootType)
        throws IOException, JsonProcessingException
    {
        /* 07-Jan-2010, tatu: As per [JACKSON-456] better handle distinction between wrapper types,
         *    primitives
         */
        if (rootType.isPrimitive()) {
            Class<?> wrapperType = ClassUtil.wrapperType(rootType.getRawClass());
            // If it's just difference between wrapper, primitive, let it slide
            if (wrapperType.isAssignableFrom(value.getClass())) {
                return;
            }
        }
        throw new JsonMappingException("Incompatible types: declared root type ("+rootType+") vs "
                +value.getClass().getName());
    }
    
    /**
     * Method that will try to find a serializer, either from cache
     * or by constructing one; but will not return an "unknown" serializer
     * if this can not be done but rather returns null.
     *
     * @return Serializer if one can be found, null if not.
     */
    protected JsonSerializer<Object> _findExplicitUntypedSerializer(Class<?> runtimeType)
    {        
        // Fast lookup from local lookup thingy works?
        JsonSerializer<Object> ser = _knownSerializers.untypedValueSerializer(runtimeType);
        if (ser != null) {
            return ser;
        }
        // If not, maybe shared map already has it?
        ser = _serializerCache.untypedValueSerializer(runtimeType);
        if (ser != null) {
            return ser;
        }
        try {
            return _createAndCacheUntypedSerializer(runtimeType);
        } catch (Exception e) {
            return null;
        }
    }

    /*
    /**********************************************************
    /* Low-level methods for actually constructing and initializing
    /* serializers
    /**********************************************************
     */
    
    /**
     * Method that will try to construct a value serializer; and if
     * one is successfully created, cache it for reuse.
     */
    protected JsonSerializer<Object> _createAndCacheUntypedSerializer(Class<?> type)
        throws JsonMappingException
    {        
        JsonSerializer<Object> ser;
        try {
            ser = _createUntypedSerializer(_config.constructType(type));
        } catch (IllegalArgumentException iae) {
            /* We better only expose checked exceptions, since those
             * are what caller is expected to handle
             */
            throw new JsonMappingException(iae.getMessage(), null, iae);
        }

        if (ser != null) {
            _serializerCache.addAndResolveNonTypedSerializer(type, ser, this);
        }
        return ser;
    }

    protected JsonSerializer<Object> _createAndCacheUntypedSerializer(JavaType type)
        throws JsonMappingException
    {        
        JsonSerializer<Object> ser;
        try {
            ser = _createUntypedSerializer(type);
        } catch (IllegalArgumentException iae) {
            /* We better only expose checked exceptions, since those
             * are what caller is expected to handle
             */
            throw new JsonMappingException(iae.getMessage(), null, iae);
        }
    
        if (ser != null) {
            _serializerCache.addAndResolveNonTypedSerializer(type, ser, this);
        }
        return ser;
    }

    /**
     * @since 2.1
     */
    @SuppressWarnings("deprecation")
    protected JsonSerializer<Object> _createUntypedSerializer(JavaType type)
        throws JsonMappingException
    {
        /* 30-Sep-2012, tatu: For now need to call the deprecated method; for 2.2,
         *   convert to using new one. But note that we do NOT pass 'property' along.
         */
        return (JsonSerializer<Object>)_serializerFactory.createSerializer(this, type, null);
    }

    /**
     * Helper method called to resolve and contextualize given
     * serializer, if and as necessary.
     */
    protected JsonSerializer<Object> _handleContextualResolvable(JsonSerializer<?> ser,
            BeanProperty property)
        throws JsonMappingException
    {
        if (ser instanceof ResolvableSerializer) {
            ((ResolvableSerializer) ser).resolve(this);
        }
        return _handleContextual(ser, property);
    }

    @SuppressWarnings("unchecked")
    protected JsonSerializer<Object> _handleResolvable(JsonSerializer<?> ser)
        throws JsonMappingException
    {
        if (ser instanceof ResolvableSerializer) {
            ((ResolvableSerializer) ser).resolve(this);
        }
        return (JsonSerializer<Object>) ser;
    }
    
    @SuppressWarnings("unchecked")
    protected JsonSerializer<Object> _handleContextual(JsonSerializer<?> ser,
            BeanProperty property)
        throws JsonMappingException
    {
        if (ser instanceof ContextualSerializer) {
            ser = ((ContextualSerializer) ser).createContextual(this, property);
        }
        return (JsonSerializer<Object>) ser;
    }

    /*
    /**********************************************************
    /* Internal methods
    /**********************************************************
     */

    protected final DateFormat _dateFormat()
    {
        if (_dateFormat != null) {
            return _dateFormat;
        }
        /* 24-Feb-2012, tatu: At this point, all timezone configuration
         *    should have occured, with respect to default dateformat
         *    and timezone configuration. But we still better clone
         *    an instance as formatters may be stateful.
         */
        DateFormat df = _config.getDateFormat();
        _dateFormat = df = (DateFormat) df.clone();
        return df;
    }
}
