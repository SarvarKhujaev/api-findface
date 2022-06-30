package com.ssd.mvd.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ssd.mvd.entity.modelForGai.ModelForCar;
import lombok.extern.jackson.Jacksonized;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class ModelForCarList {
    @JsonDeserialize
    private List< ModelForCar > modelForCarList;
}
