package com.ssd.mvd.annotations;

import com.ssd.mvd.inspectors.StringOperations;
import java.lang.annotation.*;

@Target( value = ElementType.TYPE )
@Retention( value = RetentionPolicy.RUNTIME )
@Documented
public @interface EntityAnnotations {
    String name();
    String comment() default StringOperations.EMPTY;

    boolean canTouch() default true;
    boolean isReadable() default true;
    boolean isSubClass() default false;
    boolean checkExistence() default false;

    String[] primaryKeys() default { "uuid" };
    String[] clusteringKeys() default {};
}
