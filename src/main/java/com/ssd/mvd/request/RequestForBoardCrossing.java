package com.ssd.mvd.request;

import com.ssd.mvd.interfaces.RequestCommonMethods;

// используется как запрос для получения данных о пересечении границы
public final class RequestForBoardCrossing implements RequestCommonMethods< RequestForBoardCrossing, String > {
    private String document;
    private String birth_date;
    private final byte transaction_id = 1;

    private final char langId = '1';
    private final char is_consent = 'Y';

    @Override
    public RequestForBoardCrossing generate ( final String value ) {
        return new RequestForBoardCrossing( value );
    }

    private RequestForBoardCrossing ( final String value ) {
        this.birth_date = value.split( "_" )[1];
        this.document = value.split( "_" )[0];
    }

    public RequestForBoardCrossing () {}
}
