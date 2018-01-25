package currency.repository;

import currency.representations.RateRepresentation;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Redis methods for providing caching capabilities
 * 
 * @author Kelvin
 */
@Repository
public class CurrencyRepository {
    
    private static final Logger logger = Logger.getLogger(CurrencyRepository.class);
    private static final String KEY = "Currency";
    
    private RedisTemplate<String, List<RateRepresentation>> redisTemplate;
    private HashOperations hashOps;
 
    @Autowired
    public CurrencyRepository(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    @PostConstruct
    private void init() {
        hashOps = redisTemplate.opsForHash();
    }
    
    public Boolean checkRedisConnection() {
        Jedis jedis = null;
        JedisPool jedisPool = new JedisPool();
        try{
            jedis = jedisPool.getResource();
            return true;
        } catch(JedisConnectionException ex) {
            logger.info("Redis not connected");
            return false;
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }
    
    public void saveRatesByDate(String date, List<RateRepresentation> rates) {
        hashOps.put(KEY, date, rates);
    }
    
    public List<RateRepresentation> getRatesByDate(String date) {
        return (List<RateRepresentation>) hashOps.get(KEY, date);
    }
    
    public Boolean checkRateExists(String date) {
        return hashOps.hasKey(KEY, date);
    }
    
}
