package com.ssd.mvd.entity;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class Region {
    private long regionId;
    private long mahallaId;
    private long districtId; // tuman

    private String regionName;
    private String mahallaName;
    private String districtName;
}
