package com.ssd.mvd.entityForLogging;

public enum IntegratedServiceApis {
    DTP("DTP", ""),
    EMI_DB("EMI", "DB"),
    ITS_QUROL("ИЦ", "qurol"),
    ITS_BILET("ИЦ", "bilet"),
    GTSP_WEBHOOK("ГЦП", "Webhook"),
    E_MEHMON("E-mehmon", "Webhook"),
    ITS_SUDLANGAN("ИЦ", "sudlangan"),
    ITS_PROF_UCHET("ИЦ", "prof uchet"),
    IJTIMOIY_HOLAT("IJTIMOIY HOLAT", ""),
    SERVICE_102_WEBHOOK("102", "Webhook"),
    ITS_JINOIY("ИЦ", "jinoiy statistika"),
    FIND_FACE_WEBHOOK("FINDFACE", "Webhook"),
    ITS_QIDIRUV_AVTO("ИЦ", "qidiruvdagi avto"),
    ITS_QIDIRUV_SHAXS("ИЦ", "qidiruvdagi shaxs"),

    GAI("GAI", "gai"),
    OVIR("OVIR", "ovir"),
    PAPILON("PAPILON", "papilon");

    IntegratedServiceApis ( final String name, final String description ) {
        this.description = description;
        this.name = name;
    }

    private final String name;
    private final String description;

    public String getName () {
        return name;
    }

    public String getDescription () {
        return description;
    }
}
