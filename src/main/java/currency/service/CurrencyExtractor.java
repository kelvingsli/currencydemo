/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package currency.service;

import currency.repository.CurrencyRepository;
import currency.representations.RateRepresentation;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class that use executorservice to run callable that retrieves data from redis (if cached) or from txt files
 * Thread pool size of executorservice can be configured in AppConfig 
 * 
 * @author Kelvin
 */
@Service
public class CurrencyExtractor {
    
    private static final Logger logger = Logger.getLogger(CurrencyExtractor.class);
    private CurrencyRepository currencyRepository;
    private int executorThreadPoolSize;
    
    private Pattern p = Pattern.compile("1 (?<currency>\\w{3,}) traded at (?<rate>\\d+\\.\\d+) times USD");
    
    @Autowired
    public CurrencyExtractor(CurrencyRepository currencyRepository, int executorThreadPoolSize) {
        this.currencyRepository = currencyRepository;
        this.executorThreadPoolSize = executorThreadPoolSize;
    }
    
    public Map<String, List<RateRepresentation>> getRateMap(List<String> filenames) {
        List<Future<Map<String, List<RateRepresentation>>>> futures = new ArrayList<Future<Map<String, List<RateRepresentation>>>>();
        ExecutorService executors = Executors.newFixedThreadPool(executorThreadPoolSize);
        for (String filename : filenames) {
            futures.add(executors.submit(new ExtractorCallable(currencyRepository, filename)));
        }
        
        Map<String, List<RateRepresentation>> map = new HashMap<String, List<RateRepresentation>>();
        for (Future<Map<String, List<RateRepresentation>>> future : futures) {
            try {
                String index = future.get().keySet().iterator().next();
                map.put(index, future.get().get(index));
            } catch(Exception ex) {
                logger.error("Error when processing futures", ex);
            }
        }
        
        if(map.keySet().size() > 1) {
            return map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

                    
        }   
        return map;
    }
}
