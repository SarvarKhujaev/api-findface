package com.ssd.mvd.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties( ignoreUnknown = true )
public class Patrul {
    private Date taskDate; // for registration of exact time when patrul started to deal with task
    private Date lastActiveDate; // shows when user was online lastly
    private Date startedToWorkDate; // the time
    private Date dateOfRegistration = new Date();

    private UUID card;
    private UUID organ; // choosing from dictionary
    private UUID selfEmploymentId;
    @JsonDeserialize
    private Region region; // choosing from dictionary

    private Boolean inPolygon = false;
    private Long totalActivityTime = 0L;

    private String name;
    private String rank;
    private String email;
    private String token;
    private String surname;
    private String password;
    private String carNumber;
    private String policeType; // choosing from dictionary
    private String fatherName;
    private String dateOfBirth;
    private String phoneNumber;
    private String passportNumber;
    private String patrulImageLink;
    private String surnameNameFatherName;

    private com.ssd.mvd.constants.Status status = com.ssd.mvd.constants.Status.FREE; // busy, free by default, available or not available
    private com.ssd.mvd.constants.Status taskStatus; // ths status of the Card or SelfEmployment
}
