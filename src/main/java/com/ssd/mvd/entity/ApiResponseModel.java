package com.ssd.mvd.entity;

@lombok.Builder
@lombok.Data
public class ApiResponseModel {
    private Boolean success;
    private Status status;
    private User user;
}
