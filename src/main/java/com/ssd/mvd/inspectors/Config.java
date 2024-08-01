package com.ssd.mvd.inspectors;

import com.ssd.mvd.interfaces.ServiceCommonMethods;
import com.ssd.mvd.constants.Errors;
import java.util.Map;

/*-
хранит все конфигурационные данные и параметры
*/
public class Config extends LogInspector implements ServiceCommonMethods {
    protected Config () {}

    public static boolean flag = false;

    public static String tokenForGai;

    public static String tokenForFio;

    public static String tokenForPassport;

    /*
        how many minutes to wait for Thread in SerDes class
        180 mins by default
    */
    public static int waitingMins = 180;

    protected final synchronized void setFlag( final boolean value ) {
        flag = value;
    }

    protected final synchronized void setTokenForGai( final String token ) {
        tokenForGai = token;
    }

    protected final synchronized void setTokenForPassport( final String token ) {
        tokenForPassport = token;
    }

    protected final synchronized void setWaitingMins( final int mins ) {
        waitingMins = mins;
    }

    protected final synchronized String getAPI_FOR_GAI_TOKEN() {
        return super.checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.API_FOR_GAI_TOKEN",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized String getLOGIN_FOR_GAI_TOKEN() {
        return super.checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.LOGIN_FOR_GAI_TOKEN",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized String getCURRENT_SYSTEM_FOR_GAI() {
        return super.checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.CURRENT_SYSTEM_FOR_GAI",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized String getPASSWORD_FOR_GAI_TOKEN() {
        return super.checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.PASSWORD_FOR_GAI_TOKEN",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized String getAPI_FOR_TONIROVKA() {
        return super.checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.API_FOR_TONIROVKA",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized String getAPI_FOR_VEHICLE_DATA() {
        return super.checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.API_FOR_VEHICLE_DATA",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized String getAPI_FOR_FOR_INSURANCE() {
        return super.checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.API_FOR_FOR_INSURANCE",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized String getAPI_FOR_VIOLATION_LIST() {
        return super.checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.API_FOR_VIOLATION_LIST",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized String getAPI_FOR_DOVERENNOST_LIST() {
        return super.checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.API_FOR_DOVERENNOST_LIST",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized String getAPI_FOR_MODEL_FOR_CAR_LIST() {
        return super.checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.API_FOR_MODEL_FOR_CAR_LIST",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized String getAPI_FOR_PINPP() {
        return super.checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_PINPP",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized String getAPI_FOR_BOARD_CROSSING() {
        return super.checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_BOARD_CROSSING",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized String getAPI_FOR_CADASTR() {
        return super.checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_CADASTR",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized String getAPI_FOR_PERSON_IMAGE() {
        return super.checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_PERSON_IMAGE",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized String getAPI_FOR_PASSPORT_MODEL() {
        return super.checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_PASSPORT_MODEL",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized String getAPI_FOR_MODEL_FOR_ADDRESS() {
        return super.checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_MODEL_FOR_ADDRESS",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized String getAPI_FOR_PERSON_DATA_FROM_ZAKS() {
        return super.checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_PERSON_DATA_FROM_ZAKS",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized String getAPI_FOR_TRAIN_TICKET_CONSUMER_SERVICE() {
        return super.checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_TRAIN_TICKET_CONSUMER_SERVICE",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized String getBASE64_IMAGE_TO_LINK_CONVERTER_API() {
        return super.checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.BASE64_IMAGE_TO_LINK_CONVERTER_API",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized String getKAFKA_BROKER() {
        return super.checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.KAFKA_BROKER",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized String getGROUP_ID_FOR_KAFKA() {
        return super.checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.GROUP_ID_FOR_KAFKA",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized int getKAFKA_SENDER_MAX_IN_FLIGHT() {
        return super.checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.KAFKA_SENDER_MAX_IN_FLIGHT",
                1024
        );
    }

    protected final synchronized String getKAFKA_ACKS_CONFIG() {
        return super.checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.KAFKA_ACKS_CONFIG",
                "-1"
        );
    }

    protected final synchronized String getERROR_LOGS() {
        return super.checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.KAFKA_TOPICS.ERROR_LOGS",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized String getADMIN_PANEL() {
        return super.checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.KAFKA_TOPICS.ADMIN_PANEL",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    protected final synchronized String getADMIN_PANEL_ERROR_LOG() {
        return super.checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.KAFKA_TOPICS.ADMIN_PANEL_ERROR_LOG",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    public static final Map< String, Object > fields = CollectionsInspector.newMap();
    public static final Map< String, String > headers = CollectionsInspector.newMap();

    @Override
    public void close() {
        fields.clear();
        headers.clear();
    }
}