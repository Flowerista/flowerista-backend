package ua.flowerista.shop.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import ua.flowerista.shop.exceptions.AppException;
import ua.flowerista.shop.models.VerificationToken;

import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class RedisService {

    Logger logger = LoggerFactory.getLogger(RedisService.class);

    private final JedisPool pool;

    public void saveHashMap(String token, Map<String, String> info, Long expiration) {
        try (Jedis jedis = pool.getResource()) {
            jedis.hset(token, info);
            jedis.expire(token, expiration);
        } catch (Exception e) {
            logger.error("Error while saving token: " + token + "\ninfo:\n" + info, e);
            throw new AppException("Error while saving token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, String> getHashMap(String token) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.hgetAll(token);
        } catch (Exception e) {
            logger.error("Error while getting token: " + token, e);
            throw new AppException("Error while getting token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void saveSet(String key, Set<String> set){
        try (Jedis jedis = pool.getResource()) {
            jedis.sadd(key, set.toArray(new String[0]));
        } catch (Exception e) {
            logger.error("Error while saving set: " + key, e);
            throw new AppException("Error while saving set", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Set<String> getSet(String key){
        try (Jedis jedis = pool.getResource()) {
            Set<String> set = jedis.smembers(key);
            return set;
        } catch (Exception e) {
            logger.error("Error while getting set: " + key, e);
            throw new AppException("Error while getting set", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void saveToken(String key, VerificationToken token, Long expiration) {
        try (Jedis jedis = pool.getResource()) {
            jedis.hset(key, "id", token.getId());
            jedis.hset(key, "login", token.getUserLogin());
            jedis.expire(key, expiration);
        } catch (Exception e) {
            logger.error("Error while saving token: " + key + "\nvalue:\n" + token, e);
            throw new AppException("Error while saving string", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public VerificationToken getToken(String key) {
        try (Jedis jedis = pool.getResource()) {
            String id = jedis.hget(key, "id");
            if (id == null) {
                return null;
            }
            String login = jedis.hget(key, "login");
            VerificationToken token = VerificationToken.builder()
                    .id(id)
                    .userLogin(login)
                    .build();
            return token;
        } catch (Exception e) {
            logger.error("Error while getting token: " + key, e);
            throw new AppException("Error while getting string", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteByKey(String key) {
        try (Jedis jedis = pool.getResource()) {
            jedis.del(key);
        } catch (Exception e) {
            logger.error("Error while deleting token: " + key, e);
            throw new AppException("Error while deleting token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
