package sinnet;

import org.springframework.boot.SpringApplication;

import com.microsoft.applicationinsights.attach.ApplicationInsights;

/**
 * TBD.
 */
public final class Program {

  /** Entry point of the app. */
  public static void main(final String[] args) {

    // enable application insights
    // https://learn.microsoft.com/en-us/azure/azure-monitor/app/java-spring-boot#enabling-programmatically
    ApplicationInsights.attach();
    
    SpringApplication.run(Application.class, args);
  }
}
