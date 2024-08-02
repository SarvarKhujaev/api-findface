package com.ssd.mvd.entity.modelForGai;

import com.ssd.mvd.interfaces.EntityCommonMethods;
import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.inspectors.Config;
import com.ssd.mvd.constants.Methods;
import com.ssd.mvd.constants.Errors;

public final class Tonirovka
        extends Config
        implements EntityCommonMethods< Tonirovka > {
    public String getDateBegin() {
        return this.DateBegin;
    }

    public String getDateValid() {
        return this.DateValid;
    }

    public String getTintinType() {
        return this.TintinType;
    }

    public String getDateOfPermission() {
        return this.dateOfPermission;
    }

    public String getDateOfValidotion() {
        return this.dateOfValidotion;
    }

    public String getPermissionLicense() {
        return this.permissionLicense;
    }

    public String getWhoGavePermission() {
        return this.whoGavePermission;
    }

    public String getOrganWhichGavePermission() {
        return this.organWhichGavePermission;
    }

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

    public Tonirovka setErrorResponse( final ErrorResponse errorResponse ) {
        this.errorResponse = errorResponse;
        return this;
    }

    public Tonirovka () {}

    @Override
    public Tonirovka generate(
            final String message,
            final Errors errors
    ) {
        return this.generate().setErrorResponse(
                super.error.apply(
                        message,
                        errors
                )
        );
    }

    @Override
    public Tonirovka generate() {
        return new Tonirovka();
    }

    @Override
    public Tonirovka generate(
            final String response
    ) {
        return super.deserialize( response, this.getClass() );
    }

    @Override
    public Tonirovka generate ( final ErrorResponse errorResponse ) {
        return this.generate().setErrorResponse( errorResponse );
    }

    @Override
    public String getMethodApi() {
        return super.getAPI_FOR_TONIROVKA();
    }

    @Override
    public Methods getMethodName() {
        return Methods.GET_TONIROVKA;
    }
}
