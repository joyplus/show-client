package com.fasterxml.jackson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation similar to {@link javax.xml.bind.annotation.XmlRootElement},
 * used to indicate name to use for root-level wrapping, if wrapping is
 * enabled. Annotation itself does not indicate that wrapping should
 * be used; but if it is, name used for serialization should be name
 * specified here, and deserializer will expect the name as well.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@com.fasterxml.jackson.annotation.JacksonAnnotation
public @interface JsonRootName
{
    /**
     * Root name to use if root-level wrapping is enabled.
     */
    public String value();

}
