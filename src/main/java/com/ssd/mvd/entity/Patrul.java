package com.ssd.mvd.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import static com.ssd.mvd.constants.Status.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Date;
import java.util.UUID;
import java.util.Map;

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
    private String findFaceTask;
    private String passportNumber;
    private String patrulImageLink;
    private String surnameNameFatherName;

    private Map< String, String > listOfTasks = new HashMap<>(); // the list which will store ids of all tasks which have been completed by Patrul

    public Patrul changeTaskStatus ( com.ssd.mvd.constants.Status status ) {
        switch ( ( this.taskStatus = status ) ) {
            case ATTACHED -> this.setStatus( BUSY );
            case ACCEPTED -> {
                this.setStatus( BUSY );
                this.setTaskDate( new Date() ); // fixing time when patrul started this task
            } case FINISHED -> {
                this.setStatus( FREE );
                if ( this.getCard() != null ) {
                    this.getListOfTasks().putIfAbsent( this.getCard().toString(), "card" );
                    this.setCard( null );
                } else { this.getListOfTasks().putIfAbsent( this.getSelfEmploymentId().toString(), "selfEmployment" );
                    this.setSelfEmploymentId( null ); }
            } case ARRIVED -> {
                this.setTaskDate( new Date() );
                this.setStatus( ARRIVED ); }
        } return this; }

    private com.ssd.mvd.constants.Status status = com.ssd.mvd.constants.Status.FREE; // busy, free by default, available or not available
    private com.ssd.mvd.constants.Status taskStatus; // ths status of the Card or SelfEmployment
}
