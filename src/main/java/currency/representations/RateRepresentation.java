package currency.representations;

import java.io.Serializable;

/**
 * Currency rate representation with currency and currency rate fields
 * 
 * @author Kelvin
 */
public class RateRepresentation implements Serializable {
    
    private String currency;
    private String rate;
    
    public RateRepresentation(String currency, String rate) {
        this.currency = currency;
        this.rate = rate;
    }
    
    public String getCurrency() {
        return this.currency;
    }
    
    public String getRate() {
        return this.rate;
    }
}
