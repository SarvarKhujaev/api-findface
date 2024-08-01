package com.ssd.mvd.inspectors;

import com.ssd.mvd.entity.boardCrossing.CrossBoardInfo;
import com.ssd.mvd.entity.boardCrossing.CrossBoard;
import com.ssd.mvd.entity.modelForAddress.ModelForAddress;
import com.ssd.mvd.entity.modelForPassport.ModelForPassport;
import com.ssd.mvd.request.RequestForBoardCrossing;
import com.ssd.mvd.entity.modelForCadastr.Data;
import com.ssd.mvd.entity.Pinpp;

/*
хранит instance на все объекты
*/
public final class EntitiesInstances {
    public static final Data CADASTR = new Data();
    public static final Pinpp PINPP = new Pinpp();
    public static final CrossBoard CROSS_BOARD = new CrossBoard();
    public static final CrossBoardInfo CROSS_BOARD_INFO = new CrossBoardInfo();
    public static final ModelForAddress MODEL_FOR_ADDRESS = new ModelForAddress();
    public static final ModelForPassport MODEL_FOR_PASSPORT = new ModelForPassport();
    public static final RequestForBoardCrossing REQUEST_FOR_BOARD_CROSSING = new RequestForBoardCrossing();
}
