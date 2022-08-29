package com.ssd.mvd.entity.modelForFioOfPerson;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FIO {
    private String name;
    private String surname;
    private String patronym;
}
