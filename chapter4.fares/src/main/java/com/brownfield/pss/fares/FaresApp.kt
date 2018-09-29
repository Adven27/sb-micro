package com.brownfield.pss.fares

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@SpringBootApplication
class FaresApp : CommandLineRunner {

    @Autowired
    lateinit var repo: FaresRepository

    override fun run(vararg strings: String) {
        (0..6).forEach {
            repo.save(Fare("BF10$it", "22-JAN-16", "10$it"))
        }
        logger.info("Result: " + repo.getFareByFlightNumberAndFlightDate("BF101", "22-JAN-16"))
    }

    companion object {
        private val logger = LoggerFactory.getLogger(FaresApp::class.java)
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(FaresApp::class.java, *args)
}

@RestController
@CrossOrigin
@RequestMapping("/fares")
class FaresController @Autowired constructor(var faresComponent: FaresComponent) {

    @RequestMapping("/get")
    fun getFare(@RequestParam("flightNumber") number: String, @RequestParam("flightDate") date: String) =
        faresComponent.getFare(number, date)
}

@Component
class FaresComponent @Autowired constructor(val repo: FaresRepository) {
    fun getFare(flightNumber: String, flightDate: String): Fare {
        logger.info("Looking for fares flightNumber $flightNumber flightDate $flightDate")
        return repo.getFareByFlightNumberAndFlightDate(flightNumber, flightDate)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(FaresComponent::class.java)
    }
}

interface FaresRepository : JpaRepository<Fare, Long> {
    fun getFareByFlightNumberAndFlightDate(flightNumber: String, flightDate: String): Fare
}

@Entity
data class Fare(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,
    val flightNumber: String = "",
    val flightDate: String = "",
    val fare: String = ""
) {
    constructor(flightNumber: String, flightDate: String, fare: String) : this(0, flightNumber, flightDate, fare)
}
