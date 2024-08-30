package com.ssd.mvd.interfaces;

import com.ssd.mvd.inspectors.StringOperations;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.constants.Errors;

public interface EntityCommonMethods<T extends StringOperations> {
    @lombok.NonNull
    T generate ( @lombok.NonNull final ErrorResponse errorResponse );

    @lombok.NonNull
    T generate (
            @lombok.NonNull final String message,
            @lombok.NonNull final Errors errors
    );

    @lombok.NonNull
    EntityCommonMethods<T> generate ();

    @lombok.NonNull
    T setErrorResponse( @lombok.NonNull final ErrorResponse errorResponse );

    @lombok.NonNull
    default Methods getMethodName () {
        return Methods.CAR_TOTAL_DATA;
    }

    @lombok.NonNull
    default String getMethodApi() {
        return StringOperations.EMPTY;
    }

    default T generate (
            @lombok.NonNull final String response
    ) {
        return null;
    }
}
