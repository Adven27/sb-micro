package com.brownfield.pss.search

import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*

private val FLIGHT_DATE = "22-JAN-16"
private val COUNT = 100

@SpringBootApplication
class SearchApp @Autowired constructor(private val flightRepository: FlightRepository) : CommandLineRunner {

    override fun run(vararg strings: String) {
        val inv = Inventory(COUNT)
        flightRepository.save(
            listOf(
                Flight("BF100", "SEA", "SFO", FLIGHT_DATE, Fares("100", "USD"), inv),
                Flight("BF101", "NYC", "SFO", FLIGHT_DATE, Fares("101", "USD"), inv),
                Flight("BF105", "NYC", "SFO", FLIGHT_DATE, Fares("105", "USD"), inv),
                Flight("BF106", "NYC", "SFO", FLIGHT_DATE, Fares("106", "USD"), inv),
                Flight("BF102", "CHI", "SFO", FLIGHT_DATE, Fares("102", "USD"), inv),
                Flight("BF103", "HOU", "SFO", FLIGHT_DATE, Fares("103", "USD"), inv),
                Flight("BF104", "LAX", "SFO", FLIGHT_DATE, Fares("104", "USD"), inv)
            )
        )

        LOG.info("Looking to load flights...")
        flightRepository.findByOriginAndDestinationAndFlightDate("NYC", "SFO", FLIGHT_DATE)
            .forEach { LOG.info("{}", it) }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(SearchApp::class.java)
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(SearchApp::class.java, *args)
}

@RefreshScope
@CrossOrigin
@RestController
@RequestMapping("/search")
internal class SearchRestController @Autowired constructor(private val searchComponent: SearchComponent) {

    @Value("\${originairports.shutdown}")
    private val originShutdownAirports: String? = null

    @PostMapping(value = ["/get"])
    fun search(@RequestBody query: SearchComponent.SearchQuery): List<Flight> =
        if (originShutdownAirports!!.split(",").dropLastWhile { it.isEmpty() }.contains(query.origin)) {
            LOGGER.info("The origin airport is in shutdown state.")
            emptyList()
        } else searchComponent.search(query)

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SearchRestController::class.java)
    }
}

@Component
class SearchComponent @Autowired constructor(private val flightRepository: FlightRepository) {
    fun search(query: SearchQuery) = flightRepository.findByOriginAndDestinationAndFlightDate(
        query.origin,
        query.destination,
        query.flightDate
    ).filter { it.inventory.count > 0 }

    fun updateInventory(flightNumber: String, flightDate: String, newInventory: Int) {
        logger.info("Updating inventory for flight $flightNumber innventory $newInventory")
        flightRepository.save(
            flightRepository.findByFlightNumberAndFlightDate(flightNumber, flightDate).apply {
                inventory.count = newInventory
            })
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SearchComponent::class.java)
    }

    data class SearchQuery(var origin: String = "", var destination: String = "", var flightDate: String = "")
}

@Component
class Receiver @Autowired constructor(private val searchComponent: SearchComponent) {
    @Bean
    internal fun queue(): Queue {
        return Queue("SearchQ", false)
    }

    @RabbitListener(queues = ["SearchQ"])
    fun processMessage(fare: Map<String, Any>) = searchComponent.updateInventory(
        fare["FLIGHT_NUMBER"].toString(),
        fare["FLIGHT_DATE"].toString(),
        fare["NEW_INVENTORY"].toString().toInt()
    )
}