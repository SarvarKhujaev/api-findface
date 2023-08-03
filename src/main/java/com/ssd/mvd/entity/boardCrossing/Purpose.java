package com.ssd.mvd.entity.boardCrossing;

import com.ssd.mvd.constants.Errors;
import com.ssd.mvd.controller.Archieve;
import com.ssd.mvd.controller.DataValidationInspector;

@lombok.Data
public final class Purpose {
    private String periods;
    private String countries;
    private String tripPurpose;
    private String documentType;
    private String nationalities;
    private String transportCategory;

    public Purpose ( final CrossBoard crossBoard,
                     final Integer nationalityId,
                     final DataValidationInspector dataValidationInspector ) {
        if ( dataValidationInspector
                .checkObject
                .test( crossBoard.getTrip_purpose_code() ) )
            this.setTripPurpose( Archieve
                    .getInstance()
                    .tripPurposes
                    .getOrDefault( crossBoard.getTrip_purpose_code(), Errors.DATA_NOT_FOUND.name() ) );

        if ( dataValidationInspector
                .checkObject
                .test( crossBoard.getTrans_category_code() ) )
            this.setTransportCategory( Archieve
                    .getInstance()
                    .transportCategory
                    .getOrDefault( crossBoard.getTrans_category_code(), Errors.DATA_NOT_FOUND.name() ) );

        if ( dataValidationInspector
                .checkObject
                .test( crossBoard.getPeriod_code() ) )
            this.setPeriods( Archieve
                    .getInstance()
                    .periods
                    .getOrDefault( crossBoard.getPeriod_code(), Errors.DATA_NOT_FOUND.name() ) );

        if ( dataValidationInspector
                .checkObject
                .test( crossBoard.getDocument_type_code() ) )
            this.setDocumentType( Archieve
                    .getInstance()
                    .documentTypes
                    .getOrDefault( crossBoard.getDocument_type_code(), Errors.DATA_NOT_FOUND.name() ) );

        if ( dataValidationInspector
                .checkObject
                .test( crossBoard.getDirection_country() ) )
            this.setCountries( Archieve
                    .getInstance()
                    .countries
                    .getOrDefault( crossBoard.getDirection_country(), Errors.DATA_NOT_FOUND.name() ) );

        this.setNationalities( Archieve
                .getInstance()
                .nationalities
                .getOrDefault( nationalityId, Errors.DATA_NOT_FOUND.name() ) ); }
}
