package com.ssd.mvd.entity.modelForFioOfPerson;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import com.ssd.mvd.entity.User;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FIO {
    private User user;
    private String name;
    private String surname;
    private String patronym;
}
