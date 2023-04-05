package com.ssd.mvd.entity;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class PermanentRegistration {
    private String pAddress;
    private String pCadastre;
    private String pRegistrationDate;
    private com.ssd.mvd.entity.pRegion pRegion;
    private com.ssd.mvd.entity.modelForAddress.pDistrict pDistrict;
}
