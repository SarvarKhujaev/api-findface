package com.ssd.mvd.annotations;

import com.ssd.mvd.inspectors.EntitiesInstances;
import java.lang.annotation.*;

@Target( value = ElementType.CONSTRUCTOR )
@Retention( value = RetentionPolicy.RUNTIME )
@Documented
public @interface EntityConstructorAnnotation {
    Class<?>[] permission() default EntitiesInstances.class;
}
