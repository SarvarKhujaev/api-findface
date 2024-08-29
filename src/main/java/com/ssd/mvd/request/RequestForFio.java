package com.ssd.mvd.request;

import com.ssd.mvd.interfaces.RequestCommonMethods;
import com.ssd.mvd.entity.modelForFioOfPerson.FIO;
import java.util.Locale;

public final class RequestForFio implements RequestCommonMethods< RequestForFio, FIO > {
    private String Name;
    private String Surname;
    private String Patronym;

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public RequestForFio generate ( @lombok.NonNull final FIO fio ) {
        return new RequestForFio( fio );
    }

    private RequestForFio ( final FIO fio ) {
        this.Patronym = fio.getPatronym().toUpperCase( Locale.ROOT );
        this.Surname = fio.getSurname().toUpperCase( Locale.ROOT );
        this.Name = fio.getName().toUpperCase( Locale.ROOT );
    }

    public RequestForFio () {}
}
