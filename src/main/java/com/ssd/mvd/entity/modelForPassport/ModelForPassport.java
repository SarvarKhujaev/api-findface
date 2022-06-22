package com.ssd.mvd.entity.modelForPassport;

import com.ssd.mvd.entity.modelForGai.ModelForCar;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ModelForPassport {
    private Integer AnswereId;
    private String AnswereMessage;
    private String AnswereComment;
    private List< ModelForCar > modelForCarList = new ArrayList<>(); // list of all cars of the current person
    private com.ssd.mvd.entity.modelForPassport.Data Data;
}
