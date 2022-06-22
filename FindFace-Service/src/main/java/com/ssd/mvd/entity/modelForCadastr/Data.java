package com.ssd.mvd.entity.modelForCadastr;

import java.util.List;

@lombok.Data
public class Data {
    private List< Person > PermanentRegistration;
    private TemproaryRegistration TemproaryRegistration;
}
