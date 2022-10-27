package com.ssd.mvd.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.extern.jackson.Jacksonized;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

import com.ssd.mvd.entity.family.Family;
import java.util.List;

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

    @JsonDeserialize
    private List< PapilonData > results;
    @JsonDeserialize
    private List< Violation > violationList;
}
