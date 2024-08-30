package com.ssd.mvd.annotations;

import com.ssd.mvd.inspectors.StringOperations;
import java.lang.annotation.*;

@Target( value = ElementType.FIELD )
@Retention( value = RetentionPolicy.RUNTIME )
@Documented
public @interface FieldAnnotation {
    String name();
    String comment() default StringOperations.EMPTY;

    boolean canTouch() default true;
    boolean isReadable() default true;
    boolean mightBeNull() default true;
    boolean isInteriorObject() default false;
    boolean hasToBeJoinedWithAstrix() default false;
}
