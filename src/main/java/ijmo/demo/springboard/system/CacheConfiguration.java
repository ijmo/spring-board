package ijmo.demo.springboard.system;

import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final long CACHE_TIMEOUT = 5;

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> cm.createCache("posts", cacheConfiguration(new Duration(TimeUnit.SECONDS, CACHE_TIMEOUT)));
    }

    private javax.cache.configuration.Configuration<Object, Object> cacheConfiguration(Duration duration) {
        return new MutableConfiguration<>()
                .setStatisticsEnabled(true)
                .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(duration));
    }
}