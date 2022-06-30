package com.ssd.mvd;

import com.ssd.mvd.controller.SerDes;
import com.ssd.mvd.database.CassandraDataControl;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FindFaceServiceApplication {
    public static ApplicationContext context;

    public static void main( String[] args ) {
        System.out.println( SerDes.getSerDes().getModelForCarList( "31906832890014" ) );
//        CassandraDataControl.getInstance().resetData();
        context = SpringApplication.run( FindFaceServiceApplication.class, args );
    }
}
