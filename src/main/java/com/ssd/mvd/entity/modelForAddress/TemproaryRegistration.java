package com.ssd.mvd.entity.modelForAddress;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemproaryRegistration {
    private String pAddress;
    private String pCadastre;
    private String pValidDate;
    private String pRegistrationDate;

    private pRegion pRegion;
    private pDistrict pDistrict;
}
