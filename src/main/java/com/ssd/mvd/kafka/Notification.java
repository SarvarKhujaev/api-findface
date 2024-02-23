package com.ssd.mvd.kafka;

import com.mashape.unirest.http.JsonNode;
import java.util.Date;

@lombok.Data
public final class Notification {
    private Date callingTime;
    private JsonNode jsonNode;

    private String pinfl;
    private String reason;
    private String methodName;
}
