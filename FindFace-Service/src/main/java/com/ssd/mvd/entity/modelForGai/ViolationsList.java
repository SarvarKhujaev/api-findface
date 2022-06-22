package com.ssd.mvd.entity.modelForGai;

import lombok.Data;

import java.util.List;

@Data
public class ViolationsList {
    private final List< ViolationsInformation > violationsInformationsList;
}
