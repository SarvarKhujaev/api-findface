package com.ssd.mvd.interfaces;

import com.ssd.mvd.inspectors.AvroSchemaInspector;
import org.apache.avro.generic.GenericRecord;

public interface KafkaCommonMethods extends ServiceCommonMethods {
    @lombok.NonNull
    String getTopicName();

    @lombok.NonNull
    String getSuccessMessage();

    @lombok.NonNull
    default GenericRecord getEntityRecord() {
        return AvroSchemaInspector.generateGenericRecord( this );
    }

    @lombok.NonNull
    String getCompletedMessage();
}
