package com.ssd.mvd.constants;

import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.entity.modelForCadastr.Data;
import com.ssd.mvd.entity.ModelForCarList;
import com.ssd.mvd.entity.modelForGai.*;
import com.ssd.mvd.entity.Pinpp;

public enum Methods {
    CADASTER {
        public EntityCommonMethods<?> getEntityWithError (
                final ErrorResponse errorResponse
        ) {
            return new Data().generate( errorResponse );
        }
    },
    GET_PINPP {
        public EntityCommonMethods<?> getEntityWithError (
                final ErrorResponse errorResponse
        ) {
            return new Pinpp().generate( errorResponse );
        }
    },
    GET_IMAGE_BY_PINFL,
    GET_CROSS_BOARDING,
    GET_MODEL_FOR_ADDRESS {
        public EntityCommonMethods<?> getEntityWithError (
                final ErrorResponse errorResponse
        ) {
            return new ModelForAddress().generate( errorResponse );
        }
    },
    GET_MODEL_FOR_PASSPORT {
        public EntityCommonMethods<?> getEntityWithError (
                final ErrorResponse errorResponse
        ) {
            return new com.ssd.mvd.entity.modelForPassport.ModelForPassport().generate( errorResponse );
        }
    },

    GET_PSYCHOLOGY_CARD,
    CAR_TOTAL_DATA,

    UPDATE_TOKENS,
    GET_VIOLATION_LIST {
        public EntityCommonMethods<?> getEntityWithError (
                final ErrorResponse errorResponse
        ) {
            return new ViolationsList().generate( errorResponse );
        }
    },
    CONVERT_BASE64_TO_LINK,

    GET_INSURANCE {
        public EntityCommonMethods<?> getEntityWithError (
                final ErrorResponse errorResponse
        ) {
            return new Insurance().generate( errorResponse );
        }
    },
    GET_TONIROVKA {
        public EntityCommonMethods<?> getEntityWithError (
                final ErrorResponse errorResponse
        ) {
            return new Tonirovka().generate( errorResponse );
        }
    },
    GET_VEHILE_DATA {
        public EntityCommonMethods<?> getEntityWithError (
                final ErrorResponse errorResponse
        ) {
            return new ModelForCar().generate( errorResponse );
        }
    },
    GET_DOVERENNOST_LIST {
        public EntityCommonMethods<?> getEntityWithError (
                final ErrorResponse errorResponse
        ) {
            return new DoverennostList().generate( errorResponse );
        }
    },
    GET_MODEL_FOR_CAR_LIST,

    // for RequestController
    GET_PERSON_TOTAL_DATA_BY_FIO,

    // for Popilon service
    GET_FACE_CARD,
    GET_VIOLATION_LIST_BY_PINFL;


    public EntityCommonMethods<?> getEntityWithError (
            final ErrorResponse errorResponse
    ) {
        return new ModelForCarList().generate( errorResponse );
    }
}
