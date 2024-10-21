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
    OBJECT_IS_IMMUTABLE {
        @Override
        public String getErrorMEssage (
                final String error
        ) {
            return String.join(
                    StringOperations.SPACE,
                    "Entity: ",
                    error,
                    "is immutable"
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
    },

    OBJECT_IS_OUT_OF_INSTANCE_PERMISSION {
        @Override
        @lombok.NonNull
        @org.jetbrains.annotations.Contract( value = "_, _ -> _" )
        public String translate (
        @lombok.NonNull final String languageType,
        @lombok.NonNull final String entityName
        ) {
            return String.join(
                    StringOperations.SPACE,
                    "Object:",
                    languageType,
                    "is out of permission list of:",
                    entityName
            );
        }
    },

    FIELD_AND_ANNOTATION_NAME_MISMATCH {
        @Override
        @lombok.NonNull
        @org.jetbrains.annotations.Contract( value = "_, _, _ -> _" )
        public String translate (
                @lombok.NonNull final String entityName,
                @lombok.NonNull final String fieldName,
                @lombok.NonNull final String annotationName
        ) {
            return String.join(
                    StringOperations.SPACE,
                    "Entity:",
                    entityName,
                    "with field:",
                    fieldName,
                    "is not the same as:",
                    annotationName
            );
        }
    };

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_, _, _, _, _ -> _" )
    public String translate (
            @lombok.NonNull final String languageType,
            @lombok.NonNull final String fieldName,
            @lombok.NonNull final String entityName,
            final int paramsQuantityReceived,
            final int paramsQuantityExpected
    ) {
        return switch ( languageType ) {
            case "uz" -> "Collection uchun notog'ri parametrla berilgan %s %s %d %d".formatted(
                    fieldName,
                    entityName,
                    paramsQuantityReceived,
                    paramsQuantityExpected
            );
            case "ru" -> "Для коллекции %s параметра %s указано неверное количество типов, получено %d ожидаемо %d".formatted(
                    fieldName,
                    entityName,
                    paramsQuantityReceived,
                    paramsQuantityExpected
            );
            default -> "Wrong number of types for collection %s for field %s, received %d : expected %d".formatted(
                    fieldName,
                    entityName,
                    paramsQuantityReceived,
                    paramsQuantityExpected
            );
        };
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_, _, _ -> _" )
    public String translate (
            @lombok.NonNull final String languageType,
            @lombok.NonNull final String fieldName,
            @lombok.NonNull final String entityName
    ) {
        return switch ( languageType ) {
            case "uz" -> "Mumkin emas %s %s".formatted( fieldName, entityName );
            case "ru" -> "Формат null не допустим для %s в сущности: %s".formatted( fieldName, entityName );
            default -> "Type %s for %s CANNOT BE NULL".formatted( fieldName, entityName );
        };
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_, _ -> _" )
    public String translate (
            @lombok.NonNull final String languageType,
            @lombok.NonNull final String entityName
    ) {
        return switch ( languageType ) {
            case "uz" -> "Mumkin emas %s".formatted( entityName );
            case "ru" -> "Класс %s не имеет нужной аннотации ServiceParametrAnnotation".formatted( entityName );
            default -> "Type %s is unacceptable for ServiceParametrAnnotation annotation".formatted( entityName );
        };
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    public String translate (
            @lombok.NonNull final String languageType
    ) {
        return switch ( languageType ) {
            case "ru", "uz" -> StringOperations.EMPTY;
            default -> DATA_NOT_FOUND.name();
        };
    }

    public String getErrorMEssage (
            final String error
    ) {
        return "Error in external service: ";
    }
}
