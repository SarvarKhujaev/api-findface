package com.ssd.mvd.entity.modelForGai;

import com.ssd.mvd.constants.ErrorResponse;
import java.util.List;

@lombok.Data
public class DoverennostList {
    private ErrorResponse errorResponse;
    private List< Doverennost > doverennostsList;

    public DoverennostList ( final ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }

    public DoverennostList ( final List< Doverennost > doverennostsList ) { this.setDoverennostsList( doverennostsList ); }
}
