package com.ssd.mvd.entityForLogging;

public enum IntegratedServiceApis {
    OVIR("OVIR", "ovir");

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
