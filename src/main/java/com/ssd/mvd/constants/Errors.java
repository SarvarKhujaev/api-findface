package com.ssd.mvd.constants;

import com.ssd.mvd.inspectors.StringOperations;

public enum Errors {
    WRONG_PARAMS {
        @Override
        public String getErrorMEssage (
                final String error
        ) {
            return String.join(
                    StringOperations.SPACE,
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
                    StringOperations.SPACE,
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
                    StringOperations.SPACE,
                    "GAI token is unavailable",
                    error
            );
        }
    },
    SERVICE_WORK_ERROR {
        @Override
        public String getErrorMEssage (
                final String error
        ) {
            return String.join(
                    StringOperations.SPACE,
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
                    StringOperations.SPACE,
                    error
            );
        }
    },
    EXTERNAL_SERVICE_500_ERROR {
        @Override
        public String getErrorMEssage (
                final String error
        ) {
            return String.join(
                    StringOperations.SPACE,
                    "Error in external service: ",
                    error
            );
        }
    },
    RESPONSE_FROM_SERVICE_NOT_RECEIVED {
        @Override
        public String getErrorMEssage (
                final String error
        ) {
            return String.join(
                    StringOperations.SPACE,
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
