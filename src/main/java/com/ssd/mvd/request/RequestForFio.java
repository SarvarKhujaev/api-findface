package com.ssd.mvd.request;

import com.ssd.mvd.entity.modelForFioOfPerson.FIO;
import java.util.Locale;

public final class RequestForFio {
    private final String Name;
    private final String Surname;
    private final String Patronym;

    public RequestForFio ( final FIO fio ) {
        this.Patronym = fio.getPatronym().toUpperCase( Locale.ROOT );
        this.Surname = fio.getSurname().toUpperCase( Locale.ROOT );
        this.Name = fio.getName().toUpperCase( Locale.ROOT ); }
}
