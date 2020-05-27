package sinnet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/** Configuration created to enable REdis-based HttpSession. */
@EnableRedisHttpSession
public class RedisSessionConfig {

    @Value("${web-server.redis.host}")
    private String redisHostName;
    /**
     * Creates Redis connection used e.g. by Session based on REdis server.
     * @return Connection to Redis
     */
    @Bean
    public LettuceConnectionFactory connectionFactory() {
        // we need to provide in configuration Redis name same as service define
        // in kubernetes cluster to override default name 'localhost'
        var redisConf = new RedisStandaloneConfiguration();
        redisConf.setHostName(redisHostName);
        return new LettuceConnectionFactory(redisConf);
    }
}
