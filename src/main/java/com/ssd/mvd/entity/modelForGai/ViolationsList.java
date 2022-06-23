package com.ssd.mvd.entity.modelForGai;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import lombok.Data;

@Data
public class ViolationsList {
    @JsonDeserialize
    private final List< ViolationsInformation > violationsInformationsList;
}
