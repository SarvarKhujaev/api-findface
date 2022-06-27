package com.ssd.mvd.entity.modelForGai;

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
public class ViolationsList {
    @JsonDeserialize
    private List< ViolationsInformation > violationsInformationsList;
}
