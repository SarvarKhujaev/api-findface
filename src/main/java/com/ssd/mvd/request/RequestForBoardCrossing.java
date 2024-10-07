package com.ssd.mvd.request;

import com.ssd.mvd.interfaces.RequestCommonMethods;

@SuppressWarnings(
        value = "используется как запрос для получения данных о пересечении границы"
)
public final class RequestForBoardCrossing implements RequestCommonMethods< RequestForBoardCrossing, String > {
    private String document;
    private String birth_date;

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public RequestForBoardCrossing generate ( @lombok.NonNull final String value ) {
        return new RequestForBoardCrossing( value );
    }

    private RequestForBoardCrossing ( @lombok.NonNull final String value ) {
        this.birth_date = value.split( "_" )[1];
        this.document = value.split( "_" )[0];
    }

    public RequestForBoardCrossing () {}
}
