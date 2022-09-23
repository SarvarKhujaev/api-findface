package com.ssd.mvd.controller;

import lombok.Data;

@Data
public class Config {
    private final String LOGIN_FOR_GAI_TOKEN;
    private final String CURRENT_SYSTEM_FOR_GAI;
    private final String PASSWORD_FOR_GAI_TOKEN;

    private final String LOGIN_FOR_FIO_TOKEN;
    private final String CURRENT_SYSTEM_FOR_FIO;
    private final String PASSWORD_FOR_FIO_TOKEN;

    private final String API_FOR_GAI_TOKEN;
    private final String API_FOR_TONIROVKA;
    private final String API_FOR_VEHICLE_DATA;
    private final String API_FOR_FOR_INSURANCE;
    private final String API_FOR_VIOLATION_LIST;
    private final String API_FOR_DOVERENNOST_LIST;
    private final String API_FOR_MODEL_FOR_CAR_LIST;

    private final String API_FOR_PINPP;
    private final String API_FOR_CADASTR;
    private final String API_FOR_FIO_TOKEN;
    private final String API_FOR_PERSON_IMAGE;
    private final String API_FOR_PASSPORT_MODEL;
    private final String API_FOR_MODEL_FOR_ADDRESS;
    private final String API_FOR_PERSON_DATA_FROM_ZAKS;
    private final String API_FOR_TRAIN_TICKET_CONSUMER_SERVICE;
}
