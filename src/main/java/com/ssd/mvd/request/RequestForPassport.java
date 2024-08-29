package com.ssd.mvd.request;

import com.ssd.mvd.interfaces.RequestCommonMethods;

public final class RequestForPassport implements RequestCommonMethods< RequestForPassport, String > {
    private String SerialNumber;
    private String BirthDate;

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public RequestForPassport generate ( @lombok.NonNull final String value ) {
        return new RequestForPassport( value );
    }

    private RequestForPassport ( final String value ) {
        this.SerialNumber = value.split( " " )[0];
        this.BirthDate = value.split( " " )[1];
    }

    public RequestForPassport () {}
}
