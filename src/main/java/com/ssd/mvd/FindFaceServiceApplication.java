package com.ssd.mvd;

import com.ssd.mvd.controller.SerDes;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FindFaceServiceApplication {
    public final static ApplicationContext context = SpringApplication.run( FindFaceServiceApplication.class );

    public static void main( final String[] args ) { SerDes.getSerDes(); }
}
