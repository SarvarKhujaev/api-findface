package com.ssd.mvd.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ssd.mvd.interfaces.ServiceCommonMethods;
import java.util.List;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class Results implements ServiceCommonMethods {
    private int result_code;

    @JsonDeserialize
    private List< PapilonData > results;
    @JsonDeserialize
    private List< Violation > violationList;

    @Override
    public void close() {
        this.results.clear();
        this.violationList.clear();
    }
}
