package com.ssd.mvd.entity;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class Region {
    private Long regionId;
    private Long mahallaId;
    private Long districtId; // tuman
    private String regionName;
    private String mahallaName;
    private String districtName;
}
