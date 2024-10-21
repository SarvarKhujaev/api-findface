package com.ssd.mvd.inspectors;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;

import org.apache.commons.lang3.Validate;
import reactor.test.StepVerifier;

public final class CollectionsInspectorTest extends CollectionsInspector {
    @AfterEach
    void tearDown() {
        System.gc();
    }

    @RepeatedTest( value = 10, name = "newListTest" )
    @DisplayName( value = "newListTest" )
    public void newListTest() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> Validate.isTrue( super.newList().isEmpty() )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verify();

        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> Validate.isTrue( super.newList().isEmpty() )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verifyThenAssertThat()
        .tookLessThan( TimeInspector.DURATION );
    }

    @RepeatedTest( value = 10, name = "newMapTest" )
    @DisplayName( value = "newMapTest" )
    public void newMapTest() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> Validate.isTrue( newMap().isEmpty() )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verify();

        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> Validate.isTrue( newMap().isEmpty() )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verifyThenAssertThat()
        .tookLessThan( TimeInspector.DURATION );
    }

    @RepeatedTest( value = 10, name = "analyzeTest" )
    @DisplayName( value = "analyzeTest" )
    public void analyzeTest() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                atomicReference -> {
                                    Validate.notNull( atomicReference, StringOperations.NULL_VALUE_IN_ASSERT );
                                    Validate.notNull( atomicReference.get(), StringOperations.NULL_VALUE_IN_ASSERT );
                                    Validate.notBlank( atomicReference.get().toString() );
                                    Validate.isTrue( isUUIDValid( atomicReference.get().toString() ) );
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
                                    Validate.notBlank( atomicReference.get().toString() );
                                    Validate.isTrue( isUUIDValid( atomicReference.get().toString() ) );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verifyThenAssertThat()
        .tookLessThan( TimeInspector.DURATION );
    }

    @RepeatedTest( value = 10, name = "isCollectionNotEmptyTest" )
    @DisplayName( value = "isCollectionNotEmptyTest" )
    public void isCollectionNotEmptyTest() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> {
                            Validate.isTrue(
                                    !isCollectionNotEmpty( newMap() )
                            );

                            Validate.isTrue(
                                    !isCollectionNotEmpty( super.newList() )
                            );
                        }
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verify();

        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> {
                            Validate.isTrue(
                                    !isCollectionNotEmpty( newMap() )
                            );

                            Validate.isTrue(
                                    !isCollectionNotEmpty( super.newList() )
                            );
                        }
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verifyThenAssertThat()
        .tookLessThan( TimeInspector.DURATION );
    }
}