package com.ssd.mvd.entity.foreigner;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class Foreigner {
    private Boolean sex; // shows the gender of person TRUE for women. FALSE for men

    private String name;
    private String note;
    private String city;
    private String house;
    private String image;
    private String region;
    private String street;
    private String country;
    private String surname;
    private String birthday;
    private String passport;
    private String apartment;
    private String patronymic;
    private String input_date;
}
