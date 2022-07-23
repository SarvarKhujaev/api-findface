package com.ssd.mvd.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermanentRegistration {
    private String pAddress;
    private String pCadastre;
    private String pRegistrationDate;
    private com.ssd.mvd.entity.pRegion pRegion;
}
