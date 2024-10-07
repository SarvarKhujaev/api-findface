package com.ssd.mvd.interfaces;

import com.ssd.mvd.inspectors.ErrorController;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.constants.Errors;

public interface EntityCommonMethods<T> {
    @lombok.NonNull
    default T generate ( @lombok.NonNull final ErrorResponse errorResponse ) {
        return this.generate().setErrorResponse( errorResponse );
    }

    @lombok.NonNull
    default T generate (
            @lombok.NonNull final String message,
            @lombok.NonNull final Errors errors
    ) {
        return this.generate().setErrorResponse(
                ErrorController.error(
                        message,
                        errors
                )
        );
    }

    @lombok.NonNull
    EntityCommonMethods<T> generate ();

    @lombok.NonNull
    T setErrorResponse( @lombok.NonNull final ErrorResponse errorResponse );

    @lombok.NonNull
    default Methods getMethodName () {
        return Methods.CAR_TOTAL_DATA;
    }

    default T generate (
            @lombok.NonNull final String response
    ) {
        return null;
    }
}
