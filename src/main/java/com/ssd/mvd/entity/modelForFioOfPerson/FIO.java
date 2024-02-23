package com.ssd.mvd.entity.modelForFioOfPerson;

import com.ssd.mvd.entity.User;

public final class FIO {
    public String getName() {
        return this.name;
    }

    public void setName( final String name ) {
        this.name = name;
    }

    public String getSurname() {
        return this.surname;
    }

    public String getPatronym() {
        return this.patronym;
    }

    private User user;
    private String name;
    private String surname;
    private String patronym;
}
