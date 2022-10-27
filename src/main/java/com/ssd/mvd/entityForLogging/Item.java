package com.ssd.mvd.entityForLogging;

import lombok.Builder;
import java.util.Date;
import lombok.Data;

@Data
@Builder
public class Item {
    private String content;
    private Date dataReceivedAt;
    private Date retrievedDataAt;
}
