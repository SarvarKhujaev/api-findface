package com.ssd.mvd.interfaces;

public interface RequestCommonMethods< T, U > {
    @lombok.NonNull
    T generate ( @lombok.NonNull final U value );
}
