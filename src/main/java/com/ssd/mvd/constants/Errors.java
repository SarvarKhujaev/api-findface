package com.ssd.mvd.constants;

public enum Errors {
    WRONG_PARAMS,
    DATA_NOT_FOUND,
    GAI_TOKEN_ERROR, // used when GAI token is invalid,
    SERVICE_WORK_ERROR,
    TOO_MANY_RETRIES_ERROR, // used when service is unavailable after 3 retries
    EXTERNAL_SERVICE_500_ERROR, // used when some service returns an error,
    RESPONSE_FROM_SERVICE_NOT_RECEIVED,
}
