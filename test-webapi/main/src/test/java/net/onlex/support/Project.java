package net.onlex.support;

import org.apache.commons.lang3.RandomStringUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Project {

  private final String named;
  private final String randomSuffix = RandomStringUtils.randomAlphabetic(6);

  public String getName() {
    return named + " [" + randomSuffix + "]";
  }
}
