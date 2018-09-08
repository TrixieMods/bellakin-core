package com.bellakin.core.services;

public  class TestObject {
  private String value;

  public TestObject() {
    this("");
  }

  public TestObject(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}