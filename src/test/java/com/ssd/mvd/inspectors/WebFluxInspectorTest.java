package com.ssd.mvd.inspectors;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.apache.commons.lang3.Validate;
import reactor.test.StepVerifier;

public final class WebFluxInspectorTest extends WebFluxInspector {
    @Test
    @DisplayName( value = "convertValuesToParallelFluxWithMap" )
    void convertValuesToParallelFluxWithMap() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                atomicReference -> {
                                    assertNotNull( atomicReference );
                                    assertNotNull( atomicReference.get().getMethodName() );
                                    assertNotNull( atomicReference.get().getMethodName().getMethodApi() );

                                    assertTrue( super.checkString( atomicReference.get().getMethodName().name() ).isBlank() );
                                    assertTrue( super.checkString( atomicReference.get().getMethodName().getMethodApi() ).isBlank() );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT)
        .expectComplete()
        .verify();

        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                atomicReference -> {
                                    assertNotNull( atomicReference );
                                    assertNotNull( atomicReference.get().getMethodName().getMethodApi() );
                                    assertNotNull( atomicReference.get().getMethodName() );

                                    assertTrue( super.checkString( atomicReference.get().getMethodName().getMethodApi() ).isBlank() );
                                    assertTrue( super.checkString( atomicReference.get().getMethodName().name() ).isBlank() );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT)
        .expectComplete()
        .verify();
    }

    @Test
    @DisplayName( value = "convertValuesToParallelFluxWithFilter" )
    void convertValuesToParallelFluxWithFilter() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                atomicReference -> Validate.isTrue(
                                        !super.checkString( atomicReference.get().getMethodName().getMethodApi() ).isBlank()
                                                && !super.checkString( atomicReference.get().getMethodName().name() ).isBlank()
                                )
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT)
        .expectComplete()
        .verify();
    }
}