package com.ssd.mvd.entity.boardCrossing;

import com.ssd.mvd.inspectors.LogInspector;
import java.util.Date;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
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

    public long getCard_id() {
        return this.card_id;
    }

    public int getCitizenship() {
        return this.citizenship;
    }

    public String getDocument() {
        return this.document;
    }

    public String getFull_name() {
        return this.full_name;
    }

    public String getBirth_date() {
        return this.birth_date;
    }

    public String getPoint_code() {
        return this.point_code;
    }

    public String getVisa_number() {
        return this.visa_number;
    }

    public String getNationality() {
        return this.nationality;
    }

    public String getTrans_number() {
        return this.trans_number;
    }

    public String getTrans_add_info() {
        return this.trans_add_info;
    }

    public String getDate_end_document() {
        return this.date_end_document;
    }

    public Purpose getPurpose() {
        return this.purpose;
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

    public CrossBoard () {}
}
