package com.ssd.mvd.entity.family;

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
public class Family {
    private String id;
    private String result_message;

    private Integer result_code;
    @JsonDeserialize
    private List< FamilyMember > items;
}
