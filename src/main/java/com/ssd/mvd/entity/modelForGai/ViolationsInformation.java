package com.ssd.mvd.entity.modelForGai;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class ViolationsInformation {
    private int DecreeStatus;
    private int Amount;

    private String DecreeSerialNumber;
    private String Violation;
    private String Division;
    private String PayDate;
    private String Address;
    private String Article;
    private String Owner;
    private String Model;
    private String Bill;
}
