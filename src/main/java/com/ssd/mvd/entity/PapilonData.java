package com.ssd.mvd.entity;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class PapilonData {
    private int rank;
    private double score;

    private String name;
    private String photo;
    private String birth;
    private String country;
    private String passport;
    private String personal_code;
}
