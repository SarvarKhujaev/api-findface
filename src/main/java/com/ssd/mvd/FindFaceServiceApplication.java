package com.ssd.mvd;

import com.ssd.mvd.controller.SerDes;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FindFaceServiceApplication {
    public static ApplicationContext context;

    public static void main( String[] args ) {
//        new Thread( SerDes.getSerDes(), "serdes" ).start();
        context = SpringApplication.run( FindFaceServiceApplication.class, args ); }
}
