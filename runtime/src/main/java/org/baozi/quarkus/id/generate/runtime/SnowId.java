package org.baozi.quarkus.id.generate.runtime;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IdGeneratorType(SnowIdGenerator.class)
public @interface SnowId {
    String grpcServiceName() default "id-generator";
}
