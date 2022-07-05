package com.ssd.mvd.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties ( ignoreUnknown = true )
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
