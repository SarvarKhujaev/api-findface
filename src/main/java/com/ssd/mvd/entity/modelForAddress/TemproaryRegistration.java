package com.ssd.mvd.entity.modelForAddress;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public final class TemproaryRegistration {
    private String pAddress;
    private String pCadastre;
    private String pValidDate;
    private String pRegistrationDate;

    private pRegion pRegion;
    private pDistrict pDistrict;
}
