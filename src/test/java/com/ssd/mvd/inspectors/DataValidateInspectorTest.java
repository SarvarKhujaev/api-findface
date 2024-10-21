package com.ssd.mvd.inspectors;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.DisplayName;

import reactor.test.StepVerifier;

public final class DataValidateInspectorTest extends DataValidationInspector {
    @RepeatedTest( value = 10, name = "objectIsNotNull" )
    @DisplayName( value = "objectIsNotNull" )
    public void objectIsNotNull() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                entity -> assertTrue( objectIsNotNull( entity ) )
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verify();

        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                entity -> assertTrue( objectIsNotNull( entity ) )
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verifyThenAssertThat()
        .tookLessThan( TimeInspector.DURATION );
    }

    @RepeatedTest( value = 10, name = "checkContextOrReturnDefaultValue" )
    @DisplayName( value = "checkContextOrReturnDefaultValue" )
    public void checkContextOrReturnDefaultValueTest() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                entity -> assertTrue( super.checkParam( generateTimeBased().toString() ) )
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verify();

        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                entity -> assertTrue( super.checkParam( generateTimeBased().toString() ) )
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verifyThenAssertThat()
        .tookLessThan( TimeInspector.DURATION );
    }
}