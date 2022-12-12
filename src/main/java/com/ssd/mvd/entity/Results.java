package com.ssd.mvd.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.entity.family.Family;

import lombok.extern.jackson.Jacksonized;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;
import lombok.Data;

@Data
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Results {
    private String daddyPinfl;
    private String mommyPinfl;
    private Integer result_code;

    @JsonDeserialize
    private Family daddyData;
    @JsonDeserialize
    private Family mommyData;
    @JsonDeserialize
    private Family childData;

    private ErrorResponse errorResponse;

    @JsonDeserialize
    private List< PapilonData > results;
    @JsonDeserialize
    private List< Violation > violationList;

    public Results ( ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }
}
