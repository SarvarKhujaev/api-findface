package com.ssd.mvd.entity.modelForAddress;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermanentRegistration {
    private pRegion pRegion;
    private String pAddress;
    private String pCadastre;
    private String pRegistrationDate;
}
