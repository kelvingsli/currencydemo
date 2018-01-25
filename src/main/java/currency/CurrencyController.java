package currency;

import currency.service.CurrencyExtractor;
import currency.representations.RateRepresentation;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Main controller to serve api endpoints
 * 
 * @author Kelvin
 */
@RestController
public class CurrencyController {
    
    private static final Logger logger = Logger.getLogger(CurrencyController.class);
    private CurrencyExtractor currencyExtractor;
    
    @Autowired
    public CurrencyController(CurrencyExtractor currencyExtractor) {
        this.currencyExtractor = currencyExtractor;
    }
    
    @RequestMapping("/date")
    public Map<String, List<RateRepresentation>> getRateByDate(@RequestParam(value="date", required=true) String date) {
        List<String> dates = new ArrayList<String>();
        dates.add(date);
        return currencyExtractor.getRateMap(dates);
    }
    
    @RequestMapping("/compare")
    public String compareRate(@RequestParam(value="date") String date,
            @RequestParam(value="curr1") String currency1,
            @RequestParam(value="curr2") String currency2) {
        List<String> dates = new ArrayList<String>();
        dates.add(date);
        Map<String, List<RateRepresentation>> rateMap = currencyExtractor.getRateMap(dates);
        List<RateRepresentation> rates = rateMap.get(date);
        if(rates!=null || !rates.isEmpty()) {
            RateRepresentation rate1 = rates.stream()
                        .filter(r -> r.getCurrency().equalsIgnoreCase(currency1))
                        .findFirst()
                        .get();
            RateRepresentation rate2 = rates.stream()
                        .filter(r -> r.getCurrency().equalsIgnoreCase(currency2))
                        .findFirst()
                        .get();
            String calculatedRate = new DecimalFormat("0.00").format(Double.parseDouble(rate1.getRate()) / Double.parseDouble(rate2.getRate())); 
            return String.format("1 %s traded at %s times %s", currency1.toUpperCase(), calculatedRate, currency2.toUpperCase());
        } else {
            return String.format("No relevant data available on %s", date);
        }
    }
    
    @RequestMapping("/range")
    public Map<String, RateRepresentation> getRateByRange(@RequestParam(value="start") String start,
            @RequestParam(value="end") String end,
            @RequestParam(value="curr") String currency) {
        String result = null;
        Long diff = 0L;
        List<String> dates = new ArrayList<String>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dStart = sdf.parse(start);
            Date dEnd = sdf.parse(end);
            diff = (dEnd.getTime() - dStart.getTime()) / (1000*60*60*24);
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(start));
            while(diff >= 0) {
                dates.add(sdf.format(c.getTime()));
                c.add(Calendar.DATE, 1);
                diff--;
            }
        } catch(Exception ex) {
            logger.error("Error when building date list", ex);
        }
        Map<String, List<RateRepresentation>> rateMap = currencyExtractor.getRateMap(dates);
        Map<String, RateRepresentation> results = new LinkedHashMap<String, RateRepresentation>();
        for(String date : dates) {
            List<RateRepresentation> rates = rateMap.get(date);
            RateRepresentation rate = null;
            
            logger.debug(rates.isEmpty());
            if(rates !=null && !rates.isEmpty()) {
                rate = rates.stream()
                        .filter(r -> r.getCurrency().equalsIgnoreCase(currency))
                        .findFirst()
                        .get();
            }
            results.put(date, rate);
        }
        return results;
    }    
}
