package com.fasterxml.jackson.databind.module;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.Version;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.jsontype.NamedType;

/**
 * Simple {@link Module} implementation that allows registration
 * of serializers and deserializers, and bean serializer
 * and deserializer modifiers.
 */
public class SimpleModule
    extends Module
    implements java.io.Serializable
{
    private static final long serialVersionUID = 3132264350026957446L;

    protected final String _name;
    protected final Version _version;
    
    protected SimpleSerializers _serializers = null;
    protected SimpleDeserializers _deserializers = null;

    protected SimpleSerializers _keySerializers = null;
    protected SimpleKeyDeserializers _keyDeserializers = null;

    /**
     * Lazily-constructed resolver used for storing mappings from
     * abstract classes to more specific implementing classes
     * (which may be abstract or concrete)
     */
    protected SimpleAbstractTypeResolver _abstractTypes = null;

    /**
     * Lazily-constructed resolver used for storing mappings from
     * abstract classes to more specific implementing classes
     * (which may be abstract or concrete)
     */
    protected SimpleValueInstantiators _valueInstantiators = null;

    /**
     * Lazily-constructed map that contains mix-in definitions, indexed
     * by target class, value being mix-in to apply.
     */
    protected HashMap<Class<?>, Class<?>> _mixins = null;
    
    /**
     * Set of subtypes to register, if any.
     */
    protected LinkedHashSet<NamedType> _subtypes = null;
    
    /*
    /**********************************************************
    /* Life-cycle: creation
    /**********************************************************
     */

    /**
     * Constructors that should only be used for non-reusable
     * convenience modules used by app code: "real" modules should
     * use actual name and version number information.
     */
    public SimpleModule() {
        // when passing 'this', can not chain constructors...
        _name = "SimpleModule-"+System.identityHashCode(this);
        _version = Version.unknownVersion();
    }
    
    /**
     * Convenience constructor that will default version to
     * {@link Version#unknownVersion()}.
     */
    public SimpleModule(String name) {
        this(name, Version.unknownVersion());
    }

    /**
     * Convenience constructor that will use specified Version,
     * including name from {@link Version#getArtifactId()}
     */
    public SimpleModule(Version version) {
        _name = version.getArtifactId();
        _version = version;
    }
    
    /**
     * Constructor to use for actual reusable modules.
     * ObjectMapper may use name as identifier to notice attempts
     * for multiple registrations of the same module (although it
     * does not have to).
     * 
     * @param name Unique name of the module
     * @param version Version of the module
     */
    public SimpleModule(String name, Version version) {
        _name = name;
        _version = version;
    }

    /**
     * @since 2.1
     */
    public SimpleModule(String name, Version version,
            Map<Class<?>,JsonDeserializer<?>> deserializers) {
        this(name, version, deserializers, null);
    }

    /**
     * @since 2.1
     */
    public SimpleModule(String name, Version version,
            List<JsonSerializer<?>> serializers) {
        this(name, version, null, serializers);
    }
    
    /**
     * @since 2.1
     */
    public SimpleModule(String name, Version version,
            Map<Class<?>,JsonDeserializer<?>> deserializers,
            List<JsonSerializer<?>> serializers)
    {
        _name = name;
        _version = version;
        if (deserializers != null) {
            _deserializers = new SimpleDeserializers(deserializers);
        }
        if (serializers != null) {
            _serializers = new SimpleSerializers(serializers);
        }
    }
    
    /*
    /**********************************************************
    /* Simple setters to allow overriding
    /**********************************************************
     */

    /**
     * Resets all currently configured serializers.
     */
    public void setSerializers(SimpleSerializers s) {
        _serializers = s;
    }

    /**
     * Resets all currently configured deserializers.
     */
    public void setDeserializers(SimpleDeserializers d) {
        _deserializers = d;
    }

    /**
     * Resets all currently configured key serializers.
     */
    public void setKeySerializers(SimpleSerializers ks) {
        _keySerializers = ks;
    }

    /**
     * Resets all currently configured key deserializers.
     */
    public void setKeyDeserializers(SimpleKeyDeserializers kd) {
        _keyDeserializers = kd;
    }

    /**
     * Resets currently configured abstract type mappings
     */
    public void setAbstractTypes(SimpleAbstractTypeResolver atr) {
        _abstractTypes = atr;        
    }

    /**
     * Resets all currently configured value instantiators
     */
    public void setValueInstantiators(SimpleValueInstantiators svi) {
        _valueInstantiators = svi;
    }
    
    /*
    /**********************************************************
    /* Configuration methods
    /**********************************************************
     */
    
    public SimpleModule addSerializer(JsonSerializer<?> ser)
    {
        if (_serializers == null) {
            _serializers = new SimpleSerializers();
        }
        _serializers.addSerializer(ser);
        return this;
    }
    
    public <T> SimpleModule addSerializer(Class<? extends T> type, JsonSerializer<T> ser)
    {
        if (_serializers == null) {
            _serializers = new SimpleSerializers();
        }
        _serializers.addSerializer(type, ser);
        return this;
    }

    public <T> SimpleModule addKeySerializer(Class<? extends T> type, JsonSerializer<T> ser)
    {
        if (_keySerializers == null) {
            _keySerializers = new SimpleSerializers();
        }
        _keySerializers.addSerializer(type, ser);
        return this;
    }
    
    public <T> SimpleModule addDeserializer(Class<T> type, JsonDeserializer<? extends T> deser)
    {
        if (_deserializers == null) {
            _deserializers = new SimpleDeserializers();
        }
        _deserializers.addDeserializer(type, deser);
        return this;
    }

    public SimpleModule addKeyDeserializer(Class<?> type, KeyDeserializer deser)
    {
        if (_keyDeserializers == null) {
            _keyDeserializers = new SimpleKeyDeserializers();
        }
        _keyDeserializers.addDeserializer(type, deser);
        return this;
    }

    /**
     * Lazily-constructed resolver used for storing mappings from
     * abstract classes to more specific implementing classes
     * (which may be abstract or concrete)
     */
    public <T> SimpleModule addAbstractTypeMapping(Class<T> superType,
            Class<? extends T> subType)
    {
        if (_abstractTypes == null) {
            _abstractTypes = new SimpleAbstractTypeResolver();
        }
        // note: addMapping() will verify arguments
        _abstractTypes = _abstractTypes.addMapping(superType, subType);
        return this;
    }

    /**
     * Method for registering {@link ValueInstantiator} to use when deserializing
     * instances of type <code>beanType</code>.
     *<p>
     * Instantiator is
     * registered when module is registered for <code>ObjectMapper</code>.
     */
    public SimpleModule addValueInstantiator(Class<?> beanType, ValueInstantiator inst)
    {
        if (_valueInstantiators == null) {
            _valueInstantiators = new SimpleValueInstantiators();
        }
        _valueInstantiators = _valueInstantiators.addValueInstantiator(beanType, inst);
        return this;
    }

    /**
     * Method for adding set of subtypes to be registered with
     * {@link ObjectMapper}
     * this is an alternative to using annotations in super type to indicate subtypes.
     */
    public SimpleModule registerSubtypes(Class<?> ... subtypes)
    {
        if (_subtypes == null) {
            _subtypes = new LinkedHashSet<NamedType>(Math.max(16, subtypes.length));
        }
        for (Class<?> subtype : subtypes) {
            _subtypes.add(new NamedType(subtype));
        }
        return this;
    }

    /**
     * Method for adding set of subtypes (along with type name to use) to be registered with
     * {@link ObjectMapper}
     * this is an alternative to using annotations in super type to indicate subtypes.
     */
    public SimpleModule registerSubtypes(NamedType ... subtypes)
    {
        if (_subtypes == null) {
            _subtypes = new LinkedHashSet<NamedType>(Math.max(16, subtypes.length));
        }
        for (NamedType subtype : subtypes) {
            _subtypes.add(subtype);
        }
        return this;
    }
    
    /**
     * Method for specifying that annotations define by <code>mixinClass</code>
     * should be "mixed in" with annotations that <code>targetType</code>
     * has (as if they were directly included on it!).
     *<p>
     * Mix-in annotations are
     * registered when module is registered for <code>ObjectMapper</code>.
     */
    public SimpleModule setMixInAnnotation(Class<?> targetType, Class<?> mixinClass)
    {
        if (_mixins == null) {
            _mixins = new HashMap<Class<?>, Class<?>>();
        }
        _mixins.put(targetType, mixinClass);
        return this;
    }
    
    /*
    /**********************************************************
    /* Module impl
    /**********************************************************
     */
    
    @Override
    public String getModuleName() {
        return _name;
    }

    @Override
    public void setupModule(SetupContext context)
    {
        if (_serializers != null) {
            context.addSerializers(_serializers);
        }
        if (_deserializers != null) {
            context.addDeserializers(_deserializers);
        }
        if (_keySerializers != null) {
            context.addKeySerializers(_keySerializers);
        }
        if (_keyDeserializers != null) {
            context.addKeyDeserializers(_keyDeserializers);
        }
        if (_abstractTypes != null) {
            context.addAbstractTypeResolver(_abstractTypes);
        }
        if (_valueInstantiators != null) {
            context.addValueInstantiators(_valueInstantiators);
        }
        if (_subtypes != null && _subtypes.size() > 0) {
            context.registerSubtypes(_subtypes.toArray(new NamedType[_subtypes.size()]));
        }
        if (_mixins != null) {
            for (Map.Entry<Class<?>,Class<?>> entry : _mixins.entrySet()) {
                context.setMixInAnnotations(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public Version version() {
        return _version;
    }
}
