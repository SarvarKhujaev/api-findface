package com.ssd.mvd.inspectors;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.DisplayName;

import org.apache.commons.lang3.Validate;
import reactor.test.StepVerifier;

public final class AvroSchemaInspectorTest extends CollectionsInspector {
    @DisplayName( value = "generateSchema" )
    @RepeatedTest( value = 10, name = "generateSchema" )
    public void generateSchema() {
        Validate.noNullElements( EntitiesInstances.kafkaEntities );

        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.kafkaEntities,
                                atomicReference -> {
                                    Validate.notNull( atomicReference, StringOperations.NULL_VALUE_IN_ASSERT );
                                    Validate.notNull( atomicReference.get(), StringOperations.NULL_VALUE_IN_ASSERT );

                                    Validate.notNull(
                                            AvroSchemaInspector.generateSchema( atomicReference.get() ),
                                            StringOperations.NULL_VALUE_IN_ASSERT
                                    );
                                    Validate.notBlank( AvroSchemaInspector.generateSchema( atomicReference.get() ).getDoc() );
                                    Validate.notBlank( AvroSchemaInspector.generateSchema( atomicReference.get() ).getName() );
                                    Validate.notBlank( AvroSchemaInspector.generateSchema( atomicReference.get() ).getNamespace() );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verify();
    }

    @DisplayName( value = "generateGenericRecord" )
    @RepeatedTest( value = 10, name = "generateGenericRecord" )
    public void generateGenericRecord() {
        Validate.noNullElements( EntitiesInstances.kafkaEntities );

        StepVerifier.create(
                WebFluxInspector.convertValuesToParallelFlux(
                        integer -> super.analyze(
                                EntitiesInstances.kafkaEntities,
                                atomicReference -> {
                                    Validate.notNull( atomicReference, StringOperations.NULL_VALUE_IN_ASSERT );
                                    Validate.notNull( atomicReference.get(), StringOperations.NULL_VALUE_IN_ASSERT );

                                    Validate.notNull(
                                            AvroSchemaInspector.generateGenericRecord( atomicReference.get() ),
                                            StringOperations.NULL_VALUE_IN_ASSERT
                                    );
                                    Validate.notNull(
                                            AvroSchemaInspector.generateGenericRecord( atomicReference.get() ).getSchema(),
                                            StringOperations.NULL_VALUE_IN_ASSERT
                                    );

                                    Validate.notBlank( AvroSchemaInspector.generateGenericRecord( atomicReference.get() ).getSchema().getDoc() );
                                    Validate.notBlank( AvroSchemaInspector.generateGenericRecord( atomicReference.get() ).getSchema().getName() );
                                    Validate.notBlank( AvroSchemaInspector.generateGenericRecord( atomicReference.get() ).getSchema().getNamespace() );
                                }
                        )
                )
        ).expectNextCount( WebFluxInspector.RESULT_COUNT )
        .expectComplete()
        .verify();
    }
}