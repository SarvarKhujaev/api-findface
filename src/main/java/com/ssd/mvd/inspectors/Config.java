package com.ssd.mvd.inspectors;

import java.util.Map;
import io.netty.handler.logging.LogLevel;

import com.ssd.mvd.constants.Errors;
import com.ssd.mvd.interfaces.ServiceCommonMethods;

import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

/*-
хранит все конфигурационные данные и параметры
*/
public class Config extends LogInspector implements ServiceCommonMethods {
    protected Config () {}

    public static boolean flag = false;

    public static String tokenForGai;
    public static String tokenForFio;
    public static String tokenForPassport;

    protected static final HttpClient HTTP_CLIENT = reactor.netty.http.client.HttpClient
            .create()
            .responseTimeout( HttpClientDuration )
            .headers( h -> h.add( "Content-Type", "application/json" ) )
            .wiretap(
                    "reactor.netty.http.client.HttpClient",
                    LogLevel.TRACE,
                    AdvancedByteBufFormat.TEXTUAL
            );

    @SuppressWarnings(
            value = """
                    how many minutes to wait for Thread in SerDes class
                            180 mins by default
                    """
    )
    protected static int waitingMins = 180;

    @lombok.Synchronized
    protected final synchronized void setFlag( final boolean value ) {
        flag = value;
    }

    @lombok.Synchronized
    protected final synchronized void setTokenForGai( final String token ) {
        tokenForGai = token;
    }

    @lombok.Synchronized
    protected final synchronized void setTokenForPassport( final String token ) {
        tokenForPassport = token;
    }

    @lombok.Synchronized
    protected final synchronized void setWaitingMins( final int mins ) {
        waitingMins = mins;
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getAPI_FOR_GAI_TOKEN() {
        return super.checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.API_FOR_GAI_TOKEN",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getLOGIN_FOR_GAI_TOKEN() {
        return super.checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.LOGIN_FOR_GAI_TOKEN",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getCURRENT_SYSTEM_FOR_GAI() {
        return super.checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.CURRENT_SYSTEM_FOR_GAI",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getPASSWORD_FOR_GAI_TOKEN() {
        return super.checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.PASSWORD_FOR_GAI_TOKEN",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getAPI_FOR_TONIROVKA() {
        return super.checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.API_FOR_TONIROVKA",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getAPI_FOR_VEHICLE_DATA() {
        return super.checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.API_FOR_VEHICLE_DATA",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getAPI_FOR_FOR_INSURANCE() {
        return super.checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.API_FOR_FOR_INSURANCE",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getAPI_FOR_VIOLATION_LIST() {
        return super.checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.API_FOR_VIOLATION_LIST",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getAPI_FOR_DOVERENNOST_LIST() {
        return super.checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.API_FOR_DOVERENNOST_LIST",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getAPI_FOR_MODEL_FOR_CAR_LIST() {
        return super.checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.API_FOR_MODEL_FOR_CAR_LIST",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getAPI_FOR_PINPP() {
        return super.checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_PINPP",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getAPI_FOR_BOARD_CROSSING() {
        return super.checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_BOARD_CROSSING",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getAPI_FOR_CADASTR() {
        return super.checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_CADASTR",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getAPI_FOR_PERSON_IMAGE() {
        return super.checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_PERSON_IMAGE",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getAPI_FOR_PASSPORT_MODEL() {
        return super.checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_PASSPORT_MODEL",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getAPI_FOR_MODEL_FOR_ADDRESS() {
        return super.checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_MODEL_FOR_ADDRESS",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getAPI_FOR_PERSON_DATA_FROM_ZAKS() {
        return super.checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_PERSON_DATA_FROM_ZAKS",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getAPI_FOR_TRAIN_TICKET_CONSUMER_SERVICE() {
        return super.checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_TRAIN_TICKET_CONSUMER_SERVICE",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getBASE64_IMAGE_TO_LINK_CONVERTER_API() {
        return super.checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.BASE64_IMAGE_TO_LINK_CONVERTER_API",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getKAFKA_BROKER() {
        return super.checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.KAFKA_BROKER",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getGROUP_ID_FOR_KAFKA() {
        return super.checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.GROUP_ID_FOR_KAFKA",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.Synchronized
    protected final synchronized int getKAFKA_SENDER_MAX_IN_FLIGHT() {
        return super.checkContextOrReturnDefaultValue();
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getKAFKA_ACKS_CONFIG() {
        return super.checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.KAFKA_ACKS_CONFIG",
                "-1"
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getERROR_LOGS() {
        return super.checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.KAFKA_TOPICS.ERROR_LOGS",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getADMIN_PANEL() {
        return super.checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.KAFKA_TOPICS.ADMIN_PANEL",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected final synchronized String getADMIN_PANEL_ERROR_LOG() {
        return super.checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.KAFKA_TOPICS.ADMIN_PANEL_ERROR_LOG",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    private static final Map< String, Object > fields = CollectionsInspector.newMap();
    private static final Map< String, String > headers = CollectionsInspector.newMap();

    @lombok.NonNull
    @lombok.Synchronized
    protected synchronized static Map< String, String > getHeaders () {
        return headers;
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected synchronized static Map< String, Object > getFields () {
        return fields;
    }

    @Override
    public void close() {
        headers.clear();
        fields.clear();

        this.clean();
    }
}
