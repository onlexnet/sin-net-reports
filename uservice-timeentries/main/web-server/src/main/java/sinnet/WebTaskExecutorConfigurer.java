package sinnet;

import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

@Configuration
public class WebTaskExecutorConfigurer {

  @Bean
  public TaskExecutor taskExecutor() {
	  return new ConcurrentTaskExecutor(Executors.newFixedThreadPool(3));
  }
}
