package com.ssd.mvd.entity.modelForGai;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import com.ssd.mvd.annotations.WeakReferenceAnnotation;

import com.ssd.mvd.inspectors.CollectionsInspector;
import com.ssd.mvd.inspectors.AnnotationInspector;

import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.inspectors.CustomSerializer;

import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.constants.Methods;

import java.util.List;

public final class DoverennostList
        extends CustomSerializer
        implements EntityCommonMethods< DoverennostList > {
    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    public DoverennostList setErrorResponse( @lombok.NonNull final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
        return this;
    }

    public List< Doverennost > getDoverennostsList() {
        return this.doverennostsList;
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> this" )
    private DoverennostList setDoverennostsList( final List< Doverennost > doverennostsList ) {
        this.doverennostsList = doverennostsList;
        return this;
    }

    @WeakReferenceAnnotation( name = "errorResponse", isCollection = false )
    private ErrorResponse errorResponse;
    @WeakReferenceAnnotation( name = "doverennostsList" )
    private List< Doverennost > doverennostsList;

    @EntityConstructorAnnotation
    public <T> DoverennostList ( @lombok.NonNull final Class<T> instance ) {
        AnnotationInspector.checkCallerPermission( instance, DoverennostList.class );
        AnnotationInspector.checkAnnotationIsImmutable( DoverennostList.class );
    }

    private DoverennostList () {}

    @Override
    @lombok.NonNull
    public DoverennostList generate() {
        return new DoverennostList();
    }

    @Override
    @lombok.NonNull
    public Methods getMethodName() {
        return Methods.GET_DOVERENNOST_LIST;
    }

    @Override
    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> !null" )
    public DoverennostList generate(
            @lombok.NonNull final String response
    ) {
        return this.generate().setDoverennostsList( CustomSerializer.stringToArrayList( response, Doverennost[].class ) );
    }

    @Override
    public void close() {
        CollectionsInspector.checkAndClear( this.getDoverennostsList() );
    }
}
