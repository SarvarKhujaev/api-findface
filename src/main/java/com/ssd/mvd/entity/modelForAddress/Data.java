package com.ssd.mvd.entity.modelForAddress;

import com.ssd.mvd.entity.modelForPassport.RequestGuid;

@lombok.Data
public class Data {
    private RequestGuid requestGuid;
    private PermanentRegistration PermanentRegistration;
    private com.ssd.mvd.entity.modelForCadastr.TemproaryRegistration TemproaryRegistration;
}
