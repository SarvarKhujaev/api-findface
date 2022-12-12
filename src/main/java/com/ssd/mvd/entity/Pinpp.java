package com.ssd.mvd.entity;

import com.ssd.mvd.constants.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pinpp {
    private String Data;
    private String Name;
    private String Pinpp;
    private String Region;
    private String Surname;
    private String Country;
    private String Address;
    private String District;
    private String Patronym;
    private String Cadastre;
    private String BirthDate;

    private ErrorResponse errorResponse;

    public Pinpp( ErrorResponse errorResponse ) { this.setErrorResponse( errorResponse ); }
}
