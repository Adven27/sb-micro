package com.brownfield.pss.client;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.function.Consumer;

import static java.time.temporal.ChronoUnit.SECONDS;


@SpringBootApplication
public class MqApp {
    private static final Logger logger = LoggerFactory.getLogger(MqApp.class);
    private static final int containerExposedPort = 5672;

    private static GenericContainer rabbitMq = new FixedHostPortGenericContainer("rabbitmq:3-management")
            .withFixedExposedPort(containerExposedPort, containerExposedPort)
            .withFixedExposedPort(15672, 15672)
            .waitingFor(new LogMessageWaitStrategy().withRegEx(".*Server startup complete.*\\s"))
            .withStartupTimeout(Duration.of(10, SECONDS));

    public static void main(String[] args) {
        SpringApplication.run(MqApp.class, args);
    }

    @PostConstruct
    public void start() {
        rabbitMq.start();
    }

    @PreDestroy
    public void tearDown() {
        rabbitMq.stop();
    }
}