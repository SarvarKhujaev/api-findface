package com.ssd.mvd.interfaces;

import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.constants.Errors;

public interface EntityCommonMethods<T> {
    T generate (
            final ErrorResponse errorResponse
    );

    T generate (
            final String message,
            final Errors errors
    );

    default EntityCommonMethods<T> generate () {
        return null;
    }

    default Methods getMethodName () {
        return Methods.GET_CROSS_BOARDING;
    }
}
