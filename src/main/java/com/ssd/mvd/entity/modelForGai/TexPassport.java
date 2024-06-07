package com.ssd.mvd.entity.modelForGai;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ssd.mvd.entity.modelForPassport.ModelForPassport;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class TexPassport {
    private double weight; // вес без груза
    private double totalWeight; // общий вес

    private int enginePower;
    private int numberOfSeats; // количество сидений
    private int numberOfVerticalSeats; // количество стоячих мест

    private String STIR;
    private String address;
    private String fuelType; // ??
    private String carModel;
    private String carColor;
    private String engineNumber;
    private String organization;
    private String dateOfCreation; // the date when car was created
    private String dateOfReceiving; // дата выдачи машины
    private String texPassportSeries;
    @JsonDeserialize
    private ModelForPassport modelForPassport; // car owner
}
