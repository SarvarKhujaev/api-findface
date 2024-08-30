package com.ssd.mvd.annotations;

import java.lang.annotation.*;

@Target( value = ElementType.METHOD )
@Retention( value = RetentionPolicy.RUNTIME )
@Documented
public @interface MethodsAnnotations {
    String name();

    boolean canTouch() default true;
    boolean isPrimaryKey() default false;
    boolean withoutParams() default true;
    boolean isReturnEntity() default true;
}
