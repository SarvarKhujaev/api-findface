package com.ssd.mvd.inspectors;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import com.ssd.mvd.constants.Errors;
import com.ssd.mvd.entity.Pinpp;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public class StringOperations extends UuidInspector {
    @EntityConstructorAnnotation( permission = TimeInspector.class )
    protected <T extends UuidInspector> StringOperations( @lombok.NonNull final Class<T> instance ) {
        super( StringOperations.class );

        AnnotationInspector.checkCallerPermission( instance, StringOperations.class );
        AnnotationInspector.checkAnnotationIsImmutable( StringOperations.class );
    }
    public final static String EMPTY = "";
    public final static String SPACE = " ";
    protected final static String SPACE_WITH_DOUBLE_DOTS = " : ";

    public final static String AVRO_DATE_PATTERN = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$";
    public final static String NULL_VALUE_IN_ASSERT = "NULL VALUE WAS SENT";

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
