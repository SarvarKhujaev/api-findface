package com.ssd.mvd.request;

// используется как запрос для получения данных о пересечении границы
public final class RequestForBoardCrossing {
    private final String document;
    private final String birth_date;
    private final Byte transaction_id = 1;

    private final Character langId = '1';
    private final Character is_consent = 'Y';

    public RequestForBoardCrossing ( final String value ) {
        this.birth_date = value.split( " " )[1];
        this.document = value.split( " " )[0]; }
}
