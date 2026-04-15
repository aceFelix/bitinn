package com.itniuma.bitinn;

import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author aceFelix
 */
@SpringBootApplication
@EnableFileStorage
@EnableScheduling
public class BitinnApplication {

    public static void main(String[] args) {
        SpringApplication.run(BitinnApplication.class, args);
    }
}
