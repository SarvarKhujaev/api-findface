package com.ssd.mvd.interfaces;

import com.ssd.mvd.constants.ErrorResponse;

public interface EntityCommonMethods<T> {
    T generate (
            final ErrorResponse errorResponse
    );
}
