package com.brownfield.pss.fares

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class FaresApp : CommandLineRunner {

    @Autowired
    lateinit var faresRepository: FaresRepository

    override fun run(vararg strings: String) {
        (0..6).forEach {
            faresRepository.save(Fare("BF10$it", "22-JAN-16", "10$it"))
        }
        logger.info("Result: " + faresRepository.getFareByFlightNumberAndFlightDate("BF101", "22-JAN-16"))
    }

    companion object {
        private val logger = LoggerFactory.getLogger(FaresApp::class.java)
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(FaresApp::class.java, *args)
}