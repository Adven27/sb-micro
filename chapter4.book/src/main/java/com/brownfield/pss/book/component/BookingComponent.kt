package com.brownfield.pss.book.component

import com.brownfield.pss.book.entity.BookingRecord
import com.brownfield.pss.book.entity.Inventory
import com.brownfield.pss.book.repository.BookingRepository
import com.brownfield.pss.book.repository.InventoryRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.cloud.netflix.ribbon.RibbonClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.*

@Component
class BookingComponent @Autowired constructor(
    var bookingRepository: BookingRepository,
    var sender: Sender,
    var inventoryRepository: InventoryRepository,
    var fareClient: FareClient
) {

    fun book(record: BookingRecord): Long {
        if (hasValidFare(record)) {
            val inventory = update(inventoryFor(record), record.passengers.size)
            val id = saveBooking(record)
            publishEvent(record, inventory)
            return id
        }
        throw BookingException("fare is tampered")
    }

    private fun hasValidFare(record: BookingRecord) =
        record.fare == fareClient.fare(record.flightNumber, record.flightDate).fare

    private fun publishEvent(record: BookingRecord, inventory: Inventory) {
        logger.info("sending a booking event")
        val details = mapOf(
            "FLIGHT_NUMBER" to record.flightNumber,
            "FLIGHT_DATE" to record.flightDate,
            "NEW_INVENTORY" to inventory.bookableInventory
        )
        sender.send(details)
        logger.info("booking event successfully delivered $details")
    }

    private fun saveBooking(record: BookingRecord) = bookingRepository.save(record.apply {
        status = BookingStatus.BOOKING_CONFIRMED
        passengers.forEach { it.bookingRecord = record }
        bookingDate = Date()
    }).id

    private fun update(inventory: Inventory, passangers: Int): Inventory =
        inventoryRepository.saveAndFlush(inventory.apply { available -= passangers })

    private fun inventoryFor(record: BookingRecord): Inventory {
        val inventory = inventoryRepository.findByFlightNumberAndFlightDate(record.flightNumber, record.flightDate)
        if (!inventory.isAvailable(record.passengers.size)) {
            throw BookingException("No more seats avaialble")
        }
        return inventory
    }

    fun getBooking(id: Long): BookingRecord = bookingRepository.findOne(id)

    fun updateStatus(status: String, bookingId: Long) {
        val record = bookingRepository.findOne(bookingId)
        record.status = status
    }

    companion object {
        private val logger = LoggerFactory.getLogger(BookingComponent::class.java)
    }
}

class BookingException(message: String) : RuntimeException(message)

object BookingStatus {
    val BOOKING_CONFIRMED = "BOOKING_CONFIRMED"
    val CHECKED_IN = "CHECKED_IN"
}

data class Fare(var flightNumber: String = "", var flightDate: String = "", var fare: String = "")

@FeignClient(name = "fare-client")
@RibbonClient
interface FareClient {
    @GetMapping("fares/get")
    fun fare(@RequestParam("flightNumber") flightNumber: String, @RequestParam("flightDate") flightDate: String): Fare
}