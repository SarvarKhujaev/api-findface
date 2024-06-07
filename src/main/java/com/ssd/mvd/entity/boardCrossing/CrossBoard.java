package com.ssd.mvd.entity.boardCrossing;

import com.ssd.mvd.controller.LogInspector;
import java.util.Date;

public final class CrossBoard extends LogInspector {
    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate( final Date registrationDate ) {
        this.registrationDate = registrationDate;
    }

    public int getPeriod_code() {
        return period_code;
    }

    public int getTrip_purpose_code() {
        return trip_purpose_code;
    }

    public int getDirection_country() {
        return direction_country;
    }

    public String getPinpp() {
        return pinpp;
    }

    public void setPinpp( final String pinpp ) {
        this.pinpp = pinpp;
    }

    public String getReg_date() {
        return reg_date;
    }

    public String getDocument_type_code() {
        return document_type_code;
    }

    public String getDirection_type_code() {
        return direction_type_code;
    }

    public void setDirection_type_code( final String direction_type_code ) {
        this.direction_type_code = direction_type_code;
    }

    public String getTrans_category_code() {
        return trans_category_code;
    }

    public void setPurpose( final Purpose purpose ) {
        this.purpose = purpose;
    }

    private long card_id;
    private Date registrationDate;

    private int period_code;
    private int citizenship;
    private int trip_purpose_code;
    private int direction_country;

    private String pinpp;
    private String reg_date;
    private String document;
    private String full_name;
    private String birth_date;
    private String point_code;
    private String visa_number;
    private String nationality;
    private String trans_number;
    private String trans_add_info;
    private String date_end_document;
    private String document_type_code;
    private String direction_type_code;
    private String trans_category_code;

    private Purpose purpose;

    public CrossBoard save ( final int nationalityId ) {
        try {
            this.setRegistrationDate( super.parseStringIntoDate( this.getReg_date() ) );
        } catch ( final Exception e ) {
            super.logging( e );
        }

        this.setDirection_type_code( this.getDirection_type_code().equals( "P" ) ? "въезд" : "выезд" );
        this.setPurpose( new Purpose( this, nationalityId ) );
        return this;
    }
}
