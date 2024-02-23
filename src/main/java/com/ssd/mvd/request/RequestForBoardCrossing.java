package com.ssd.mvd.request;

// используется как запрос для получения данных о пересечении границы
public final class RequestForBoardCrossing {
    private final String document;
    private final String birth_date;
    private final Byte transaction_id = 1;

    private final Character langId = '1';
    private final Character is_consent = 'Y';

    public static RequestForBoardCrossing generate ( final String value ) {
        return new RequestForBoardCrossing( value );
    }

    private RequestForBoardCrossing ( final String value ) {
        this.birth_date = value.split( "_" )[1];
        this.document = value.split( "_" )[0];
    }
}
