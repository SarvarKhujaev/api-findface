package com.ssd.mvd.entity.modelForGai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
