package com.ssd.mvd.kafka;

import com.mashape.unirest.http.JsonNode;
import java.util.Date;
import lombok.Data;

@Data
public class Notification {
    private Date callingTime;
    private JsonNode jsonNode;

    private String pinfl;
    private String reason;
    private String methodName;
}
