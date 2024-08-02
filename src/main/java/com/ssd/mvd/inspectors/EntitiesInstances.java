package com.ssd.mvd.inspectors;

import com.ssd.mvd.entity.modelForFioOfPerson.PersonTotalDataByFIO;
import com.ssd.mvd.entity.modelForPassport.ModelForPassport;
import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.entity.boardCrossing.CrossBoardInfo;
import com.ssd.mvd.request.RequestForBoardCrossing;
import com.ssd.mvd.entity.modelForCadastr.Data;
import com.ssd.mvd.entity.ModelForCarList;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.entity.modelForGai.*;
import com.ssd.mvd.entity.CarTotalData;
import com.ssd.mvd.kafka.Notification;
import com.ssd.mvd.entity.Pinpp;

import java.util.concurrent.Semaphore;

/*
хранит instance на все объекты
*/
public final class EntitiesInstances {
    public static final Data CADASTR = new Data();
    public static final Pinpp PINPP = new Pinpp();
    public static final Insurance INSURANCE = new Insurance();
    public static final Tonirovka TONIROVKA = new Tonirovka();
    public static final Semaphore SEMAPHORE = new Semaphore( 1 );
    public static final Notification NOTIFICATION = new Notification();
    public static final ModelForCar MODEL_FOR_CAR = new ModelForCar();
    public static final CarTotalData CAR_TOTAL_DATA = new CarTotalData();
    public static final PsychologyCard PSYCHOLOGY_CARD = new PsychologyCard();
    public static final ViolationsList VIOLATIONS_LIST = new ViolationsList();
    public static final CrossBoardInfo CROSS_BOARD_INFO = new CrossBoardInfo();
    public static final DoverennostList DOVERENNOST_LIST = new DoverennostList();
    public static final ModelForAddress MODEL_FOR_ADDRESS = new ModelForAddress();
    public static final ModelForCarList MODEL_FOR_CAR_LIST = new ModelForCarList();
    public static final ModelForPassport MODEL_FOR_PASSPORT = new ModelForPassport();
    public static final PersonTotalDataByFIO PERSON_TOTAL_DATA_BY_FIO = new PersonTotalDataByFIO();
    public static final RequestForBoardCrossing REQUEST_FOR_BOARD_CROSSING = new RequestForBoardCrossing();
}
