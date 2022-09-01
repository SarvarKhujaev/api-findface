package com.ssd.mvd.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ssd.mvd.entity.family.FamilyMember;
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

    @JsonDeserialize
    private FamilyMember daddyData;
    @JsonDeserialize
    private FamilyMember mommyData;
    @JsonDeserialize
    private FamilyMember childData;
}
