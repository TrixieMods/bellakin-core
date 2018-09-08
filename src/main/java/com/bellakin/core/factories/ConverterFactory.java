package com.bellakin.core.factories;

import com.bellakin.core.services.JsonConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ConverterFactory {

  @Bean
  public JsonConverter getJsonConverter() {
    return new JsonConverter(new ObjectMapper());
  }

}
