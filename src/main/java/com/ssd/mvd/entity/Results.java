package com.ssd.mvd.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
    private Integer result_code;

    @JsonDeserialize
    private List< PapilonData > results;
    @JsonDeserialize
    private List< Violation > violationList;
}
