package com.ssd.mvd.entity;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class PapilonData {
    private Integer rank;
    private Double score;

    private String name;
    private String photo;
    private String birth;
    private String country;
    private String passport;
    private String personal_code;
}
