package com.ssd.mvd.entity.family;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMember {
    private String pnfl;
    private String name;
    private String surname;
    private String doc_num;
    private String patronym;
    private String doc_date;
    private String birth_date;
    private String cert_series;
    private String cert_number;
    private String cert_birth_date;

    // Fathers data
    private String f_pnfl;
    private String f_family;
    private String f_patronym;
    private String f_birth_day;
    private String f_first_name;

    // Mothers data
    private String m_pnfl;
    private String m_family;
    private String m_patronym;
    private String m_birth_day;
    private String m_first_name;

    private Integer branch;
    private Integer gender_code;
}
