package com.ssd.mvd.entity.modelForCadastr;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemproaryRegistration {
    private String pPsp;
    private String pPerson;
    private pStatus pStatus;
    private String pCitizen;
    private String pDateBirth;
    private String pRegistrationDate;
}
