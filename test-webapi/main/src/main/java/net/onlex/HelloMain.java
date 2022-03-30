package net.onlex;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import lombok.extern.slf4j.Slf4j;

@QuarkusMain
public class HelloMain implements QuarkusApplication {

  public static void main(String[] args) {
    Quarkus.run(HelloMain.class, args);
  }

  @Override
  public int run(String... args) throws Exception {
    return 0;
  }
}