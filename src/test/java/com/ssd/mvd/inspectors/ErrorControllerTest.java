package com.ssd.mvd.inspectors;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.ssd.mvd.kafka.KafkaDataControl;
import com.ssd.mvd.constants.Errors;
import reactor.test.StepVerifier;

public final class ErrorControllerTest extends ErrorController {
    @BeforeEach
    void setUp() {
        assertNotNull( KafkaDataControl.getKafkaDataControl() );
    }

    @AfterEach
    void tearDown() {
        KafkaDataControl.getKafkaDataControl().close();
    }

    @Test
    @DisplayName( value = "saveErrorLog" )
    void saveErrorLog() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                entity -> StepVerifier.create(
                                        super.saveErrorLog(
                                                Errors.SERVICE_WORK_ERROR.name(),
                                                entity
                                        )
                                ).expectNextCount( 1 )
                                .expectComplete()
                                .verify()
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT)
        .expectComplete()
        .verify();
    }

    @Test
    @DisplayName( value = "testSaveErrorLog" )
    void testSaveErrorLog() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                entity -> {
                                    assertNotNull( entity );
                                    super.saveErrorLog(
                                            entity.getMethodName(),
                                            Errors.SERVICE_WORK_ERROR.name(),
                                            Errors.DATA_NOT_FOUND.name()
                                    );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT)
        .expectComplete()
        .verify();
    }

    @Test
    @DisplayName( value = "testSaveErrorLog1" )
    void testSaveErrorLog1() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                entity -> {
                                    assertNotNull( entity );
                                    super.saveErrorLog( entity.getMethodName().name() );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT)
        .expectComplete()
        .verify();
    }

    @Test
    @DisplayName( value = "completeError" )
    void completeError() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                super::completeError
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT)
        .expectComplete()
        .verify();
    }

    @Test
    @DisplayName( value = "testCompleteError" )
    void testCompleteError() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                entity -> {
                                    assertNotNull( entity );
                                    StepVerifier.create(
                                            super.completeError(
                                                    new IllegalArgumentException( entity.getMethodName().name() ),
                                                    Errors.SERVICE_WORK_ERROR
                                            )
                                    ).expectNextCount( 1 )
                                    .expectComplete()
                                    .verify();
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT)
        .expectComplete()
        .verify();
    }

    @Test
    @DisplayName( value = "testCompleteError1" )
    void testCompleteError1() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                entity -> {
                                    assertNotNull( entity );
                                    StepVerifier.create(
                                            super.completeError(
                                                    new IllegalArgumentException( entity.getMethodName().name() ),
                                                    entity
                                            )
                                    ).expectNextCount( 1 )
                                    .expectComplete()
                                    .verify();
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT)
        .expectComplete()
        .verify();
    }
}