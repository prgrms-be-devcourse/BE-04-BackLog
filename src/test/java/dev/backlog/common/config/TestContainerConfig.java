package dev.backlog.common.config;

import org.junit.jupiter.api.extension.Extension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class TestContainerConfig implements Extension {

    private static final String REDIS_IMAGE = "redis:7.0.8-alpine";
    private static final int REDIS_PORT = 6379;
    private static final GenericContainer redis;

    static {
        redis = new GenericContainer(REDIS_IMAGE)
                .withExposedPorts(REDIS_PORT)
                .waitingFor(Wait.forListeningPort());
        redis.start();

        System.setProperty("spring.data.redis.host", redis.getHost());
        System.setProperty("spring.data.redis.port", String.valueOf(redis.getMappedPort(6379)));
    }

}
