package com.ssd.mvd.inspectors;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.ssd.mvd.constants.Errors;
import com.ssd.mvd.kafka.KafkaDataControl;
import com.ssd.mvd.annotations.EntityConstructorAnnotation;

import io.netty.handler.logging.LogLevel;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@SuppressWarnings( value = "хранит все конфигурационные данные и параметры" )
@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public class Config extends LogInspector {
    @EntityConstructorAnnotation(
            permission = {
                    KafkaDataControl.class,
                    WebFluxInspector.class
            }
    )
    protected <T extends UuidInspector> Config( @lombok.NonNull final Class<T> instance ) {
        super( Config.class );

        AnnotationInspector.checkCallerPermission( instance, Config.class );
        AnnotationInspector.checkAnnotationIsImmutable( Config.class );
    }

    public volatile static boolean flag = false;

    public volatile static String tokenForGai;
    public volatile static String tokenForFio;
    public volatile static String tokenForPassport;

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
                    how many minutes to wait for Thread in SerDes class 180 mins by default
                    """
    )
    protected static volatile int waitingMins = 180;

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
    public static synchronized String getAPI_FOR_GAI_TOKEN() {
        return checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.API_FOR_GAI_TOKEN",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getLOGIN_FOR_GAI_TOKEN() {
        return checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.LOGIN_FOR_GAI_TOKEN",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getCURRENT_SYSTEM_FOR_GAI() {
        return checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.CURRENT_SYSTEM_FOR_GAI",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getPASSWORD_FOR_GAI_TOKEN() {
        return checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.PASSWORD_FOR_GAI_TOKEN",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getAPI_FOR_TONIROVKA() {
        return checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.API_FOR_TONIROVKA",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getAPI_FOR_VEHICLE_DATA() {
        return checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.API_FOR_VEHICLE_DATA",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getAPI_FOR_FOR_INSURANCE() {
        return checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.API_FOR_FOR_INSURANCE",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getAPI_FOR_VIOLATION_LIST() {
        return checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.API_FOR_VIOLATION_LIST",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getAPI_FOR_DOVERENNOST_LIST() {
        return checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.API_FOR_DOVERENNOST_LIST",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getAPI_FOR_MODEL_FOR_CAR_LIST() {
        return checkContextOrReturnDefaultValue(
                "variables.GAI_VARIABLES.API_FOR_MODEL_FOR_CAR_LIST",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getAPI_FOR_PINPP() {
        return checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_PINPP",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getAPI_FOR_BOARD_CROSSING() {
        return checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_BOARD_CROSSING",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getAPI_FOR_CADASTR() {
        return checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_CADASTR",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getAPI_FOR_PERSON_IMAGE() {
        return checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_PERSON_IMAGE",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getAPI_FOR_PASSPORT_MODEL() {
        return checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_PASSPORT_MODEL",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getAPI_FOR_MODEL_FOR_ADDRESS() {
        return checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_MODEL_FOR_ADDRESS",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getAPI_FOR_PERSON_DATA_FROM_ZAKS() {
        return checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_PERSON_DATA_FROM_ZAKS",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getAPI_FOR_TRAIN_TICKET_CONSUMER_SERVICE() {
        return checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.API_FOR_TRAIN_TICKET_CONSUMER_SERVICE",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getBASE64_IMAGE_TO_LINK_CONVERTER_API() {
        return checkContextOrReturnDefaultValue(
                "variables.OVIR_VARIABLES.BASE64_IMAGE_TO_LINK_CONVERTER_API",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getKAFKA_BROKER() {
        return checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.KAFKA_BROKER",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getGROUP_ID_FOR_KAFKA() {
        return checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.GROUP_ID_FOR_KAFKA",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.Synchronized
    public static synchronized int getKAFKA_SENDER_MAX_IN_FLIGHT() {
        return checkContextOrReturnDefaultValue();
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getKAFKA_ACKS_CONFIG() {
        return checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.KAFKA_ACKS_CONFIG",
                "-1"
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getRETRIES_CONFIG() {
        return checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.RETRIES_CONFIG",
                "-1"
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getLINGER_MS_CONFIG() {
        return checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.LINGER_MS_CONFIG",
                "-1"
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getBATCH_SIZE_CONFIG() {
        return checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.BATCH_SIZE_CONFIG",
                "-1"
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getBUFFER_MEMORY_CONFIG() {
        return checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.BUFFER_MEMORY_CONFIG",
                "-1"
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getREQUEST_TIMEOUT_MS_CONFIG() {
        return checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.REQUEST_TIMEOUT_MS_CONFIG",
                "-1"
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getMETADATA_MAX_AGE_CONFIG() {
        return checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.METADATA_MAX_AGE_CONFIG",
                "-1"
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getERROR_LOGS() {
        return checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.KAFKA_TOPICS.ERROR_LOGS",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getADMIN_PANEL() {
        return checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.KAFKA_TOPICS.ADMIN_PANEL",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized String getADMIN_PANEL_ERROR_LOG() {
        return checkContextOrReturnDefaultValue(
                "variables.KAFKA_VARIABLES.KAFKA_TOPICS.ADMIN_PANEL_ERROR_LOG",
                Errors.DATA_NOT_FOUND.name()
        );
    }

    private static final AtomicReference< Map< String, Object > > fields = EntitiesInstances.generateAtomicEntity(
            CollectionsInspector.newMap()
    );
    private static final AtomicReference< Map< String, String > > headers = EntitiesInstances.generateAtomicEntity(
            CollectionsInspector.newMap()
    );

    @lombok.NonNull
    @lombok.Synchronized
    protected synchronized static AtomicReference< Map< String, String > > getHeaders () {
        return headers;
    }

    @lombok.NonNull
    @lombok.Synchronized
    protected synchronized static AtomicReference< Map< String, Object > > getFields () {
        return fields;
    }

    @Override
    public void close() {
        CollectionsInspector.checkAndClear( headers.get() );
        CollectionsInspector.checkAndClear( fields.get() );
    }
}
