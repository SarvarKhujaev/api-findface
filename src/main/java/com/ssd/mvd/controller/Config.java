package com.ssd.mvd.controller;

import com.ssd.mvd.FindFaceServiceApplication;
import lombok.Data;

@Data
public class Config {
    private final String LOGIN_FOR_GAI_TOKEN = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.LOGIN_FOR_GAI_TOKEN" );

    private final String CURRENT_SYSTEM_FOR_GAI = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.CURRENT_SYSTEM_FOR_GAI" );

    private final String PASSWORD_FOR_GAI_TOKEN = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.PASSWORD_FOR_GAI_TOKEN" );

    private final String LOGIN_FOR_FIO_TOKEN = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.LOGIN_FOR_FIO_TOKEN" );

    private final String CURRENT_SYSTEM_FOR_FIO = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.CURRENT_SYSTEM_FOR_FIO" );

    private final String PASSWORD_FOR_FIO_TOKEN = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.PASSWORD_FOR_FIO_TOKEN" );

    private final String API_FOR_GAI_TOKEN = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.API_FOR_GAI_TOKEN" );

    private final String API_FOR_TONIROVKA = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.API_FOR_TONIROVKA" );

    private final String API_FOR_VEHICLE_DATA = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.API_FOR_VEHICLE_DATA" );

    private final String API_FOR_FOR_INSURANCE = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.API_FOR_FOR_INSURANCE" );

    private final String API_FOR_VIOLATION_LIST = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.API_FOR_VIOLATION_LIST" );

    private final String API_FOR_DOVERENNOST_LIST = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.API_FOR_DOVERENNOST_LIST" );

    private final String API_FOR_MODEL_FOR_CAR_LIST = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.API_FOR_MODEL_FOR_CAR_LIST" );

    private final String API_FOR_PINPP = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.API_FOR_PINPP" );

    private final String API_FOR_CADASTR = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.API_FOR_CADASTR" );

    private final String API_FOR_FIO_TOKEN = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.API_FOR_FIO_TOKEN" );

    private final String API_FOR_PERSON_IMAGE = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.API_FOR_PERSON_IMAGE" );

    private final String API_FOR_PASSPORT_MODEL = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.API_FOR_PASSPORT_MODEL" );

    private final String API_FOR_MODEL_FOR_ADDRESS = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.API_FOR_MODEL_FOR_ADDRESS" );

    private final String API_FOR_PERSON_DATA_FROM_ZAKS = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.API_FOR_PERSON_DATA_FROM_ZAKS" );

    private final String API_FOR_TRAIN_TICKET_CONSUMER_SERVICE = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.API_FOR_TRAIN_TICKET_CONSUMER_SERVICE" );

    private final String BASE64_IMAGE_TO_LINK_CONVERTER_API = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.BASE64_IMAGE_TO_LINK_CONVERTER_API" );
}
