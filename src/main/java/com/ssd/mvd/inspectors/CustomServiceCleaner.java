package com.ssd.mvd.inspectors;

import java.lang.ref.WeakReference;

public final class CustomServiceCleaner {
    @lombok.Synchronized
    public static synchronized <T> void clearReference ( @lombok.NonNull final WeakReference< T > reference ) {
        reference.enqueue();
        reference.clear();
    }
}
