package currency.service;

import currency.repository.CurrencyRepository;
import currency.representations.RateRepresentation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 * Callable to enable parallel pulling of data from txt files for large date range ranges
 * If data is cached, pull from redis
 * If data is not cached, read txt file and parse data. Cache into redis.
 * 
 * @author Kelvin
 */
public class ExtractorCallable implements Callable<Map<String, List<RateRepresentation>>> {
    
    private static final Logger logger = Logger.getLogger(ExtractorCallable.class);
    private Pattern p = Pattern.compile("1 (?<currency>\\w{3,}) traded at (?<rate>\\d+\\.\\d+) times USD");
    private String filename;
    private CurrencyRepository currencyRepository;
    
    public ExtractorCallable(CurrencyRepository currencyRepository, String filename) {
        this.filename = filename;
        this.currencyRepository = currencyRepository;
    }
    
    @Override
    public Map<String, List<RateRepresentation>> call() {
        
        Map<String, List<RateRepresentation>> map = new HashMap<String, List<RateRepresentation>>();
        List<RateRepresentation> rates = new ArrayList<RateRepresentation>();
        Boolean isRedis = currencyRepository.checkRedisConnection();
        
        if(isRedis && currencyRepository.checkRateExists(filename)) {
            rates = currencyRepository.getRatesByDate(filename);
            logger.info("Data retrieved from Redis for " + filename);
        } else {
            List<String> records = readFile(filename);
            for(String record : records) {
                Matcher m = p.matcher(record);
                while (m.find()) {
                    String currency  = m.group("currency");
                    String rate = m.group("rate");
                    rates.add(new RateRepresentation(currency, rate));
                }
            }
            
            if(isRedis) {
                currencyRepository.saveRatesByDate(filename, rates);
                logger.info("Data cached into Redis for " + filename);
            }
            
        }
        map.put(filename, rates);
        return map;
    } 
    
    
    private List<String> readFile(String filename) {
        List<String> records = new ArrayList<String>();
        try {
            logger.info("Path: " + getClass().getResource("/data/" + filename + ".txt"));
            File file = new File(getClass().getResource("/data/" + filename + ".txt").getFile());
            if(file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while((line = reader.readLine()) != null) {
                    records.add(line);
                }
            }
        } catch(Exception ex) {
            logger.error("Error when reading file " + filename + ".txt", ex);
        }
        return records;
    }
    
    
}
