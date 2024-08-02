package com.ssd.mvd.interfaces;

import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.constants.Errors;
import com.ssd.mvd.inspectors.StringOperations;

public interface EntityCommonMethods<T> {
    T generate (
            final ErrorResponse errorResponse
    );

    T generate (
            final String message,
            final Errors errors
    );

    EntityCommonMethods<T> generate ();

    default Methods getMethodName () {
        return Methods.CAR_TOTAL_DATA;
    }

    default String getMethodApi() {
        return StringOperations.EMPTY;
    }

    default T generate (
            final String response
    ) {
        return null;
    }
}
