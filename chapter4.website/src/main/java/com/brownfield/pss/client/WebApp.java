package com.brownfield.pss.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@EnableDiscoveryClient
@EnableGlobalMethodSecurity
@SpringBootApplication
public class WebApp implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(WebApp.class);

    @Autowired
    RestTemplate searchClient;
    @Autowired
    RestTemplate bookingClient;
    @Autowired
    RestTemplate checkInClient;

    public static void main(String[] args) {
        SpringApplication.run(WebApp.class, args);
    }

    @Override
    public void run(String... strings) {
        Flight[] flights = searchClient.postForObject("http://search-service/search/get", new SearchQuery("NYC", "SFO", "22-JAN-16"), Flight[].class);
        Arrays.asList(flights).forEach(flight -> logger.info(" flight >" + flight));

        if (flights.length == 0) {
            return;
        }
        Flight flight = flights[0];
        BookingRecord booking = new BookingRecord(flight.getFlightNumber(), flight.getOrigin(),
                flight.getDestination(), flight.getFlightDate(), null,
                flight.getFares().getFare());
        Set<Passenger> passengers = new HashSet<Passenger>();
        passengers.add(new Passenger("Gavin", "Franc", "Male", booking));
        booking.setPassengers(passengers);
        long bookingId = 0;
        try {
            bookingId = bookingClient.postForObject("http://book-service/booking/create", booking, long.class);
            logger.info("Booking created " + bookingId);
        } catch (Exception e) {
            logger.error("BOOKING SERVICE NOT AVAILABLE...!!!");
        }

        //check in passenger
        if (bookingId == 0) return;
        try {
            CheckInRecord checkIn = new CheckInRecord("Franc", "Gavin", "28C", null, "BF101", "22-JAN-16", bookingId);
            long checkinId = checkInClient.postForObject("http://checkin-service/checkin/create", checkIn, long.class);
            logger.info("Checked IN " + checkinId);
        } catch (Exception e) {
            logger.error("CHECK IN SERVICE NOT AVAILABLE...!!!");
        }
    }

    @Configuration
    static class AppCfg {
        @LoadBalanced
        @Bean
        RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }
}