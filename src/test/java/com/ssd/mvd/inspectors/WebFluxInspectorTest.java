package com.ssd.mvd.inspectors;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

public final class WebFluxInspectorTest extends WebFluxInspector {
    @Test
    @DisplayName( value = "convertValuesToParallelFluxWithMap" )
    void convertValuesToParallelFluxWithMap() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                entity -> {
                                    assertNotNull( entity );
                                    assertNotNull( entity.getMethodApi() );
                                    assertNotNull( entity.getMethodName() );

                                    assertTrue( super.checkString( entity.getMethodApi() ).isBlank() );
                                    assertTrue( super.checkString( entity.getMethodName().name() ).isBlank() );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT)
        .expectComplete()
        .verify();

        StepVerifier.create(
                super.convertValuesToParallelFluxWithMap(
                        EntitiesInstances.instancesList,
                        entity -> {
                                    assertNotNull( entity );
                                    assertNotNull( entity.getMethodApi() );
                                    assertNotNull( entity.getMethodName() );

                                    assertTrue( super.checkString( entity.getMethodApi() ).isBlank() );
                                    assertTrue( super.checkString( entity.getMethodName().name() ).isBlank() );

                                    return entity;
                        }
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT)
        .expectComplete()
        .verify();
    }

    @Test
    @DisplayName( value = "convertValuesToParallelFluxWithFilter" )
    void convertValuesToParallelFluxWithFilter() {
        StepVerifier.create(
                super.convertValuesToParallelFluxWithFilter(
                        EntitiesInstances.instancesList,
                        entity -> !super.checkString( entity.getMethodApi() ).isBlank()
                                && !super.checkString( entity.getMethodName().name() ).isBlank()
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT)
        .expectComplete()
        .verify();
    }
}