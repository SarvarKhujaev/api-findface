package com.ssd.mvd.entity.modelForFioOfPerson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.extern.jackson.Jacksonized;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;
import lombok.Data;

@Data
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties( ignoreUnknown = true )
public class PersonTotalDataByFIO {
    private Integer AnswereId;
    private String AnswereMessage;
    private String AnswereComment;
    @JsonDeserialize
    private List< Person > Data;
}
