package gr.blackswamp.awesorm.Annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Indexed {
    int order() default -1;
}
