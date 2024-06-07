package com.ssd.mvd.constants;

public enum Errors {
    WRONG_PARAMS {
        @Override
        public String getErrorMEssage (
                final String error
        ) {
            return String.join(
                    " ",
                    "wrong params were received",
                    error
            );
        }
    },
    DATA_NOT_FOUND {
        @Override
        public String getErrorMEssage (
                final String error
        ) {
            return String.join(
                    " ",
                    "Data for: ",
                    error,
                    " was not found"
            );
        }
    },
    GAI_TOKEN_ERROR {
        @Override
        public String getErrorMEssage (
                final String error
        ) {
            return String.join(
                    " ",
                    "GAI token is unavailable",
                    error
            );
        }
    }, // used when GAI token is invalid,
    SERVICE_WORK_ERROR {
        @Override
        public String getErrorMEssage (
                final String error
        ) {
            return String.join(
                    " ",
                    "Service: ",
                    error,
                    " does not return response!!!"
            );
        }
    },
    TOO_MANY_RETRIES_ERROR {
        @Override
        public String getErrorMEssage (
                final String error
        ) {
            return String.join(
                    " ",
                    error
            );
        }
    }, // used when service is unavailable after 3 retries
    EXTERNAL_SERVICE_500_ERROR {
        @Override
        public String getErrorMEssage (
                final String error
        ) {
            return String.join(
                    " ",
                    "Error in external service: ",
                    error
            );
        }
    }, // used when some service returns an error,
    RESPONSE_FROM_SERVICE_NOT_RECEIVED {
        @Override
        public String getErrorMEssage (
                final String error
        ) {
            return String.join(
                    " ",
                    "Service: ",
                    error,
                    " does not return response!!!"
            );
        }
    };

    public String getErrorMEssage (
            final String error
    ) {
        return "Error in external service: ";
    }
}
