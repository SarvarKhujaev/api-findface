package com.ssd.mvd.interfaces;

public interface KafkaCommonMethods {
    @lombok.NonNull
    String getTopicName();

    @lombok.NonNull
    String getSuccessMessage();

    @lombok.NonNull
    String getCompletedMessage();
}
