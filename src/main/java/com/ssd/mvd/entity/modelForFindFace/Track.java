package com.ssd.mvd.entity.modelForFindFace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Track {
    private String first_timestamp;
    private String last_timestamp;
    private String id;
    private Car car;
}