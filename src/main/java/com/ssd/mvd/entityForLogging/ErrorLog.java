package com.ssd.mvd.entityForLogging;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorLog {
    private String url;
    private String integratedService;
    private String integratedServiceApiDescription;

    private List< Item > itemList;
}