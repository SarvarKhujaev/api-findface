package com.ssd.mvd.request;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import com.ssd.mvd.interfaces.RequestCommonMethods;
import com.ssd.mvd.inspectors.AnnotationInspector;

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

    @EntityConstructorAnnotation
    public <T> RequestForBoardCrossing ( @lombok.NonNull final Class<T> instance ) {
        AnnotationInspector.checkCallerPermission( instance, RequestForBoardCrossing.class );
        AnnotationInspector.checkAnnotationIsImmutable( RequestForBoardCrossing.class );
    }

    private RequestForBoardCrossing ( @lombok.NonNull final String value ) {
        this.birth_date = value.split( "_" )[1];
        this.document = value.split( "_" )[0];
    }
}
