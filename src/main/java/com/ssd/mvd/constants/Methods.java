package com.ssd.mvd.constants;

import com.ssd.mvd.inspectors.Config;

public enum Methods {
    CADASTER,
    GET_PINPP {
        @Override
        @lombok.NonNull
        public String getMethodApi() {
            return Config.getAPI_FOR_PINPP();
        }
    },
    GET_CROSS_BOARDING {
        @Override
        @lombok.NonNull
        public String getMethodApi() {
            return Config.getAPI_FOR_BOARD_CROSSING();
        }
    },
    GET_MODEL_FOR_ADDRESS {
        @Override
        @lombok.NonNull
        public String getMethodApi() {
            return Config.getAPI_FOR_MODEL_FOR_ADDRESS();
        }
    },
    GET_MODEL_FOR_PASSPORT {
        @Override
        @lombok.NonNull
        public String getMethodApi() {
            return Config.getAPI_FOR_PASSPORT_MODEL();
        }
    },

    GET_PSYCHOLOGY_CARD,
    CAR_TOTAL_DATA,

    UPDATE_TOKENS,
    GET_VIOLATION_LIST {
        @Override
        @lombok.NonNull
        public String getMethodApi() {
            return Config.getAPI_FOR_VIOLATION_LIST();
        }
    },
    CONVERT_BASE64_TO_LINK,

    GET_INSURANCE {
        @Override
        @lombok.NonNull
        public String getMethodApi() {
            return Config.getAPI_FOR_FOR_INSURANCE();
        }
    },
    GET_TONIROVKA {
        @Override
        @lombok.NonNull
        public String getMethodApi() {
            return Config.getAPI_FOR_TONIROVKA();
        }
    },
    GET_VEHILE_DATA,
    GET_DOVERENNOST_LIST {
        @Override
        @lombok.NonNull
        public String getMethodApi() {
            return Config.getAPI_FOR_DOVERENNOST_LIST();
        }
    },
    GET_MODEL_FOR_CAR_LIST {
        @Override
        @lombok.NonNull
        public String getMethodApi() {
            return Config.getAPI_FOR_MODEL_FOR_CAR_LIST();
        }
    },

    // for RequestController
    GET_PERSON_TOTAL_DATA_BY_FIO,
    GET_PERSONAL_CADASTOR_INITIAL,

    // for Popilon service
    GET_FACE_CARD,
    GET_VIOLATION_LIST_BY_PINFL;

    @lombok.NonNull
    public String getMethodApi() {
        return Config.getAPI_FOR_CADASTR();
    }
}
