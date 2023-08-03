package com.ssd.mvd.entity.foreigner;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class ForeignerList {
    @JsonDeserialize
    private List< Foreigner > data;
}
