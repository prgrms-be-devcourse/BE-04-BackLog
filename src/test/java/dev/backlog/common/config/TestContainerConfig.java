package dev.backlog.common.config;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class TestContainerConfig {

    private static final String REDIS_IMAGE = "redis:7.0.8-alpine";
    private static final int REDIS_PORT = 6379;
    private static final GenericContainer REDIS;

    static {
        REDIS = new GenericContainer(REDIS_IMAGE)
                .withExposedPorts(REDIS_PORT)
                .waitingFor(Wait.forListeningPort());
        REDIS.start();

        System.setProperty("spring.data.redis.host", REDIS.getHost());
        System.setProperty("spring.data.redis.port", String.valueOf(REDIS.getMappedPort(REDIS_PORT)));
    }

}
