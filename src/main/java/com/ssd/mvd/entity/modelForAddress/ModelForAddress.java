package com.ssd.mvd.entity.modelForAddress;

import com.ssd.mvd.entity.modelForCadastr.TemproaryRegistration;
import com.ssd.mvd.entity.modelForPassport.RequestGuid;
import com.ssd.mvd.entity.PermanentRegistration;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelForAddress {
    private RequestGuid RequestGuid;
    private PermanentRegistration PermanentRegistration;
    private TemproaryRegistration TemproaryRegistration;
}