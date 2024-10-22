package com.ssd.mvd.interfaces;

import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Errors;
import com.ssd.mvd.inspectors.*;

import org.apache.commons.lang3.Validate;
import reactor.test.StepVerifier;
import org.junit.jupiter.api.*;

public final class EntityCommonMethodsTest extends WebFluxInspector {
    @BeforeEach
    void setUp() {
        Validate.notNull( EntitiesInstances.instancesList, StringOperations.NULL_VALUE_IN_ASSERT );
        Validate.noNullElements( EntitiesInstances.instancesList );
    }

    @AfterEach
    void tearDown() {
        System.gc();
    }

    @RepeatedTest( value = 10, name = "generate" )
    @DisplayName( value = "generate" )
    public void generate() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                atomicReference -> {
                                    Validate.notNull( atomicReference, StringOperations.NULL_VALUE_IN_ASSERT );
                                    Validate.notNull( atomicReference.get(), StringOperations.NULL_VALUE_IN_ASSERT );
                                    Validate.notNull(
                                            atomicReference.get().generate( ErrorResponse.builder().build() ),
                                            StringOperations.NULL_VALUE_IN_ASSERT
                                    );
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
                                    Validate.notNull(
                                            atomicReference.get().generate( ErrorResponse.builder().build() ),
                                            StringOperations.NULL_VALUE_IN_ASSERT
                                    );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verifyThenAssertThat()
        .tookLessThan( TimeInspector.DURATION );
    }

    @RepeatedTest( value = 10, name = "testGenerate" )
    @DisplayName( value = "testGenerate" )
    public void testGenerate() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                atomicReference -> {
                                    Validate.notNull( atomicReference, StringOperations.NULL_VALUE_IN_ASSERT );
                                    Validate.notNull( atomicReference.get(), StringOperations.NULL_VALUE_IN_ASSERT );
                                    Validate.notNull(
                                            atomicReference.get().generate( EMPTY, Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED ),
                                            StringOperations.NULL_VALUE_IN_ASSERT
                                    );
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
                                    Validate.notNull(
                                            atomicReference.get().generate( EMPTY, Errors.RESPONSE_FROM_SERVICE_NOT_RECEIVED ),
                                            StringOperations.NULL_VALUE_IN_ASSERT
                                    );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verifyThenAssertThat()
        .tookLessThan( TimeInspector.DURATION );
    }

    @RepeatedTest( value = 10, name = "testGenerate1" )
    @DisplayName( value = "testGenerate1" )
    public void testGenerate1() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                atomicReference -> {
                                    Validate.notNull( atomicReference, StringOperations.NULL_VALUE_IN_ASSERT );
                                    Validate.notNull( atomicReference.get(), StringOperations.NULL_VALUE_IN_ASSERT );
                                    Validate.notNull(
                                            atomicReference.get().generate( EMPTY ),
                                            StringOperations.NULL_VALUE_IN_ASSERT
                                    );
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
                                    Validate.notNull(
                                            atomicReference.get().generate( EMPTY ),
                                            StringOperations.NULL_VALUE_IN_ASSERT
                                    );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verifyThenAssertThat()
        .tookLessThan( TimeInspector.DURATION );
    }

    @RepeatedTest( value = 10, name = "setErrorResponse" )
    @DisplayName( value = "setErrorResponse" )
    public void setErrorResponse() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                atomicReference -> {
                                    Validate.notNull( atomicReference, StringOperations.NULL_VALUE_IN_ASSERT );
                                    Validate.notNull( atomicReference.get(), StringOperations.NULL_VALUE_IN_ASSERT );
                                    Validate.notNull(
                                            atomicReference.get().setErrorResponse( ErrorResponse.builder().build() ),
                                            StringOperations.NULL_VALUE_IN_ASSERT
                                    );
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
                                    Validate.notNull(
                                            atomicReference.get().setErrorResponse( ErrorResponse.builder().build() ),
                                            StringOperations.NULL_VALUE_IN_ASSERT
                                    );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verifyThenAssertThat()
        .tookLessThan( TimeInspector.DURATION );
    }

    @RepeatedTest( value = 10, name = "getMethodName" )
    @DisplayName( value = "getMethodName" )
    public void getMethodName() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                atomicReference -> {
                                    Validate.notNull( atomicReference, StringOperations.NULL_VALUE_IN_ASSERT );
                                    Validate.notNull( atomicReference.get(), StringOperations.NULL_VALUE_IN_ASSERT );
                                    Validate.notNull(
                                            atomicReference.get().getMethodName(),
                                            StringOperations.NULL_VALUE_IN_ASSERT
                                    );
                                    Validate.notNull(
                                            atomicReference.get().getMethodName().name(),
                                            StringOperations.NULL_VALUE_IN_ASSERT
                                    );
                                    Validate.notNull(
                                            atomicReference.get().getMethodName().getMethodApi(),
                                            StringOperations.NULL_VALUE_IN_ASSERT
                                    );

                                    Validate.isTrue(
                                            !atomicReference.get().getMethodName().name().isBlank()
                                    );

                                    Validate.isTrue(
                                            !atomicReference.get().getMethodName().getMethodApi().isBlank()
                                    );
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
                                    Validate.notNull(
                                            atomicReference.get().getMethodName(),
                                            StringOperations.NULL_VALUE_IN_ASSERT
                                    );
                                    Validate.notNull(
                                            atomicReference.get().getMethodName().name(),
                                            StringOperations.NULL_VALUE_IN_ASSERT
                                    );
                                    Validate.notNull(
                                            atomicReference.get().getMethodName().getMethodApi(),
                                            StringOperations.NULL_VALUE_IN_ASSERT
                                    );

                                    Validate.isTrue(
                                            !atomicReference.get().getMethodName().name().isBlank()
                                    );

                                    Validate.isTrue(
                                            !atomicReference.get().getMethodName().getMethodApi().isBlank()
                                    );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verifyThenAssertThat()
        .tookLessThan( TimeInspector.DURATION );
    }

    @RepeatedTest( value = 10, name = "testGenerate2" )
    @DisplayName( value = "testGenerate2" )
    public void testGenerate2() {
        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.instancesList,
                                atomicReference -> {
                                    Validate.notNull( atomicReference, StringOperations.NULL_VALUE_IN_ASSERT );
                                    Validate.notNull( atomicReference.get(), StringOperations.NULL_VALUE_IN_ASSERT );
                                    Validate.notNull(
                                            atomicReference.get().generate(),
                                            StringOperations.NULL_VALUE_IN_ASSERT
                                    );
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
                                    Validate.notNull(
                                            atomicReference.get().generate(),
                                            StringOperations.NULL_VALUE_IN_ASSERT
                                    );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verifyThenAssertThat()
        .tookLessThan( TimeInspector.DURATION );
    }
}