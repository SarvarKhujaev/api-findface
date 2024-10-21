package com.ssd.mvd.annotations;

import java.lang.annotation.*;

@Target( value = ElementType.FIELD )
@Retention( value = RetentionPolicy.RUNTIME )
@Documented
@SuppressWarnings(
        value = """
                применяется для того, чтобы отметить параметры классов
                которые должны быть очищены после отработки класса
                """
)
public @interface WeakReferenceAnnotation {
    String name();

    @SuppressWarnings( value = "показывает является ли параметр Map" )
    boolean isMap() default false;
    @SuppressWarnings( value = "показывает является ли параметр WeakReference" )
    boolean isWeak() default false;
    @SuppressWarnings( value = "показывает является ли параметр коллекцией" )
    boolean isCollection() default true;
}
