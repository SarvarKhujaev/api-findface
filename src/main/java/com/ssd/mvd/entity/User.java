package com.ssd.mvd.entity;

import java.util.List;
import java.util.UUID;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class User {
    private UUID id;

    private String role;
    private String pinfl;
    private String region;
    private String address;
    private String fullname;
    private String position;//lavozim
    private String birthDate;
    private String department;//tarkibiy tuzilma
    private String phoneNumber;
    private String militaryRank;//unvon
    private String userPhotoUrl;
    private String loginEndDate;
    private String loginStartDate;
    private String passportNumber;

    private List< String > district;
    private List< String > permissions;
}

