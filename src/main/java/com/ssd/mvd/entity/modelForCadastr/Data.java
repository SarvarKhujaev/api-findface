package com.ssd.mvd.entity.modelForCadastr;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.extern.jackson.Jacksonized;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@lombok.Data
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class Data {
    @JsonDeserialize
    private List< Person > PermanentRegistration;
    private TemproaryRegistration TemproaryRegistration;
}
