package com.brownfield.pss.book;

import com.brownfield.pss.book.component.BookingComponent;
import com.brownfield.pss.book.entity.BookingRecord;
import com.brownfield.pss.book.entity.Inventory;
import com.brownfield.pss.book.entity.Passenger;
import com.brownfield.pss.book.repository.BookingRepository;
import com.brownfield.pss.book.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class BookApp implements CommandLineRunner{
	private static final Logger logger = LoggerFactory.getLogger(BookApp.class);
	@Autowired
	private BookingRepository bookingRepository;
	
	@Autowired
	private BookingComponent bookingComponent;
	
	@Autowired
	InventoryRepository inventoryRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(BookApp.class, args);
	}

	@Override
	public void run(String... strings) {
		
		Inventory[] invs = { 
					new Inventory("BF100", "22-JAN-16", 100),
					new Inventory("BF101", "22-JAN-16", 100),
					new Inventory("BF102", "22-JAN-16", 100),
					new Inventory("BF103", "22-JAN-16", 100),
					new Inventory("BF104", "22-JAN-16", 100),
					new Inventory("BF105", "22-JAN-16", 100),
					new Inventory("BF106", "22-JAN-16", 100)};
		Arrays.asList(invs).forEach(inventory -> inventoryRepository.save(inventory));
				
		 
		
		BookingRecord booking = new BookingRecord("BF101", "NYC","SFO","22-JAN-16",new Date(),"101");
		Set<Passenger> passengers = new HashSet<>();
		passengers.add(new Passenger("Gean","Franc","Male", booking));
	//	passengers.add(new Passenger("Redi","Ivan","Female",booking));
	 	
		booking.setPassengers(passengers);
 		long record  = bookingComponent.book(booking);
		logger.info("Booking successfully saved..." + record);
		
		logger.info("Looking to load booking record..."); 
	    logger.info("Result: " + bookingComponent.getBooking(record));
 
	    
	}
	
}
