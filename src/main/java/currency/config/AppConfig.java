package currency.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * General configuration
 * 
 * @author Kelvin
 */
@Configuration
@ComponentScan({"currency"})
public class AppConfig {
    
    @Bean
    public int executorThreadPoolSize() {
        return 5;
    }
}
