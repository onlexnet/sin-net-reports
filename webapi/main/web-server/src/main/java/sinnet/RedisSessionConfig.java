package sinnet;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/** Configuration created to enable REdis-based HttpSession. */
@EnableRedisHttpSession
public class RedisSessionConfig {

    /**
     * Creates Redis connection used e.g. by Session based on REdis server.
     * @return Connection to Redis
     */
    @Bean
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }
}
