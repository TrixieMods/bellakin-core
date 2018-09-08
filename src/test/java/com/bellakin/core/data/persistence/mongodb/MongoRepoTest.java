package com.bellakin.core.data.persistence.mongodb;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

public class MongoRepoTest {

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }
}