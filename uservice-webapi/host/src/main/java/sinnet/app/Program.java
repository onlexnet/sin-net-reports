package sinnet.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/** Entry point for the application. */
@SpringBootApplication
@ComponentScan(basePackages = { "sinnet" })
public class Program {

  public static void main(String ... args) {
    SpringApplication.run(Program.class, args);
  }
}
