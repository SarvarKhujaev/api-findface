package com.ssd.mvd.inspectors;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import com.ssd.mvd.interfaces.ServiceCommonMethods;

import java.util.concurrent.atomic.AtomicReference;
import java.lang.ref.WeakReference;

public class CustomServiceCleaner extends CollectionsInspector {
    @EntityConstructorAnnotation( permission = AnnotationInspector.class )
    protected <T extends UuidInspector> CustomServiceCleaner ( @lombok.NonNull final Class<T> instance ) {
        super( CustomServiceCleaner.class );

        AnnotationInspector.checkCallerPermission( instance, CustomServiceCleaner.class );
        AnnotationInspector.checkAnnotationIsImmutable( CustomServiceCleaner.class );
    }

    @lombok.Synchronized
    protected final synchronized <T> void clearReference ( @lombok.NonNull final WeakReference< T > reference ) {
        reference.enqueue();
        reference.clear();
    }

    @lombok.Synchronized
    protected final synchronized <T extends ServiceCommonMethods> void clearReference (
            @lombok.NonNull final AtomicReference< T > reference
    ) {
        reference.get().close();
    }

    @lombok.Synchronized
    public static synchronized <T extends ServiceCommonMethods> void clearReference (
            @lombok.NonNull final T reference
    ) {
        reference.close();
    }
}
