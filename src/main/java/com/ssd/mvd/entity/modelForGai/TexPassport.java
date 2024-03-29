package com.ssd.mvd.entity.modelForGai;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ssd.mvd.entity.modelForPassport.ModelForPassport;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class TexPassport {
    private Double weight; // вес без груза
    private Double totalWeight; // общий вес

    private Integer enginePower;
    private Integer numberOfSeats; // количество сидений
    private Integer numberOfVerticalSeats; // количество стоячих мест

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
