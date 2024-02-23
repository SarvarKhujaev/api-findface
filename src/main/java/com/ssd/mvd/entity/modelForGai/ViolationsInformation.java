package com.ssd.mvd.entity.modelForGai;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class ViolationsInformation {
    private Integer DecreeStatus;
    private Integer Amount;

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
