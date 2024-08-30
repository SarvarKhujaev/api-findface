package com.ssd.mvd.inspectors;

import com.ssd.mvd.constants.Errors;
import com.ssd.mvd.entity.Pinpp;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public class StringOperations {
    public final static String EMPTY = "";
    public final static String SPACE = " ";
    protected final static String SPACE_WITH_DOUBLE_DOTS = " : ";

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected final synchronized String checkString (
            @lombok.NonNull final String value
    ) {
        return !value.isBlank()
                ? value
                : EMPTY;
    }

    @lombok.NonNull
    @org.jetbrains.annotations.Contract( value = "_ -> _" )
    protected final synchronized String joinString ( @lombok.NonNull final Pinpp pinpp ) {
        return String.join(
                SPACE,
                this.checkString( pinpp.getName() ),
                this.checkString( pinpp.getSurname() ),
                this.checkString( pinpp.getPatronym() )
        );
    }

    @SuppressWarnings(
            value = "Prevent modification of the object's state"
    )
    @Override
    public Config clone() {
        throw new UnsupportedOperationException( Errors.OBJECT_IS_IMMUTABLE.getErrorMEssage( this.getClass().getName() ) );
    }
}
