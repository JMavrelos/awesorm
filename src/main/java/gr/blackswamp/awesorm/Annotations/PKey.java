package gr.blackswamp.awesorm.Annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PKey {
    String value() default "";

    int length() default -1;
}
