package com.ssd.mvd.entity.modelForGai;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public final class ViolationsInformation {
    public int getDecreeStatus() {
        return this.DecreeStatus;
    }

    public int getAmount() {
        return this.Amount;
    }

    public String getDecreeSerialNumber() {
        return this.DecreeSerialNumber;
    }

    public String getViolation() {
        return this.Violation;
    }

    public String getDivision() {
        return this.Division;
    }

    public String getPayDate() {
        return this.PayDate;
    }

    public String getAddress() {
        return this.Address;
    }

    public String getArticle() {
        return this.Article;
    }

    public String getOwner() {
        return this.Owner;
    }

    public String getModel() {
        return this.Model;
    }

    public String getBill() {
        return this.Bill;
    }

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

    public ViolationsInformation() {}
}
