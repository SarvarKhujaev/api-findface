package com.ssd.mvd.inspectors;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public final class ConfigTest {
    @Test
    @DisplayName( value = "getAPI_FOR_GAI_TOKEN" )
    void testGetAPI_FOR_GAI_TOKEN() {
        assertNotNull( Config.getAPI_FOR_GAI_TOKEN() );
    }

    @Test
    @DisplayName( value = "getLOGIN_FOR_GAI_TOKEN" )
    void testGetLOGIN_FOR_GAI_TOKEN() {
        assertNotNull( Config.getLOGIN_FOR_GAI_TOKEN() );
    }

    @Test
    @DisplayName( value = "getCURRENT_SYSTEM_FOR_GAI" )
    void testGetCURRENT_SYSTEM_FOR_GAI() {
        assertNotNull( Config.getCURRENT_SYSTEM_FOR_GAI() );
    }

    @Test
    @DisplayName( value = "getPASSWORD_FOR_GAI_TOKEN" )
    void testGetPASSWORD_FOR_GAI_TOKEN() {
        assertNotNull( Config.getPASSWORD_FOR_GAI_TOKEN() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_TONIROVKA" )
    void testGetAPI_FOR_TONIROVKA() {
        assertNotNull( Config.getAPI_FOR_TONIROVKA() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_VEHICLE_DATA" )
    void testGetAPI_FOR_VEHICLE_DATA() {
        assertNotNull( Config.getAPI_FOR_VEHICLE_DATA() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_FOR_INSURANCE" )
    void testGetAPI_FOR_FOR_INSURANCE() {
        assertNotNull( Config.getAPI_FOR_FOR_INSURANCE() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_VIOLATION_LIST" )
    void testGetAPI_FOR_VIOLATION_LIST() {
        assertNotNull( Config.getAPI_FOR_VIOLATION_LIST() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_DOVERENNOST_LIST" )
    void testGetAPI_FOR_DOVERENNOST_LIST() {
        assertNotNull( Config.getAPI_FOR_DOVERENNOST_LIST() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_MODEL_FOR_CAR_LIST" )
    void testGetAPI_FOR_MODEL_FOR_CAR_LIST() {
        assertNotNull( Config.getAPI_FOR_MODEL_FOR_CAR_LIST() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_PINPP" )
    void testGetAPI_FOR_PINPP() {
        assertNotNull( Config.getAPI_FOR_PINPP() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_BOARD_CROSSING" )
    void testGetAPI_FOR_BOARD_CROSSING() {
        assertNotNull( Config.getAPI_FOR_BOARD_CROSSING() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_CADASTR" )
    void testGetAPI_FOR_CADASTR() {
        assertNotNull( Config.getAPI_FOR_CADASTR() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_PERSON_IMAGE" )
    void testGetAPI_FOR_PERSON_IMAGE() {
        assertNotNull( Config.getAPI_FOR_PERSON_IMAGE() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_PASSPORT_MODEL" )
    void testGetAPI_FOR_PASSPORT_MODEL() {
        assertNotNull( Config.getAPI_FOR_PASSPORT_MODEL() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_MODEL_FOR_ADDRESS" )
    void testGetAPI_FOR_MODEL_FOR_ADDRESS() {
        assertNotNull( Config.getAPI_FOR_MODEL_FOR_ADDRESS() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_PERSON_DATA_FROM_ZAKS" )
    void testGetAPI_FOR_PERSON_DATA_FROM_ZAKS() {
        assertNotNull( Config.getAPI_FOR_PERSON_DATA_FROM_ZAKS() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_TRAIN_TICKET_CONSUMER_SERVICE" )
    void testGetAPI_FOR_TRAIN_TICKET_CONSUMER_SERVICE() {
        assertNotNull( Config.getAPI_FOR_TRAIN_TICKET_CONSUMER_SERVICE() );
    }

    @Test
    @DisplayName( value = "getBASE64_IMAGE_TO_LINK_CONVERTER_API" )
    void testGetBASE64_IMAGE_TO_LINK_CONVERTER_API() {
        assertNotNull( Config.getBASE64_IMAGE_TO_LINK_CONVERTER_API() );
    }

    @Test
    @DisplayName( value = "getKAFKA_BROKER" )
    void testGetKAFKA_BROKER() {
        assertNotNull( Config.getKAFKA_BROKER() );
    }

    @Test
    @DisplayName( value = "getGROUP_ID_FOR_KAFKA" )
    void testGetGROUP_ID_FOR_KAFKA() {
        assertNotNull( Config.getGROUP_ID_FOR_KAFKA() );
    }

    @Test
    @DisplayName( value = "getKAFKA_SENDER_MAX_IN_FLIGHT" )
    void testGetKAFKA_SENDER_MAX_IN_FLIGHT() {
        assertEquals( Config.getKAFKA_SENDER_MAX_IN_FLIGHT(), 9042 );
    }

    @Test
    @DisplayName( value = "getKAFKA_ACKS_CONFIG" )
    void testGetKAFKA_ACKS_CONFIG() {
        assertNotNull( Config.getKAFKA_ACKS_CONFIG() );
    }

    @Test
    @DisplayName( value = "getERROR_LOGS" )
    void testGetERROR_LOGS() {
        assertNotNull( Config.getERROR_LOGS() );
    }

    @Test
    @DisplayName( value = "getADMIN_PANEL" )
    void testGetADMIN_PANEL() {
        assertNotNull( Config.getADMIN_PANEL() );
    }

    @Test
    @DisplayName( value = "getADMIN_PANEL_ERROR_LOG" )
    void testGetADMIN_PANEL_ERROR_LOG() {
        assertNotNull( Config.getADMIN_PANEL_ERROR_LOG() );
    }

    @Test
    @DisplayName( value = "getHeaders" )
    void testGetHeaders() {
        assertFalse( Config.getHeaders().get().isEmpty() );
    }

    @Test
    @DisplayName( value = "getFields" )
    void testGetFields() {
        assertFalse( Config.getFields().get().isEmpty() );
    }
}