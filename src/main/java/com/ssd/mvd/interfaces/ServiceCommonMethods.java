package com.ssd.mvd.interfaces;

public interface ServiceCommonMethods {
    default void close( @lombok.NonNull final Throwable throwable ) {}

    default void clean() {
        System.gc();
    }

    default void close() {}
}
