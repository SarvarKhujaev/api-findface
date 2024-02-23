package com.ssd.mvd.entity;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class Violation {
    private Long protocol_id;

    private String pinpp;
    private String decision;
    private String punishment;
    private String last_name_lat;
    private String violation_time;
    private String first_name_lat;
    private String protocol_series;
    private String second_name_lat;
    private String protocol_number;
    private String adm_case_number;
    private String adm_case_series;
    private String resolution_time;
    private String violation_article;
}
