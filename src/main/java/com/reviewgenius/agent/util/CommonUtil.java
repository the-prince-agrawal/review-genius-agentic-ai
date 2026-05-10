package com.reviewgenius.agent.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Slf4j
public class CommonUtil {

  public static String getMockResponse(String fileName) {
    try {
      log.info("Loading dummy response from file: {}", fileName);
      return new String(new ClassPathResource(fileName).getInputStream().readAllBytes());
    } catch (IOException ex) {
      throw new RuntimeException("Failed to load dummy LLM response", ex);
    }
  }
}
