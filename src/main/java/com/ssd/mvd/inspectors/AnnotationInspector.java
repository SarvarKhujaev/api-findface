package com.ssd.mvd.inspectors;

import com.ssd.mvd.annotations.EntityConstructorAnnotation;
import com.ssd.mvd.annotations.ImmutableEntityAnnotation;
import com.ssd.mvd.annotations.WeakReferenceAnnotation;

import com.ssd.mvd.interfaces.ServiceCommonMethods;
import com.ssd.mvd.constants.Errors;

import org.apache.commons.lang3.Validate;

import java.lang.reflect.Constructor;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

import java.util.stream.Stream;
import java.util.Collections;
import java.util.Collection;
import java.util.Map;

@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public class AnnotationInspector extends CustomServiceCleaner {
    protected static volatile WeakReferenceAnnotation weakReferenceAnnotation;

    protected AnnotationInspector () {
        super( AnnotationInspector.class );
    }

    @EntityConstructorAnnotation( permission = DataValidationInspector.class )
    protected <T extends UuidInspector> AnnotationInspector( @lombok.NonNull final Class<T> instance ) {
        super( AnnotationInspector.class );

        AnnotationInspector.checkCallerPermission( instance, AnnotationInspector.class );
        AnnotationInspector.checkAnnotationIsImmutable( AnnotationInspector.class );
    }

    @SuppressWarnings(
            value = """
                    Принимает любой Object и проверяет не является ли он Immutable
                    если все хорошо, то возвращает сам Object
                    """
    )
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> fail" )
    protected static synchronized < T > T checkAnnotationIsNotImmutable (
            @lombok.NonNull final T object
    ) {
        Validate.isTrue(
                !object.getClass().isAnnotationPresent( ImmutableEntityAnnotation.class )
        );

        return object;
    }

    @SuppressWarnings(
            value = """
                    Принимает класс и возвращает его экземпляры классов,
                    у которых есть доступ к конструктору вызванного объекта

                    Проверяет что у метода есть нужная аннотация
                    В случае ошибки вызывает Exception с подходящим сообщением
                    """
    )
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_, _ -> fail" )
    public static synchronized <T, U> void checkCallerPermission (
            // класс который обращается
            @lombok.NonNull final Class<T> callerInstance,
            // класс к которому обращаются
            @lombok.NonNull final Class<U> calledInstance
    ) {
        try {
            final Constructor<U> declaredConstructor = calledInstance.getDeclaredConstructor( Class.class );
            org.springframework.util.ReflectionUtils.makeAccessible( declaredConstructor );
            declaredConstructor.setAccessible( true );

            Validate.isTrue(
                    (
                            declaredConstructor.isAnnotationPresent( EntityConstructorAnnotation.class )
                                    && declaredConstructor.getParameters().length == 1
                                    && Collections.frequency(
                                    CollectionsInspector.convertArrayToList(
                                            declaredConstructor
                                                    .getAnnotation( EntityConstructorAnnotation.class )
                                                    .permission()
                                    ),
                                    callerInstance
                            ) > 0
                    ),
                    Errors.OBJECT_IS_OUT_OF_INSTANCE_PERMISSION.translate(
                            callerInstance.getName(),
                            calledInstance.getName()
                    )
            );
        } catch ( final NoSuchMethodException e ) {
            throw new RuntimeException(e);
        }
    }

    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> fail" )
    public static synchronized < T > void checkAnnotationIsImmutable (
            @lombok.NonNull final Class<T> object
    ) {
        Validate.isTrue(
                object.isAnnotationPresent( ImmutableEntityAnnotation.class ),
                Errors.OBJECT_IS_IMMUTABLE.translate( object.getName() )
        );
    }

    @SuppressWarnings(
            value = """
                    Принимает экземпляр класса и возвращает список всех его параметров
                    """
    )
    @lombok.NonNull
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> !null" )
    protected static synchronized Stream< Field > getFields (
            @lombok.NonNull final Class< ? > object
    ) {
        return convertArrayToList( object.getDeclaredFields() ).stream();
    }

    @SuppressWarnings(
            value = """
                    Принимает экземпляр класса и возвращает список всех его методов
                    """
    )
    @lombok.NonNull
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> !null" )
    protected static synchronized Stream< Method > getMethods (
            @lombok.NonNull final Class< ? > object
    ) {
        return convertArrayToList( object.getDeclaredMethods() ).stream();
    }

    @SuppressWarnings(
            value = """
                    Проверяет содержит ли параметр класса,
                    экземпляр класса имплементирующего интерфейс ServiceCommonMethods
                    """
    )
    @lombok.Synchronized
    @org.jetbrains.annotations.Contract( value = "_ -> true" )
    protected static synchronized boolean iSFieldMustBeCleaned (
            @lombok.NonNull final Field field
    ) {
        return field.getClass().getInterfaces().length == 1
                && field.getClass().getInterfaces()[0].isAssignableFrom( ServiceCommonMethods.class );
    }

    @SuppressWarnings(
            value = """
                    берет все статичные ссылки на объекты из сборника EntitiesInstances
                    и очищает каждый объект через вызов метода close из интерфейса ServiceCommonMethods
                    """
    )
    protected void clearAllEntities () {
        super.analyze(
                EntitiesInstances.instancesList,
                atomicReference -> super.analyze(
                        getFields( atomicReference.getClass() )
                                .filter( field -> field.isAnnotationPresent( WeakReferenceAnnotation.class ) )
                                .toList(),
                        field -> {
                            try {
                                org.springframework.util.ReflectionUtils.makeAccessible( field );
                                weakReferenceAnnotation = field.getAnnotation( WeakReferenceAnnotation.class );

                                Validate.isTrue(
                                        field.getName().compareTo( weakReferenceAnnotation.name() ) == 0,
                                        Errors.FIELD_AND_ANNOTATION_NAME_MISMATCH.translate(
                                                atomicReference.get().getClass().getName(),
                                                field.getName(),
                                                weakReferenceAnnotation.name()
                                        )
                                );

                                if ( weakReferenceAnnotation.isCollection() ) {
                                    checkAndClear(
                                            ( (Collection<?>) field.get( atomicReference.get() ) )
                                    );
                                } else if ( weakReferenceAnnotation.isWeak() ) {
                                    super.clearReference(
                                            ( (WeakReference<?>) field.get( atomicReference.get() ) )
                                    );
                                } else if ( weakReferenceAnnotation.isMap() ) {
                                    checkAndClear(
                                            ( (Map<?, ?>) field.get( atomicReference.get() ) )
                                    );
                                }

                                /*
                                если параметр является ссылкой на другой объект,
                                то просто стираем его через обозначение null
                                */
                                else {
                                    if ( iSFieldMustBeCleaned( field ) ) {
                                        super.clearReference( atomicReference );
                                    }

                                    field.set( atomicReference.get(), null );
                                }
                            } catch ( final IllegalAccessException e ) {
                                System.out.println( e.getMessage() );
                            }
                        }
                )
        );

        super.clearReference( EntitiesInstances.ILLEGAL_ARGUMENT_EXCEPTION_ATOMIC_REFERENCE );
        super.clearReference( EntitiesInstances.CONNECT_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE );
        super.clearReference( EntitiesInstances.READ_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE );
        super.clearReference( uuid );

        clearReference( EntitiesInstances.CAR_TOTAL_DATA.get() );
    }

    @SuppressWarnings(
            value = """
                    берет все статичные ссылки на объекты из сборника EntitiesInstances
                    и очищает каждый объект через вызов метода close из интерфейса ServiceCommonMethods
                    """
    )
    protected <T extends ServiceCommonMethods> void clearEntity ( @lombok.NonNull final T entity ) {
        super.analyze(
                getFields( entity.getClass() )
                        .filter( field -> field.isAnnotationPresent( WeakReferenceAnnotation.class ) )
                        .toList(),
                field -> {
                    try {
                        org.springframework.util.ReflectionUtils.makeAccessible( field );
                        weakReferenceAnnotation = field.getAnnotation( WeakReferenceAnnotation.class );

                        Validate.isTrue(
                                field.getName().compareTo( weakReferenceAnnotation.name() ) != 0,
                                Errors.FIELD_AND_ANNOTATION_NAME_MISMATCH.translate(
                                        entity.getClass().getName(),
                                        field.getName(),
                                        weakReferenceAnnotation.name()
                                )
                        );

                        if ( weakReferenceAnnotation.isCollection() ) {
                            checkAndClear(
                                    ( (Collection<?>) field.get( entity ) )
                            );
                        } else if ( weakReferenceAnnotation.isWeak() ) {
                            super.clearReference(
                                    ( (WeakReference<?>) field.get( entity ) )
                            );
                        } else if ( weakReferenceAnnotation.isMap() ) {
                            checkAndClear(
                                    ( (Map<?, ?>) field.get( entity ) )
                            );
                        }

                        /*
                        если параметр является ссылкой на другой объект,
                        то просто стираем его через обозначение null
                        */
                        else {
                            if ( iSFieldMustBeCleaned( field ) ) {
                                clearReference( entity );
                            }

                            field.set( entity, null );
                        }
                    } catch ( final IllegalAccessException e ) {
                        System.out.println( e.getMessage() );
                    }
                }
        );
    }
}
