package com.ssd.mvd.entity.modelForFioOfPerson;

import com.ssd.mvd.entity.User;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class FIO {
    private User user;
    private String name;
    private String surname;
    private String patronym;
}
