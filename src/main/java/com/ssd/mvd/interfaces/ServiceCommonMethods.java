package com.ssd.mvd.interfaces;

public interface ServiceCommonMethods {
    default void close( final Throwable throwable ) {}

    void close();

    default void clean() {
        System.gc();
    }
}
