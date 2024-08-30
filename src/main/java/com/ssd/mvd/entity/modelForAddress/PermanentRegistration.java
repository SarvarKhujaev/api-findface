package com.ssd.mvd.entity.modelForAddress;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public final class PermanentRegistration {
    private pRegion pRegion;

    private String pAddress;
    private String pCadastre;
    private String pRegistrationDate;
}
