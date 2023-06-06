package com.ssd.mvd.entity.boardCrossing;

import java.text.SimpleDateFormat;
import java.util.Date;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class CrossBoard {
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
        try { this.setRegistrationDate( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).parse( this.getReg_date() ) );
        } catch ( final Exception e ) { System.out.println( e.getMessage() ); }
        this.setDirection_type_code( this.getDirection_type_code().equals( "P" ) ? "въезд" : "выезд" );
        this.setPurpose( new Purpose( this, nationalityId ) );
        return this; }
}
