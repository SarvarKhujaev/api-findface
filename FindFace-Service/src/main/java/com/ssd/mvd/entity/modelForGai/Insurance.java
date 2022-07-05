package com.ssd.mvd.entity.modelForGai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Insurance {
    private String companyName;
    private String insuranceType;
    private String dateOfCreation;
    private String insuranceNumber;
    private String dateOfValidation;
}
