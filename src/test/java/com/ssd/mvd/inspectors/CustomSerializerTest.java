package com.ssd.mvd.inspectors;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

public final class CustomSerializerTest extends CustomSerializer {
    @Test
    @DisplayName( value = "serialize" )
    void serialize() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                entity -> {
                                    assertNotNull( entity );
                                    assertNotNull( entity.get() );
                                    assertNotNull( entity.get().getMethodName().getMethodApi() );
                                    assertNotNull( entity.get().getMethodName() );

                                    assertTrue( super.checkString( entity.get().getMethodName().getMethodApi() ).isBlank() );
                                    assertTrue( super.checkString( entity.get().getMethodName().name() ).isBlank() );
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
                                entity -> {
                                    assertNotNull( serialize( entity ) );
                                    assertFalse( serialize( entity ).isBlank() );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT)
        .expectComplete()
        .verify();
    }

    @Test
    @DisplayName( value = "deserialize" )
    void deserialize() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                entity -> {
                                    final String temp = serialize( entity );

                                    assertNotNull( temp );
                                    assertFalse( temp.isBlank() );

                                    final Object object = deserialize( temp, entity.get().getClass() );

                                    assertNotNull( object );
                                    assertEquals( entity, object );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT)
        .expectComplete()
        .verify();
    }
}