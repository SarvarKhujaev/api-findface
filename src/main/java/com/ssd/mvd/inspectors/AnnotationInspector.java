package com.ssd.mvd.inspectors;

import com.ssd.mvd.annotations.ImmutableEntityAnnotation;

@ImmutableEntityAnnotation
public class AnnotationInspector extends LogInspector {
    @SuppressWarnings(
            value = """
                    Принимает любой Object и проверяет не является ли он Immutable
                    если все хорошо, то возвращает сам Object
                    """
    )
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> fail" )
    protected static synchronized < T > T checkAnnotationIsNotImmutable (
            @lombok.NonNull final T object
    ) {
        if ( object.getClass().isAnnotationPresent( ImmutableEntityAnnotation.class ) ) {
            throw new IllegalArgumentException();
        }

        return object;
    }
}
