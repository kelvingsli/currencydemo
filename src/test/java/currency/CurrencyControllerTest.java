package currency;

import currency.config.AppConfig;
import currency.representations.RateRepresentation;
import currency.service.CurrencyExtractor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test on the CurrencyController class
 * 
 * @author Kelvin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class CurrencyControllerTest {
    
    public CurrencyControllerTest(){
    };
    
    @Autowired
    private CurrencyController currencyController;
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getRateByDate method, of class CurrencyController.
     */
    @Test
    public void testGetRateByDate() {
        System.out.println("getRateByDate");
        String date = "2017-01-01";
        Map<String, List<RateRepresentation>> expResult = new HashMap<String, List<RateRepresentation>>();
        List<RateRepresentation> rates = new ArrayList<RateRepresentation>();
        rates.add(new RateRepresentation("CHF", "1.04"));
        rates.add(new RateRepresentation("EUR", "1.19"));
        rates.add(new RateRepresentation("GBP", "1.30"));
        rates.add(new RateRepresentation("OMF", "2.60"));
        rates.add(new RateRepresentation("BHD", "2.65"));
        rates.add(new RateRepresentation("KWD", "3.32"));
        rates.add(new RateRepresentation("SGD", "0.74"));
        expResult.put(date, rates);
        Map<String, List<RateRepresentation>> result = currencyController.getRateByDate(date);
        assertEquals(expResult.get(date).size(), result.get(date).size());
        assertEquals(expResult.get(date).get(0).getRate(), result.get(date).get(0).getRate());
        
    }

    /**
     * Test of compareRate method, of class CurrencyController.
     */
    @Test
    public void testCompareRate() {
        System.out.println("compareRate");
        String date = "2017-01-01";
        String currency1 = "OMR";
        String currency2 = "CHF";
        String expResult = "1 OMR traded at 2.50 times CHF";
        String result = currencyController.compareRate(date, currency1, currency2);
        assertEquals(expResult, result);

    }

    /**
     * Test of getRateByRange method, of class CurrencyController.
     */
    @Test
    public void testGetRateByRange() {
        System.out.println("getRateByRange");
        String start = "2017-01-01";
        String end = "2017-01-03";
        String currency = "CHF";
        Map<String, RateRepresentation> expResult = new LinkedHashMap<String, RateRepresentation>();
        expResult.put("2017-01-01", new RateRepresentation(currency, "1.04"));
        expResult.put("2017-01-02", new RateRepresentation(currency, "1.04"));
        expResult.put("2017-01-03", new RateRepresentation(currency, "3.04"));
        Map<String, RateRepresentation> result = currencyController.getRateByRange(start, end, currency);
        assertEquals(expResult.keySet().size(), result.keySet().size());
        assertEquals(expResult.get(end).getRate(), result.get(end).getRate());
        
    }
    
}
