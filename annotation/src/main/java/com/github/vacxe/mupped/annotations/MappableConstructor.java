package com.github.vacxe.mupped.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value= ElementType.CONSTRUCTOR)
@Retention(value= RetentionPolicy.SOURCE)
public @interface MappableConstructor {
}
