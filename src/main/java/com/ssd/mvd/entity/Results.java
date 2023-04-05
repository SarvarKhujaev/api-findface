package com.ssd.mvd.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.extern.jackson.Jacksonized;
import java.util.List;

@lombok.Data
@Jacksonized
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class Results {
    private Integer result_code;

    @JsonDeserialize
    private List< PapilonData > results;
    @JsonDeserialize
    private List< Violation > violationList;
}
