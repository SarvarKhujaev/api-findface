package com.ssd.mvd.entity.foreigner;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForeignerList {
    @JsonDeserialize
    private List< Foreigner > data;
}
