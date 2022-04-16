package net.onlex;

import java.util.function.IntPredicate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Belly {

  private int cukes;

  public void eat(int cukes) {
    this.cukes += cukes;
  }

  public void timeInHrs(int hours) {
    cukes = cukes - 10 * hours;
    if (cukes < 0) {
      cukes = 0;
    }
  }

  public Boolean growl() {
    return cukes == 0;
  }
}
