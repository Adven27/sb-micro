package com.brownfield.pss.book.controller;

import com.brownfield.pss.book.component.BookingComponent;
import com.brownfield.pss.book.component.BookingStatus;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

    BookingComponent bookingComponent;

    @Autowired
    public Receiver(BookingComponent bookingComponent) {
        this.bookingComponent = bookingComponent;
    }

    @RabbitListener(queues = "CheckINQ")
    public void processMessage(long bookingID) {
        System.out.println(bookingID);
        bookingComponent.updateStatus(BookingStatus.INSTANCE.getCHECKED_IN(), bookingID);
    }

}