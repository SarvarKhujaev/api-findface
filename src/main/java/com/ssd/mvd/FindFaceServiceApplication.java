package com.ssd.mvd;

import com.ssd.mvd.controller.SerDes;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FindFaceServiceApplication {
    public static ApplicationContext context;

    public static void main( String[] args ) {
        String data = "AA6088411_25.05.1998";
        String[] strings = data.split( "_" );
        System.out.println( strings[0] + " " + strings[1] );
        System.out.println( SerDes.getSerDes().getPsychologyCard( SerDes.getSerDes().deserialize( strings[ 0 ], strings[ 1 ] ) ) );
//        new Thread( SerDes.getSerDes(), "serdes" ).start();
//        context = SpringApplication.run( FindFaceServiceApplication.class, args );
    }
}
