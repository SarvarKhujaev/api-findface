package com.ssd.mvd.inspectors;

import com.ssd.mvd.entity.modelForFioOfPerson.PersonTotalDataByFIO;
import com.ssd.mvd.entity.modelForPassport.ModelForPassport;
import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.entity.boardCrossing.CrossBoardInfo;
import com.ssd.mvd.entity.response.ApiResponseModel;
import com.ssd.mvd.entity.modelForCadastr.Data;
import com.ssd.mvd.entity.boardCrossing.Person;

import com.ssd.mvd.entityForLogging.UserRequest;
import com.ssd.mvd.entityForLogging.PersonInfo;
import com.ssd.mvd.entityForLogging.ErrorLog;

import com.ssd.mvd.entity.response.Status;
import com.ssd.mvd.entity.response.User;

import com.ssd.mvd.entity.ModelForCarList;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.entity.modelForGai.*;
import com.ssd.mvd.entity.CarTotalData;
import com.ssd.mvd.entity.Pinpp;

import com.ssd.mvd.request.RequestForBoardCrossing;
import com.ssd.mvd.kafka.Notification;

import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.interfaces.KafkaCommonMethods;

import org.apache.commons.collections4.list.UnmodifiableList;
import org.apache.commons.collections4.set.UnmodifiableSet;

import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.channel.ConnectTimeoutException;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.Semaphore;
import java.lang.ref.WeakReference;

import java.util.List;
import java.util.Set;

@SuppressWarnings( value = "хранит instance на все объекты" )
@com.ssd.mvd.annotations.ImmutableEntityAnnotation
public final class EntitiesInstances {
    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized <T> WeakReference<T> generateWeakEntity ( @lombok.NonNull final T entity ) {
        return new WeakReference<>( entity );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized <T> AtomicReference<T> generateAtomicEntity ( @lombok.NonNull final T entity ) {
        return new AtomicReference<>( entity );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized ApiResponseModel generateResponse (
            @lombok.NonNull final String message
    ) {
        return API_RESPONSE_MODEL_ATOMIC_REFERENCE.getAndUpdate(
                apiResponseModel -> {
                    apiResponseModel.getStatus().setMessage( message );
                    return apiResponseModel;
                }
        );
    }

    @lombok.NonNull
    @lombok.Synchronized
    public static synchronized ApiResponseModel generateResponse (
            @lombok.NonNull final String message,
            @lombok.NonNull final User user
    ) {
        return API_RESPONSE_MODEL_ATOMIC_REFERENCE.getAndUpdate(
                apiResponseModel -> {
                    apiResponseModel.getStatus().setMessage( message );
                    apiResponseModel.setUser( user );
                    return apiResponseModel;
                }
        );
    }

    public static final AtomicReference< Semaphore > SEMAPHORE = generateAtomicEntity( new Semaphore( 1 ) );

    public static final AtomicReference< Data > CADASTR = generateAtomicEntity(
            AnnotationInspector.checkAnnotationIsNotImmutable( new Data( EntitiesInstances.class ) )
    );
    public static final AtomicReference< Pinpp > PINPP = generateAtomicEntity(
            AnnotationInspector.checkAnnotationIsNotImmutable( new Pinpp( EntitiesInstances.class ) )
    );
    public static final AtomicReference< Person > PERSON = generateAtomicEntity(
            AnnotationInspector.checkAnnotationIsNotImmutable( new Person( EntitiesInstances.class ) )
    );
    public static final AtomicReference< Insurance > INSURANCE = generateAtomicEntity(
            AnnotationInspector.checkAnnotationIsNotImmutable( new Insurance( EntitiesInstances.class ) )
    );
    public static final AtomicReference< Tonirovka > TONIROVKA = generateAtomicEntity(
            AnnotationInspector.checkAnnotationIsNotImmutable( new Tonirovka( EntitiesInstances.class ) )
    );
    public static final AtomicReference< Notification > NOTIFICATION = generateAtomicEntity(
            AnnotationInspector.checkAnnotationIsNotImmutable( new Notification( EntitiesInstances.class ) )
    );
    public static final AtomicReference< ModelForCar > MODEL_FOR_CAR = generateAtomicEntity(
            AnnotationInspector.checkAnnotationIsNotImmutable( new ModelForCar( EntitiesInstances.class ) )
    );
    public static final AtomicReference< CarTotalData > CAR_TOTAL_DATA = generateAtomicEntity(
            AnnotationInspector.checkAnnotationIsNotImmutable( new CarTotalData( EntitiesInstances.class ) )
    );
    public static final AtomicReference< PsychologyCard > PSYCHOLOGY_CARD = generateAtomicEntity(
            AnnotationInspector.checkAnnotationIsNotImmutable( new PsychologyCard( EntitiesInstances.class ) )
    );
    public static final AtomicReference< ViolationsList > VIOLATIONS_LIST = generateAtomicEntity(
            AnnotationInspector.checkAnnotationIsNotImmutable( new ViolationsList( EntitiesInstances.class ) )
    );
    public static final AtomicReference< CrossBoardInfo > CROSS_BOARD_INFO = generateAtomicEntity(
            AnnotationInspector.checkAnnotationIsNotImmutable( new CrossBoardInfo( EntitiesInstances.class ) )
    );
    public static final AtomicReference< DoverennostList > DOVERENNOST_LIST = generateAtomicEntity(
            AnnotationInspector.checkAnnotationIsNotImmutable( new DoverennostList( EntitiesInstances.class ) )
    );
    public static final AtomicReference< ModelForAddress > MODEL_FOR_ADDRESS = generateAtomicEntity(
            AnnotationInspector.checkAnnotationIsNotImmutable( new ModelForAddress( EntitiesInstances.class ) )
    );
    public static final AtomicReference< ModelForCarList > MODEL_FOR_CAR_LIST = generateAtomicEntity(
            AnnotationInspector.checkAnnotationIsNotImmutable( new ModelForCarList( EntitiesInstances.class ) )
    );
    public static final AtomicReference< ModelForPassport > MODEL_FOR_PASSPORT = generateAtomicEntity(
            AnnotationInspector.checkAnnotationIsNotImmutable( new ModelForPassport( EntitiesInstances.class ) )
    );
    public static final AtomicReference< PersonInfo > PERSON_INFO_ATOMIC_REFERENCE = generateAtomicEntity(
            AnnotationInspector.checkAnnotationIsNotImmutable( new PersonInfo( EntitiesInstances.class ) )
    );
    public static final AtomicReference< UserRequest > USER_REQUEST_ATOMIC_REFERENCE = generateAtomicEntity(
            AnnotationInspector.checkAnnotationIsNotImmutable( new UserRequest( EntitiesInstances.class ) )
    );
    public static final AtomicReference< PersonTotalDataByFIO > PERSON_TOTAL_DATA_BY_FIO = generateAtomicEntity(
            AnnotationInspector.checkAnnotationIsNotImmutable( new PersonTotalDataByFIO( EntitiesInstances.class ) )
    );
    public static final AtomicReference< RequestForBoardCrossing > REQUEST_FOR_BOARD_CROSSING = generateAtomicEntity(
            AnnotationInspector.checkAnnotationIsNotImmutable( new RequestForBoardCrossing( EntitiesInstances.class ) )
    );

    public static final AtomicReference< ApiResponseModel > API_RESPONSE_MODEL_ATOMIC_REFERENCE = generateAtomicEntity(
            ApiResponseModel
                    .builder()
                    .status( Status.builder().build() )
                    .build()
    );

    public static final WeakReference< ReadTimeoutException > READ_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE = generateWeakEntity(
            new ReadTimeoutException()
    );

    public static final WeakReference< ConnectTimeoutException > CONNECT_TIMEOUT_EXCEPTION_ATOMIC_REFERENCE = generateWeakEntity(
            new ConnectTimeoutException()
    );

    public static final WeakReference< IllegalArgumentException > ILLEGAL_ARGUMENT_EXCEPTION_ATOMIC_REFERENCE = generateWeakEntity(
            new IllegalArgumentException()
    );

    public static final UnmodifiableList< AtomicReference< ? extends EntityCommonMethods<?> > > instancesList = new UnmodifiableList<>(
            List.of(
                    PINPP,
                    PERSON,
                    CADASTR,
                    INSURANCE,
                    TONIROVKA,
                    MODEL_FOR_CAR,
                    CAR_TOTAL_DATA,
                    PSYCHOLOGY_CARD,
                    VIOLATIONS_LIST,
                    CROSS_BOARD_INFO,
                    DOVERENNOST_LIST,
                    MODEL_FOR_ADDRESS,
                    MODEL_FOR_CAR_LIST,
                    MODEL_FOR_PASSPORT,
                    PERSON_TOTAL_DATA_BY_FIO
            )
    );

    @SuppressWarnings( value = "хранит instance на все объекты связанные с задачами" )
    public static final Set< AtomicReference< ? extends KafkaCommonMethods> > kafkaEntities = UnmodifiableSet.unmodifiableSet(
            Set.of(
                    NOTIFICATION,
                    USER_REQUEST_ATOMIC_REFERENCE,
                    generateAtomicEntity( new ErrorLog( StringOperations.EMPTY ) )
            )
    );
}
