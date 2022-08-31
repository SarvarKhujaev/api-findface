package com.ssd.mvd.entity.modelForAddress;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ssd.mvd.entity.modelForPassport.RequestGuid;
import com.ssd.mvd.entity.PermanentRegistration;

import lombok.extern.jackson.Jacksonized;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;
import lombok.Data;

@Data
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ModelForAddress {
    private RequestGuid RequestGuid;
    private PermanentRegistration PermanentRegistration;
    @JsonDeserialize
    private List< com.ssd.mvd.entity.modelForAddress.TemproaryRegistration > TemproaryRegistration;
}