package net.onlex.support;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.concurrent.Memoizer;

import io.vavr.Lazy;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
public class Project {

  private final String named;
  private final String randomSuffix = RandomStringUtils.randomAlphabetic(6);

  public String getName() {
    return named + " [" + randomSuffix + "]";
  }
}
