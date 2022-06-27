package com.ssd.mvd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class FindFaceServiceApplication {
    public static ApplicationContext context;

    public static void main( String[] args ) {
//        CassandraDataControl.getInstance().resetData();
        context = SpringApplication.run( FindFaceServiceApplication.class, args ); }
}
