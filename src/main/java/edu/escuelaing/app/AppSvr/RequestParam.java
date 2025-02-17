package edu.escuelaing.app.AppSvr;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)

public @interface RequestParam {
    String value() default "";
    String defaultValue() default "";
    boolean required() default true;
}
