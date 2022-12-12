package com.ssd.mvd.entity.modelForGai;

import com.ssd.mvd.constants.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tonirovka {
    private String DateBegin;
    private String DateValid;
    private String TintinType;
    private String dateOfPermission;
    private String dateOfValidotion; // дата валидности разрешения, в случае если он просрочен пометить красным
    private String permissionLicense;
    private String whoGavePermission;
    private String organWhichGavePermission;

    private ErrorResponse errorResponse;

    public Tonirovka ( ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }
}
