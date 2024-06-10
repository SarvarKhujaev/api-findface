package com.ssd.mvd.entity.modelForGai;

public final class ViolationsInformation {
    public int getDecreeStatus() {
        return this.DecreeStatus;
    }

    public void setDecreeStatus( final int decreeStatus ) {
        this.DecreeStatus = decreeStatus;
    }

    public int getAmount() {
        return this.Amount;
    }

    public void setAmount( final int amount ) {
        this.Amount = amount;
    }

    public String getDecreeSerialNumber() {
        return this.DecreeSerialNumber;
    }

    public void setDecreeSerialNumber( final String decreeSerialNumber ) {
        this.DecreeSerialNumber = decreeSerialNumber;
    }

    public String getViolation() {
        return this.Violation;
    }

    public void setViolation( final String violation ) {
        this.Violation = violation;
    }

    public String getDivision() {
        return this.Division;
    }

    public void setDivision( final String division ) {
        this.Division = division;
    }

    public String getPayDate() {
        return this.PayDate;
    }

    public void setPayDate( final String payDate ) {
        this.PayDate = payDate;
    }

    public String getAddress() {
        return this.Address;
    }

    public void setAddress( final String address ) {
        this.Address = address;
    }

    public String getArticle() {
        return this.Article;
    }

    public void setArticle( final String article ) {
        this.Article = article;
    }

    public String getOwner() {
        return this.Owner;
    }

    public void setOwner( final String owner ) {
        this.Owner = owner;
    }

    public String getModel() {
        return this.Model;
    }

    public void setModel( final String model ) {
        this.Model = model;
    }

    public String getBill() {
        return this.Bill;
    }

    public void setBill( final String bill ) {
        this.Bill = bill;
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

    public ViolationsInformation () {}
}
