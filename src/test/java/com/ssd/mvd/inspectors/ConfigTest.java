package com.ssd.mvd.inspectors;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public final class ConfigTest extends Config {
    @Test
    @DisplayName( value = "getAPI_FOR_GAI_TOKEN" )
    void testGetAPI_FOR_GAI_TOKEN() {
        assertNotNull( super.getAPI_FOR_GAI_TOKEN() );
    }

    @Test
    @DisplayName( value = "getLOGIN_FOR_GAI_TOKEN" )
    void testGetLOGIN_FOR_GAI_TOKEN() {
        assertNotNull( super.getLOGIN_FOR_GAI_TOKEN() );
    }

    @Test
    @DisplayName( value = "getCURRENT_SYSTEM_FOR_GAI" )
    void testGetCURRENT_SYSTEM_FOR_GAI() {
        assertNotNull( super.getCURRENT_SYSTEM_FOR_GAI() );
    }

    @Test
    @DisplayName( value = "getPASSWORD_FOR_GAI_TOKEN" )
    void testGetPASSWORD_FOR_GAI_TOKEN() {
        assertNotNull( super.getPASSWORD_FOR_GAI_TOKEN() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_TONIROVKA" )
    void testGetAPI_FOR_TONIROVKA() {
        assertNotNull( super.getAPI_FOR_TONIROVKA() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_VEHICLE_DATA" )
    void testGetAPI_FOR_VEHICLE_DATA() {
        assertNotNull( super.getAPI_FOR_VEHICLE_DATA() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_FOR_INSURANCE" )
    void testGetAPI_FOR_FOR_INSURANCE() {
        assertNotNull( super.getAPI_FOR_FOR_INSURANCE() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_VIOLATION_LIST" )
    void testGetAPI_FOR_VIOLATION_LIST() {
        assertNotNull( super.getAPI_FOR_VIOLATION_LIST() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_DOVERENNOST_LIST" )
    void testGetAPI_FOR_DOVERENNOST_LIST() {
        assertNotNull( super.getAPI_FOR_DOVERENNOST_LIST() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_MODEL_FOR_CAR_LIST" )
    void testGetAPI_FOR_MODEL_FOR_CAR_LIST() {
        assertNotNull( super.getAPI_FOR_MODEL_FOR_CAR_LIST() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_PINPP" )
    void testGetAPI_FOR_PINPP() {
        assertNotNull( super.getAPI_FOR_PINPP() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_BOARD_CROSSING" )
    void testGetAPI_FOR_BOARD_CROSSING() {
        assertNotNull( super.getAPI_FOR_BOARD_CROSSING() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_CADASTR" )
    void testGetAPI_FOR_CADASTR() {
        assertNotNull( super.getAPI_FOR_CADASTR() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_PERSON_IMAGE" )
    void testGetAPI_FOR_PERSON_IMAGE() {
        assertNotNull( super.getAPI_FOR_PERSON_IMAGE() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_PASSPORT_MODEL" )
    void testGetAPI_FOR_PASSPORT_MODEL() {
        assertNotNull( super.getAPI_FOR_PASSPORT_MODEL() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_MODEL_FOR_ADDRESS" )
    void testGetAPI_FOR_MODEL_FOR_ADDRESS() {
        assertNotNull( super.getAPI_FOR_MODEL_FOR_ADDRESS() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_PERSON_DATA_FROM_ZAKS" )
    void testGetAPI_FOR_PERSON_DATA_FROM_ZAKS() {
        assertNotNull( super.getAPI_FOR_PERSON_DATA_FROM_ZAKS() );
    }

    @Test
    @DisplayName( value = "getAPI_FOR_TRAIN_TICKET_CONSUMER_SERVICE" )
    void testGetAPI_FOR_TRAIN_TICKET_CONSUMER_SERVICE() {
        assertNotNull( super.getAPI_FOR_TRAIN_TICKET_CONSUMER_SERVICE() );
    }

    @Test
    @DisplayName( value = "getBASE64_IMAGE_TO_LINK_CONVERTER_API" )
    void testGetBASE64_IMAGE_TO_LINK_CONVERTER_API() {
        assertNotNull( super.getBASE64_IMAGE_TO_LINK_CONVERTER_API() );
    }

    @Test
    @DisplayName( value = "getKAFKA_BROKER" )
    void testGetKAFKA_BROKER() {
        assertNotNull( super.getKAFKA_BROKER() );
    }

    @Test
    @DisplayName( value = "getGROUP_ID_FOR_KAFKA" )
    void testGetGROUP_ID_FOR_KAFKA() {
        assertNotNull( super.getGROUP_ID_FOR_KAFKA() );
    }

    @Test
    @DisplayName( value = "getKAFKA_SENDER_MAX_IN_FLIGHT" )
    void testGetKAFKA_SENDER_MAX_IN_FLIGHT() {
        assertEquals( super.getKAFKA_SENDER_MAX_IN_FLIGHT(), 9042 );
    }

    @Test
    @DisplayName( value = "getKAFKA_ACKS_CONFIG" )
    void testGetKAFKA_ACKS_CONFIG() {
        assertNotNull( super.getKAFKA_ACKS_CONFIG() );
    }

    @Test
    @DisplayName( value = "getERROR_LOGS" )
    void testGetERROR_LOGS() {
        assertNotNull( super.getERROR_LOGS() );
    }

    @Test
    @DisplayName( value = "getADMIN_PANEL" )
    void testGetADMIN_PANEL() {
        assertNotNull( super.getADMIN_PANEL() );
    }

    @Test
    @DisplayName( value = "getADMIN_PANEL_ERROR_LOG" )
    void testGetADMIN_PANEL_ERROR_LOG() {
        assertNotNull( super.getADMIN_PANEL_ERROR_LOG() );
    }

    @Test
    @DisplayName( value = "getHeaders" )
    void testGetHeaders() {
        assertFalse( Config.getHeaders().isEmpty() );
    }

    @Test
    @DisplayName( value = "getFields" )
    void testGetFields() {
        assertFalse( Config.getFields().isEmpty() );
    }
}