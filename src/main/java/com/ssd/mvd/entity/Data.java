package com.ssd.mvd.entity;

@lombok.Data
public class Data<T, V> {
    private String type;
    private V subject;
    private T data;
}
