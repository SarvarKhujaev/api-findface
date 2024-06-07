package com.ssd.mvd.request;

import com.ssd.mvd.interfaces.RequestCommonMethods;

public final class RequestForPassport implements RequestCommonMethods< RequestForPassport, String > {
    private String SerialNumber;
    private String BirthDate;

    @Override
    public RequestForPassport generate ( final String value ) {
        return new RequestForPassport( value );
    }

    private RequestForPassport ( final String value ) {
        this.SerialNumber = value.split( " " )[0];
        this.BirthDate = value.split( " " )[1];
    }

    public RequestForPassport () {}
}
