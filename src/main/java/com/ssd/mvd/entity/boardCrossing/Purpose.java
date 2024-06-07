package com.ssd.mvd.entity.boardCrossing;

import com.ssd.mvd.constants.Errors;
import com.ssd.mvd.controller.Archieve;

public final class Purpose extends Archieve {
    public void setPeriods ( final String periods ) {
        this.periods = periods;
    }

    public void setCountries( final String countries ) {
        this.countries = countries;
    }

    public void setTripPurpose( final String tripPurpose ) {
        this.tripPurpose = tripPurpose;
    }

    public void setDocumentType( final String documentType ) {
        this.documentType = documentType;
    }

    public void setNationalities( final String nationalities ) {
        this.nationalities = nationalities;
    }

    public void setTransportCategory( final String transportCategory ) {
        this.transportCategory = transportCategory;
    }

    private String periods;
    private String countries;
    private String tripPurpose;
    private String documentType;
    private String nationalities;
    private String transportCategory;

    public Purpose (
            final CrossBoard crossBoard,
            final Integer nationalityId ) {
        if ( super.objectIsNotNull( crossBoard.getTrip_purpose_code() ) ) {
            this.setTripPurpose(
                    super.tripPurposes.getOrDefault(
                            crossBoard.getTrip_purpose_code(),
                            Errors.DATA_NOT_FOUND.name()
                    )
            );
        }

        if ( super.objectIsNotNull( crossBoard.getTrans_category_code() ) ) {
            this.setTransportCategory(
                    super.transportCategory.getOrDefault(
                            crossBoard.getTrans_category_code(),
                            Errors.DATA_NOT_FOUND.name()
                    )
            );
        }

        if ( super.objectIsNotNull( crossBoard.getPeriod_code() ) ) {
            this.setPeriods(
                    super.periods.getOrDefault(
                            crossBoard.getPeriod_code(),
                            Errors.DATA_NOT_FOUND.name()
                    )
            );
        }

        if ( super.objectIsNotNull( crossBoard.getDocument_type_code() ) ) {
            this.setDocumentType(
                    super.documentTypes.getOrDefault(
                            crossBoard.getDocument_type_code(),
                            Errors.DATA_NOT_FOUND.name()
                    )
            );
        }

        if ( super.objectIsNotNull( crossBoard.getDirection_country() ) ) {
            this.setCountries(
                    super.countries.getOrDefault(
                            crossBoard.getDirection_country(),
                            Errors.DATA_NOT_FOUND.name()
                    )
            );
        }

        this.setNationalities(
                super.nationalities.getOrDefault(
                        nationalityId,
                        Errors.DATA_NOT_FOUND.name()
                )
        );
    }
}
