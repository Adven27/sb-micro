package com.brownfield.pss.client

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.testcontainers.containers.FixedHostPortGenericContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import java.time.Duration
import java.time.temporal.ChronoUnit.SECONDS
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

class KGenericContainer(imageName: String) : FixedHostPortGenericContainer<KGenericContainer>(imageName)

private val rabbitMq = KGenericContainer("rabbitmq:3-management")
    .withFixedExposedPort(5672, 5672)
    .withFixedExposedPort(15672, 15672)
    .waitingFor(LogMessageWaitStrategy().withRegEx(".*Server startup complete.*\\s"))
    .withStartupTimeout(Duration.of(10, SECONDS))

@SpringBootApplication
class MqApp {
    @PostConstruct
    fun start() = rabbitMq.start()

    @PreDestroy
    fun tearDown() = rabbitMq.stop()
}

fun main(args: Array<String>) {
    SpringApplication.run(MqApp::class.java, *args)
}