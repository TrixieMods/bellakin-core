package com.bellakin.core.services;

import com.bellakin.core.factories.ConverterFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author dan.rees.thomas@gmail.com
 */
public class JsonConverterTest {

  @Mock
  private ObjectMapper mapper;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testMapping() {
    // Given
    JsonConverter jsonConverter = new JsonConverter(mapper);

    // When
//    when(mapper.readValue(anyString(), ArgumentMatchers.any(TestObject.class))).then(new TestObject());

    // Then
  }



}