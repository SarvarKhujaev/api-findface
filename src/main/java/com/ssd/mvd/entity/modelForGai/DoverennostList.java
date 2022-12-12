package com.ssd.mvd.entity.modelForGai;

import com.ssd.mvd.constants.ErrorResponse;
import java.util.List;
import lombok.Data;

@Data
public class DoverennostList {
    private ErrorResponse errorResponse;
    private List< Doverennost > doverennostsList;

    public DoverennostList( ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }

    public DoverennostList ( List< Doverennost > doverennostsList ) { this.setDoverennostsList( doverennostsList ); }
}
