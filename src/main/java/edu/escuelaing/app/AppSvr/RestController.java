package edu.escuelaing.app.AppSvr;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import edu.escuelaing.app.AppSvr.RestController;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface RestController {
}
