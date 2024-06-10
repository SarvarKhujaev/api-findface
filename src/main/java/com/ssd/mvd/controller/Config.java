package com.ssd.mvd.controller;

import com.ssd.mvd.constants.Errors;
import java.util.HashMap;
import java.util.Map;

/*-
хранит все конфигурационные данные и параметры
*/
public class Config extends LogInspector {
    private boolean flag = false;

    protected boolean getFlag() {
        return this.flag;
    }

    protected void setFlag( final boolean flag ) {
        this.flag = flag;
    }

    private String tokenForGai;

    public String getTokenForGai() {
        return this.tokenForGai;
    }

    protected void setTokenForGai( final String tokenForGai ) {
        this.tokenForGai = tokenForGai;
    }

    private String tokenForFio;

    protected String getTokenForFio() {
        return this.tokenForFio;
    }

    private String tokenForPassport;

    protected String getTokenForPassport() {
        return this.tokenForPassport;
    }

    protected void setTokenForPassport( final String tokenForPassport ) {
        this.tokenForPassport = tokenForPassport;
    }

    /*
        how many minutes to wait for Thread in SerDes class
        180 mins by default
    */
    private int waitingMins = 180;

    protected int getWaitingMins() {
        return this.waitingMins;
    }

    protected void setWaitingMins( final int waitingMins ) {
        this.waitingMins = waitingMins;
    }

    protected String getAPI_FOR_GAI_TOKEN() {
        return this.API_FOR_GAI_TOKEN;
    }

    protected String getLOGIN_FOR_GAI_TOKEN() {
        return this.LOGIN_FOR_GAI_TOKEN;
    }

    protected String getCURRENT_SYSTEM_FOR_GAI() {
        return this.CURRENT_SYSTEM_FOR_GAI;
    }

    protected String getPASSWORD_FOR_GAI_TOKEN() {
        return this.PASSWORD_FOR_GAI_TOKEN;
    }

    protected String getAPI_FOR_TONIROVKA() {
        return this.API_FOR_TONIROVKA;
    }

    protected String getAPI_FOR_VEHICLE_DATA() {
        return this.API_FOR_VEHICLE_DATA;
    }

    protected String getAPI_FOR_FOR_INSURANCE() {
        return this.API_FOR_FOR_INSURANCE;
    }

    protected String getAPI_FOR_VIOLATION_LIST() {
        return this.API_FOR_VIOLATION_LIST;
    }

    protected String getAPI_FOR_DOVERENNOST_LIST() {
        return this.API_FOR_DOVERENNOST_LIST;
    }

    protected String getAPI_FOR_MODEL_FOR_CAR_LIST() {
        return this.API_FOR_MODEL_FOR_CAR_LIST;
    }

    protected String getAPI_FOR_PINPP() {
        return this.API_FOR_PINPP;
    }

    protected String getAPI_FOR_BOARD_CROSSING() {
        return this.API_FOR_BOARD_CROSSING;
    }

    protected String getAPI_FOR_CADASTR() {
        return this.API_FOR_CADASTR;
    }

    protected String getAPI_FOR_PERSON_IMAGE() {
        return this.API_FOR_PERSON_IMAGE;
    }

    protected String getAPI_FOR_PASSPORT_MODEL() {
        return this.API_FOR_PASSPORT_MODEL;
    }

    protected String getAPI_FOR_MODEL_FOR_ADDRESS() {
        return this.API_FOR_MODEL_FOR_ADDRESS;
    }

    protected String getAPI_FOR_PERSON_DATA_FROM_ZAKS() {
        return this.API_FOR_PERSON_DATA_FROM_ZAKS;
    }

    protected String getAPI_FOR_TRAIN_TICKET_CONSUMER_SERVICE() {
        return this.API_FOR_TRAIN_TICKET_CONSUMER_SERVICE;
    }

    protected String getBASE64_IMAGE_TO_LINK_CONVERTER_API() {
        return this.BASE64_IMAGE_TO_LINK_CONVERTER_API;
    }

    protected String getKAFKA_BROKER() {
        return this.KAFKA_BROKER;
    }

    protected String getGROUP_ID_FOR_KAFKA() {
        return this.GROUP_ID_FOR_KAFKA;
    }

    protected int getKAFKA_SENDER_MAX_IN_FLIGHT() {
        return this.KAFKA_SENDER_MAX_IN_FLIGHT;
    }

    protected String getKAFKA_ACKS_CONFIG() {
        return this.KAFKA_ACKS_CONFIG;
    }

    protected String getERROR_LOGS() {
        return this.ERROR_LOGS;
    }

    protected String getADMIN_PANEL() {
        return this.ADMIN_PANEL;
    }

    protected String getADMIN_PANEL_ERROR_LOG() {
        return this.ADMIN_PANEL_ERROR_LOG;
    }

    private final Map< String, Object > fields = new HashMap<>();
    private final Map< String, String > headers = new HashMap<>();

    protected Map< String, Object > getFields() {
        return this.fields;
    }

    protected Map< String, String > getHeaders() {
        return this.headers;
    }

    private final String API_FOR_GAI_TOKEN = super.checkContextOrReturnDefaultValue(
            "variables.GAI_VARIABLES.API_FOR_GAI_TOKEN",
            Errors.DATA_NOT_FOUND.name()
    );

    private final String LOGIN_FOR_GAI_TOKEN = super.checkContextOrReturnDefaultValue(
            "variables.GAI_VARIABLES.LOGIN_FOR_GAI_TOKEN",
            Errors.DATA_NOT_FOUND.name()
    );

    private final String CURRENT_SYSTEM_FOR_GAI = super.checkContextOrReturnDefaultValue(
            "variables.GAI_VARIABLES.CURRENT_SYSTEM_FOR_GAI",
            Errors.DATA_NOT_FOUND.name()
    );

    private final String PASSWORD_FOR_GAI_TOKEN = super.checkContextOrReturnDefaultValue(
            "variables.GAI_VARIABLES.PASSWORD_FOR_GAI_TOKEN",
            Errors.DATA_NOT_FOUND.name()
    );

    private final String API_FOR_TONIROVKA = super.checkContextOrReturnDefaultValue(
            "variables.GAI_VARIABLES.API_FOR_TONIROVKA",
            Errors.DATA_NOT_FOUND.name()
    );

    private final String API_FOR_VEHICLE_DATA = super.checkContextOrReturnDefaultValue(
            "variables.GAI_VARIABLES.API_FOR_VEHICLE_DATA",
            Errors.DATA_NOT_FOUND.name()
    );

    private final String API_FOR_FOR_INSURANCE = super.checkContextOrReturnDefaultValue(
            "variables.GAI_VARIABLES.API_FOR_FOR_INSURANCE",
            Errors.DATA_NOT_FOUND.name()
    );

    private final String API_FOR_VIOLATION_LIST = super.checkContextOrReturnDefaultValue(
            "variables.GAI_VARIABLES.API_FOR_VIOLATION_LIST",
            Errors.DATA_NOT_FOUND.name()
    );

    private final String API_FOR_DOVERENNOST_LIST = super.checkContextOrReturnDefaultValue(
            "variables.GAI_VARIABLES.API_FOR_DOVERENNOST_LIST",
            Errors.DATA_NOT_FOUND.name()
    );

    private final String API_FOR_MODEL_FOR_CAR_LIST = super.checkContextOrReturnDefaultValue(
            "variables.GAI_VARIABLES.API_FOR_MODEL_FOR_CAR_LIST",
            Errors.DATA_NOT_FOUND.name()
    );

    private final String API_FOR_PINPP = super.checkContextOrReturnDefaultValue(
            "variables.OVIR_VARIABLES.API_FOR_PINPP",
            Errors.DATA_NOT_FOUND.name()
    );

    private final String API_FOR_BOARD_CROSSING = super.checkContextOrReturnDefaultValue(
            "variables.OVIR_VARIABLES.API_FOR_BOARD_CROSSING",
            Errors.DATA_NOT_FOUND.name()
    );

    private final String API_FOR_CADASTR = super.checkContextOrReturnDefaultValue(
            "variables.OVIR_VARIABLES.API_FOR_CADASTR",
            Errors.DATA_NOT_FOUND.name()
    );

    private final String API_FOR_PERSON_IMAGE = super.checkContextOrReturnDefaultValue(
            "variables.OVIR_VARIABLES.API_FOR_PERSON_IMAGE",
            Errors.DATA_NOT_FOUND.name()
    );

    private final String API_FOR_PASSPORT_MODEL = super.checkContextOrReturnDefaultValue(
            "variables.OVIR_VARIABLES.API_FOR_PASSPORT_MODEL",
            Errors.DATA_NOT_FOUND.name()
    );

    private final String API_FOR_MODEL_FOR_ADDRESS = super.checkContextOrReturnDefaultValue(
            "variables.OVIR_VARIABLES.API_FOR_MODEL_FOR_ADDRESS",
            Errors.DATA_NOT_FOUND.name()
    );

    private final String API_FOR_PERSON_DATA_FROM_ZAKS = super.checkContextOrReturnDefaultValue(
            "variables.OVIR_VARIABLES.API_FOR_PERSON_DATA_FROM_ZAKS",
            Errors.DATA_NOT_FOUND.name()
    );

    private final String API_FOR_TRAIN_TICKET_CONSUMER_SERVICE = super.checkContextOrReturnDefaultValue(
            "variables.OVIR_VARIABLES.API_FOR_TRAIN_TICKET_CONSUMER_SERVICE",
            Errors.DATA_NOT_FOUND.name()
    );

    private final String BASE64_IMAGE_TO_LINK_CONVERTER_API = super.checkContextOrReturnDefaultValue(
            "variables.OVIR_VARIABLES.BASE64_IMAGE_TO_LINK_CONVERTER_API",
            Errors.DATA_NOT_FOUND.name()
    );

    private final String KAFKA_BROKER = super.checkContextOrReturnDefaultValue(
            "variables.KAFKA_VARIABLES.KAFKA_BROKER",
            Errors.DATA_NOT_FOUND.name()
    );

    private final String GROUP_ID_FOR_KAFKA = super.checkContextOrReturnDefaultValue(
            "variables.KAFKA_VARIABLES.GROUP_ID_FOR_KAFKA",
            Errors.DATA_NOT_FOUND.name()
    );

    private final int KAFKA_SENDER_MAX_IN_FLIGHT = super.checkContextOrReturnDefaultValue(
            "variables.KAFKA_VARIABLES.KAFKA_SENDER_MAX_IN_FLIGHT",
            1024
    );

    private final String KAFKA_ACKS_CONFIG = super.checkContextOrReturnDefaultValue(
            "variables.KAFKA_VARIABLES.KAFKA_ACKS_CONFIG",
            "-1"
    );

    private final String ERROR_LOGS = super.checkContextOrReturnDefaultValue(
            "variables.KAFKA_VARIABLES.KAFKA_TOPICS.ERROR_LOGS",
            Errors.DATA_NOT_FOUND.name()
    );

    private final String ADMIN_PANEL = super.checkContextOrReturnDefaultValue(
            "variables.KAFKA_VARIABLES.KAFKA_TOPICS.ADMIN_PANEL",
            Errors.DATA_NOT_FOUND.name()
    );

    private final String ADMIN_PANEL_ERROR_LOG = super.checkContextOrReturnDefaultValue(
            "variables.KAFKA_VARIABLES.KAFKA_TOPICS.ADMIN_PANEL_ERROR_LOG",
            Errors.DATA_NOT_FOUND.name()
    );
}
