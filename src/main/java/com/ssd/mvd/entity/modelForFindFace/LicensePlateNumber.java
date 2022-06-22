package com.ssd.mvd.entity.modelForFindFace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicensePlateNumber {
    private Double confidence;
    private String name;
    private Bbox bbox;
}