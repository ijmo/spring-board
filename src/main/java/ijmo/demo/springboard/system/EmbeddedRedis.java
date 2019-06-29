package ijmo.demo.springboard.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Component
@Profile("dev")
public class EmbeddedRedis {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedRedis.class);

    @Value("${spring.redis.port}")
    private int port;

    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() throws IOException {
        redisServer = new RedisServer(port);
        redisServer.start();
        log.info("Embedded redis server started");
    }

    @PreDestroy
    public void stopRedis() {
        redisServer.stop();
    }
}