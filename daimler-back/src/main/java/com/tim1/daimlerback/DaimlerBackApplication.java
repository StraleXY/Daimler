package com.tim1.daimlerback;

import com.tim1.daimlerback.repositories.IPassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DaimlerBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(DaimlerBackApplication.class, args);
    }

}
