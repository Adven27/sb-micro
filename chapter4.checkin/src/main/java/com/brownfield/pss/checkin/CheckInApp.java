package com.brownfield.pss.checkin;

import com.brownfield.pss.checkin.entity.CheckInRecord;
import com.brownfield.pss.checkin.repository.CheckinRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.Date;

@EnableDiscoveryClient
@SpringBootApplication
public class CheckInApp implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(CheckInApp.class);

    @Autowired
    CheckinRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(CheckInApp.class, args);
    }

    @Override
    public void run(String... strings) {
        CheckInRecord record = new CheckInRecord("Franc", "Gean", "28A", new Date(), "BF101", "22-JAN-16", 1);

        CheckInRecord result = repository.save(record);
        logger.info("checked in successfully ..." + result);


        logger.info("Looking to load checkedIn record...");
        logger.info("Result: " + repository.findOne(result.getId()));


    }
}
