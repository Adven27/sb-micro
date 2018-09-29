package com.brownfield.pss.search

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*

interface FlightRepository : JpaRepository<Flight, Long> {
    fun findByOriginAndDestinationAndFlightDate(origin: String, destination: String, flightDate: String): List<Flight>

    fun findByFlightNumberAndFlightDate(flightNumber: String, flightDate: String): Flight
}

@Entity
class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    lateinit var flightNumber: String
    lateinit var origin: String
    lateinit var destination: String
    lateinit var flightDate: String

    @OneToOne(cascade = arrayOf(CascadeType.ALL))
    @JoinColumn(name = "fare_Id")
    lateinit var fares: Fares

    @OneToOne(cascade = arrayOf(CascadeType.ALL))
    @JoinColumn(name = "inv_Id")
    lateinit var inventory: Inventory

    constructor() : super()

    constructor(
        flightNumber: String, origin: String, destination: String, flightDate: String, fares: Fares,
        inventory: Inventory
    ) : super() {
        this.flightNumber = flightNumber
        this.origin = origin
        this.destination = destination
        this.flightDate = flightDate
        this.fares = fares
        this.inventory = inventory
    }

    override fun toString(): String {
        return ("Flight [id=" + id + ", flightNUmber=" + flightNumber + ", origin=" + origin + ", destination="
                + destination + ", flightDate=" + flightDate + ", fares=" + fares + ", inventory=" + inventory + "]")
    }
}

@Entity
class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "inv_id")
    var id: Long = 0

    var count: Int = 0

    constructor() : super()

    constructor(count: Int) : super() {
        this.count = count
    }

    override fun toString(): String {
        return "Inventory [id=$id, count=$count]"
    }
}

@Entity
class Fares {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "fare_id")
    var id: Long = 0

    lateinit var fare: String
    lateinit var currency: String


    constructor(fare: String, currency: String) : super() {
        this.fare = fare
        this.currency = currency
    }

    constructor() : super()

    override fun toString(): String {
        return "Fares [id=$id, fare=$fare, currency=$currency]"
    }
}