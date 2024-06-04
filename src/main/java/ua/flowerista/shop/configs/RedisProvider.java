package ua.flowerista.shop.configs;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.time.Duration;


@Service
public class RedisProvider {

    @Value("${spring.data.redis.url}")
    private String redisUrl;

    @Bean
    public JedisPool getPool() {
        URI redisUri = URI.create(redisUrl);
        GenericObjectPoolConfig<Jedis> poolConfig = new JedisPoolConfig();
        poolConfig.setJmxEnabled(false);
        poolConfig.setJmxNamePrefix("jedis");
        poolConfig.setMaxTotal(30);
        poolConfig.setMaxIdle(10);
        poolConfig.setMinIdle(5);
        poolConfig.setMaxWait(Duration.ofMillis(2000));

        return new JedisPool(poolConfig,
                redisUri.getHost(),
                redisUri.getPort(),
                6000,
                //if no user info is provided in url it will use null by default
                redisUri.getUserInfo() == null ? null : redisUri.getUserInfo().split(":", 2)[1],
                true);
    }
}
