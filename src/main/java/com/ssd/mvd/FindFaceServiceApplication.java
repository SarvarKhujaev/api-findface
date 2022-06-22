package com.ssd.mvd;

import com.ssd.mvd.database.KafkaDataControl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FindFaceServiceApplication {

    public static void main( String[] args ) {
        KafkaDataControl.getInstance();
//        SpringApplication.run(FindFaceServiceApplication.class, args);
    }

}
