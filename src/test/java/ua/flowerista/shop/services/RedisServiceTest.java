package ua.flowerista.shop.services;

import org.junit.jupiter.api.*;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class RedisServiceTest {

    @Container
    @ServiceConnection(name = "redis")
    static GenericContainer<?> redisContainer = new GenericContainer<>(
            DockerImageName
                    .parse("redis:latest"))
            .withExposedPorts(6379);
    private static JedisPool jedisPool;
    private static RedisService redisService;

    @BeforeAll
    static void beforeAll() {
        redisContainer.start();
        jedisPool = new JedisPool(redisContainer.getHost(), redisContainer.getMappedPort(6379));
        redisService = new RedisService(jedisPool);
    }

    @AfterAll
    static void afterAll() {
        redisContainer.stop();
    }

    @BeforeEach
    void setUp() {
        jedisPool.getResource().flushAll();
    }

    @Test
    @DisplayName("Save and get hash map")
    void saveHashMapAndRetrieveSuccess() {
        //given
        Map<String, String> input = Map.of("key", "value");
        //when
        redisService.saveHashMap("test", input, 1000L);
        Map<String, String> result = redisService.getHashMap("test");
        //then
        assertEquals(input, result);
    }

    @Test
    @DisplayName("Save and get set")
    void saveListAndRetrieveSuccess() {
        //given
        Set<String> input = Set.of("1", "2", "3");
        //when
        redisService.saveSet("test", input);
        Set<String> result = redisService.getSet("test");
        //then
        assertEquals(input, result);
    }

    @Test
    @DisplayName("Delete token")
    void deleteTokenSuccess() {
        //given
        Map<String, String> input = Map.of("key", "value");
        redisService.saveHashMap("test", input, 1000L);
        //when
        redisService.deleteByKey("test");
        Map<String, String> result = redisService.getHashMap("test");
        //then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Token deleted after expiration")
    void tokenDeletedAfterExpiration() throws InterruptedException {
        //given
        Map<String, String> input = Map.of("key", "value");
        redisService.saveHashMap("test", input, 1L);
        //when
        Thread.sleep(1000);
        Map<String, String> result = redisService.getHashMap("test");
        //then
        assertTrue(result.isEmpty());
    }
}
