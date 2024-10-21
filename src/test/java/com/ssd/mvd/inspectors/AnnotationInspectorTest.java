package com.ssd.mvd.inspectors;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.DisplayName;

import org.apache.commons.lang3.Validate;
import reactor.test.StepVerifier;

public final class AnnotationInspectorTest extends AnnotationInspector {
    @RepeatedTest( value = 10, name = "checkCallerPermission" )
    @DisplayName( value = "checkCallerPermission" )
    public void checkCallerPermission () {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> {
                            super.analyze(
                                    EntitiesInstances.instancesList,
                                    atomicReference -> {
                                                assertNotNull( atomicReference );
                                                assertNotNull( atomicReference.get() );
                                                assertThrows(
                                                        RuntimeException.class,
                                                        () -> AnnotationInspector.checkCallerPermission(
                                                                AnnotationInspectorTest.class,
                                                                atomicReference.get().getClass()
                                                        )
                                                );
                                            }
                            );

                            super.analyze(
                                    EntitiesInstances.instancesList,
                                    atomicReference -> {
                                                assertNotNull( atomicReference );
                                                assertNotNull( atomicReference.get() );
                                                assertDoesNotThrow(
                                                        () -> AnnotationInspector.checkCallerPermission(
                                                                EntitiesInstances.class,
                                                                atomicReference.get().getClass()
                                                        )
                                                );
                                            }
                            );
                        }
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verify();

        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> {
                            super.analyze(
                                    EntitiesInstances.instancesList,
                                    atomicReference -> {
                                        assertNotNull( atomicReference );
                                        assertNotNull( atomicReference.get() );
                                        assertThrows(
                                                RuntimeException.class,
                                                () -> AnnotationInspector.checkCallerPermission(
                                                        AnnotationInspectorTest.class,
                                                        atomicReference.get().getClass()
                                                )
                                        );
                                    }
                            );

                            super.analyze(
                                    EntitiesInstances.instancesList,
                                    atomicReference -> {
                                        assertNotNull( atomicReference );
                                        assertNotNull( atomicReference.get() );
                                        assertDoesNotThrow(
                                                () -> AnnotationInspector.checkCallerPermission(
                                                        EntitiesInstances.class,
                                                        atomicReference.get().getClass()
                                                )
                                        );
                                    }
                            );
                        }
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verifyThenAssertThat()
        .tookLessThan( TimeInspector.DURATION );
    }
    @RepeatedTest( value = 10, name = "checkAnnotationIsNotImmutable" )
    @DisplayName( value = "checkAnnotationIsNotImmutable" )
    public void checkAnnotationIsNotImmutable() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                entity -> {
                                    assertDoesNotThrow(
                                            () -> checkAnnotationIsNotImmutable( entity.get() )
                                    );
                                    assertNotNull( checkAnnotationIsNotImmutable( entity.get() ) );
                                    assertEquals( checkAnnotationIsNotImmutable( entity.get() ), entity.get() );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verify();

        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                entity -> {
                                    assertDoesNotThrow(
                                            () -> checkAnnotationIsNotImmutable( entity.get() )
                                    );
                                    assertNotNull( checkAnnotationIsNotImmutable( entity.get() ) );
                                    assertEquals( checkAnnotationIsNotImmutable( entity.get() ), entity.get() );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verifyThenAssertThat()
        .tookLessThan( TimeInspector.DURATION );
    }

    @RepeatedTest( value = 10, name = "checkAnnotationIsImmutable" )
    @DisplayName( value = "checkAnnotationIsImmutable" )
    public void checkAnnotationIsImmutable() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                entity -> {
                                    assertDoesNotThrow(
                                            () -> checkAnnotationIsNotImmutable( entity.get() )
                                    );
                                    assertNotNull( checkAnnotationIsNotImmutable( entity.get() ) );
                                    assertEquals( checkAnnotationIsNotImmutable( entity.get() ), entity.get() );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verify();

        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                entity -> {
                                    assertDoesNotThrow(
                                            () -> checkAnnotationIsNotImmutable( entity.get() )
                                    );
                                    assertNotNull( checkAnnotationIsNotImmutable( entity.get() ) );
                                    assertEquals( checkAnnotationIsNotImmutable( entity.get() ), entity.get() );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verifyThenAssertThat()
        .tookLessThan( TimeInspector.DURATION );
    }

    @RepeatedTest( value = 10, name = "getFields" )
    @DisplayName( value = "getFields" )
    public void getFields() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                atomicReference -> {
                                    Validate.notNull( atomicReference, StringOperations.NULL_VALUE_IN_ASSERT );
                                    Validate.notNull( atomicReference.get(), StringOperations.NULL_VALUE_IN_ASSERT );

                                    Validate.isTrue( getFields( atomicReference.get().getClass() ).findAny().isPresent() );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verify();

        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                atomicReference -> {
                                    Validate.notNull( atomicReference, StringOperations.NULL_VALUE_IN_ASSERT );
                                    Validate.notNull( atomicReference.get(), StringOperations.NULL_VALUE_IN_ASSERT );

                                    Validate.isTrue( getFields( atomicReference.get().getClass() ).findAny().isPresent() );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verifyThenAssertThat()
        .tookLessThan( TimeInspector.DURATION );
    }

    @RepeatedTest( value = 10, name = "getMethods" )
    @DisplayName( value = "getMethods" )
    public void getMethods() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                atomicReference -> {
                                    Validate.notNull( atomicReference, StringOperations.NULL_VALUE_IN_ASSERT );
                                    Validate.notNull( atomicReference.get(), StringOperations.NULL_VALUE_IN_ASSERT );

                                    Validate.isTrue( getMethods( atomicReference.get().getClass() ).findAny().isPresent() );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verify();

        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                atomicReference -> {
                                    Validate.notNull( atomicReference, StringOperations.NULL_VALUE_IN_ASSERT );
                                    Validate.notNull( atomicReference.get(), StringOperations.NULL_VALUE_IN_ASSERT );

                                    Validate.isTrue( getMethods( atomicReference.get().getClass() ).findAny().isPresent() );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verifyThenAssertThat()
        .tookLessThan( TimeInspector.DURATION );
    }

    @RepeatedTest( value = 10, name = "clearAllEntities" )
    @DisplayName( value = "clearAllEntities" )
    public void clearAllEntities() {
        assertDoesNotThrow( super::clearAllEntities );
    }

    @RepeatedTest( value = 10, name = "clearEntity" )
    @DisplayName( value = "clearEntity" )
    public void clearEntity() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                atomicReference -> {
                                    Validate.notNull( atomicReference, StringOperations.NULL_VALUE_IN_ASSERT );
                                    Validate.notNull( atomicReference.get(), StringOperations.NULL_VALUE_IN_ASSERT );

                                    super.clearEntity( atomicReference.get() );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verify();

        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                atomicReference -> {
                                    Validate.notNull( atomicReference, StringOperations.NULL_VALUE_IN_ASSERT );
                                    Validate.notNull( atomicReference.get(), StringOperations.NULL_VALUE_IN_ASSERT );

                                    super.clearEntity( atomicReference.get() );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verifyThenAssertThat()
        .tookLessThan( TimeInspector.DURATION );
    }
}
