package com.ssd.mvd.entity.modelForGai;

import com.ssd.mvd.constants.ErrorResponse;

public final class Tonirovka {
    private String DateBegin;
    private String DateValid;
    private String TintinType;
    private String dateOfPermission;
    // дата валидности разрешения, в случае если он просрочен пометить красным
    private String dateOfValidotion;
    private String permissionLicense;
    private String whoGavePermission;
    private String organWhichGavePermission;

    private ErrorResponse errorResponse;

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    public void setErrorResponse( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
    }

    public static Tonirovka generate ( final ErrorResponse errorResponse ) {
        return new Tonirovka( errorResponse );
    }

    private Tonirovka ( final ErrorResponse errorResponse ) {
        this.setErrorResponse( errorResponse );
    }
}
