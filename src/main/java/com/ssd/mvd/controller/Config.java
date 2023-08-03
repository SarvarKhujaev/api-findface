package com.ssd.mvd.controller;

import com.ssd.mvd.FindFaceServiceApplication;
import java.util.HashMap;
import java.util.Map;

public class Config extends LogInspector {
    private Boolean flag = false;
    protected Boolean getFlag() { return this.flag; }

    protected void setFlag( final Boolean flag ) { this.flag = flag; }

    private String tokenForGai;

    protected String getTokenForGai() { return this.tokenForGai; }

    protected void setTokenForGai( final String tokenForGai ) { this.tokenForGai = tokenForGai; }

    private String tokenForFio;

    protected String getTokenForFio() { return this.tokenForFio; }

    private String tokenForPassport;

    protected String getTokenForPassport() { return this.tokenForPassport; }

    protected void setTokenForPassport( final String tokenForPassport ) { this.tokenForPassport = tokenForPassport; }

    // how many minutes to wait for Thread in SerDes class
    // 180 mins by default
    private Integer waitingMins = 180;

    protected Integer getWaitingMins() { return this.waitingMins; }

    protected void setWaitingMins( final Integer waitingMins ) { this.waitingMins = waitingMins; }

    protected String getAPI_FOR_GAI_TOKEN() { return this.API_FOR_GAI_TOKEN; }

    protected String getLOGIN_FOR_GAI_TOKEN() { return this.LOGIN_FOR_GAI_TOKEN; }

    protected String getCURRENT_SYSTEM_FOR_GAI() { return this.CURRENT_SYSTEM_FOR_GAI; }

    protected String getPASSWORD_FOR_GAI_TOKEN() { return this.PASSWORD_FOR_GAI_TOKEN; }

    protected String getAPI_FOR_TONIROVKA() { return this.API_FOR_TONIROVKA; }

    protected String getAPI_FOR_VEHICLE_DATA() { return this.API_FOR_VEHICLE_DATA; }

    protected String getAPI_FOR_FOR_INSURANCE() { return this.API_FOR_FOR_INSURANCE; }

    protected String getAPI_FOR_VIOLATION_LIST() { return this.API_FOR_VIOLATION_LIST; }

    protected String getAPI_FOR_DOVERENNOST_LIST() { return this.API_FOR_DOVERENNOST_LIST; }

    protected String getAPI_FOR_MODEL_FOR_CAR_LIST() { return this.API_FOR_MODEL_FOR_CAR_LIST; }

    protected String getAPI_FOR_PINPP() { return this.API_FOR_PINPP; }

    protected String getAPI_FOR_BOARD_CROSSING() { return this.API_FOR_BOARD_CROSSING; }

    protected String getAPI_FOR_CADASTR() { return this.API_FOR_CADASTR; }

    protected String getAPI_FOR_PERSON_IMAGE() { return this.API_FOR_PERSON_IMAGE; }

    protected String getAPI_FOR_PASSPORT_MODEL() { return this.API_FOR_PASSPORT_MODEL; }

    protected String getAPI_FOR_MODEL_FOR_ADDRESS() { return this.API_FOR_MODEL_FOR_ADDRESS; }

    protected String getAPI_FOR_PERSON_DATA_FROM_ZAKS() { return this.API_FOR_PERSON_DATA_FROM_ZAKS; }

    protected String getAPI_FOR_TRAIN_TICKET_CONSUMER_SERVICE() { return this.API_FOR_TRAIN_TICKET_CONSUMER_SERVICE; }

    protected String getBASE64_IMAGE_TO_LINK_CONVERTER_API() { return this.BASE64_IMAGE_TO_LINK_CONVERTER_API; }

    protected String getKAFKA_BROKER() { return this.KAFKA_BROKER; }

    protected String getGROUP_ID_FOR_KAFKA() { return this.GROUP_ID_FOR_KAFKA; }

    protected String getERROR_LOGS() { return this.ERROR_LOGS; }

    protected String getADMIN_PANEL() { return this.ADMIN_PANEL; }

    protected String getADMIN_PANEL_ERROR_LOG() { return this.ADMIN_PANEL_ERROR_LOG; }

    private final Map< String, Object > fields = new HashMap<>();
    private final Map< String, String > headers = new HashMap<>();

    protected Map< String, Object > getFields() { return this.fields; }

    protected Map< String, String > getHeaders() { return this.headers; }

    private final String API_FOR_GAI_TOKEN = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.GAI_VARIABLES.API_FOR_GAI_TOKEN" );

    private final String LOGIN_FOR_GAI_TOKEN = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.GAI_VARIABLES.LOGIN_FOR_GAI_TOKEN" );

    private final String CURRENT_SYSTEM_FOR_GAI = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.GAI_VARIABLES.CURRENT_SYSTEM_FOR_GAI" );

    private final String PASSWORD_FOR_GAI_TOKEN = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.GAI_VARIABLES.PASSWORD_FOR_GAI_TOKEN" );

    private final String API_FOR_TONIROVKA = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.GAI_VARIABLES.API_FOR_TONIROVKA" );

    private final String API_FOR_VEHICLE_DATA = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.GAI_VARIABLES.API_FOR_VEHICLE_DATA" );

    private final String API_FOR_FOR_INSURANCE = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.GAI_VARIABLES.API_FOR_FOR_INSURANCE" );

    private final String API_FOR_VIOLATION_LIST = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.GAI_VARIABLES.API_FOR_VIOLATION_LIST" );

    private final String API_FOR_DOVERENNOST_LIST = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.GAI_VARIABLES.API_FOR_DOVERENNOST_LIST" );

    private final String API_FOR_MODEL_FOR_CAR_LIST = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.GAI_VARIABLES.API_FOR_MODEL_FOR_CAR_LIST" );

    private final String API_FOR_PINPP = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.OVIR_VARIABLES.API_FOR_PINPP" );

    private final String API_FOR_BOARD_CROSSING = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.OVIR_VARIABLES.API_FOR_BOARD_CROSSING" );

    private final String API_FOR_CADASTR = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.OVIR_VARIABLES.API_FOR_CADASTR" );

    private final String API_FOR_PERSON_IMAGE = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.OVIR_VARIABLES.API_FOR_PERSON_IMAGE" );

    private final String API_FOR_PASSPORT_MODEL = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.OVIR_VARIABLES.API_FOR_PASSPORT_MODEL" );

    private final String API_FOR_MODEL_FOR_ADDRESS = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.OVIR_VARIABLES.API_FOR_MODEL_FOR_ADDRESS" );

    private final String API_FOR_PERSON_DATA_FROM_ZAKS = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.OVIR_VARIABLES.API_FOR_PERSON_DATA_FROM_ZAKS" );

    private final String API_FOR_TRAIN_TICKET_CONSUMER_SERVICE = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.OVIR_VARIABLES.API_FOR_TRAIN_TICKET_CONSUMER_SERVICE" );

    private final String BASE64_IMAGE_TO_LINK_CONVERTER_API = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.OVIR_VARIABLES.BASE64_IMAGE_TO_LINK_CONVERTER_API" );

    private final String KAFKA_BROKER = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.KAFKA_VARIABLES.KAFKA_BROKER" );

    private final String GROUP_ID_FOR_KAFKA = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.KAFKA_VARIABLES.GROUP_ID_FOR_KAFKA" );

    private final String ERROR_LOGS = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.KAFKA_VARIABLES.ERROR_LOGS" );

    private final String ADMIN_PANEL = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.KAFKA_VARIABLES.ADMIN_PANEL" );

    private final String ADMIN_PANEL_ERROR_LOG = FindFaceServiceApplication
            .context
            .getEnvironment()
            .getProperty( "variables.KAFKA_VARIABLES.ADMIN_PANEL_ERROR_LOG" );
}
