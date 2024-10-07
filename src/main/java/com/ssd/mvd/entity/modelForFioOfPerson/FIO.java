package com.ssd.mvd.entity.modelForFioOfPerson;

import com.ssd.mvd.entity.response.User;

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

    public void setSurname(
            final String surname
    ) {
        this.surname = surname;
    }

    public void setPatronym(
            final String patronym
    ) {
        this.patronym = patronym;
    }

    private User user;
    private String name;

    private String surname;
    private String patronym;
}
