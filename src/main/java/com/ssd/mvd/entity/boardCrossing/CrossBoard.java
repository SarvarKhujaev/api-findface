package com.ssd.mvd.entity.boardCrossing;

import com.ssd.mvd.controller.LogInspector;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class CrossBoard extends LogInspector {
    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate( final Date registrationDate ) {
        this.registrationDate = registrationDate;
    }

    public Integer getPeriod_code() {
        return period_code;
    }

    public Integer getTrip_purpose_code() {
        return trip_purpose_code;
    }

    public Integer getDirection_country() {
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

    private Long card_id;
    private Date registrationDate;

    private Integer period_code;
    private Integer citizenship;
    private Integer trip_purpose_code;
    private Integer direction_country;

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

    public CrossBoard save ( final Integer nationalityId ) {
        try {
            this.setRegistrationDate( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).parse( this.getReg_date() ) );
        } catch ( final Exception e ) {
            super.logging( e );
        }

        this.setDirection_type_code( this.getDirection_type_code().equals( "P" ) ? "въезд" : "выезд" );
        this.setPurpose( new Purpose( this, nationalityId ) );
        return this;
    }
}
