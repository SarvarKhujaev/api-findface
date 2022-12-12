package com.ssd.mvd.constants;

public enum Errors {
    ERROR_500, // used when some service returns an error
    WRONG_PARAMS,
    NOT_AVAILABLE, // used when some service is unavailable,
    DATA_NOT_FOUND,
    GAI_TOKEN_ERROR, // used when GAI token is invalid,
    SERVICE_WORK_ERROR,
}
