package com.bellakin.core.data.persistence;

/**
 *
 */
public abstract class AbstractRepo<T> implements IRepo<T> {

  private final Class<T> type;

  protected AbstractRepo(Class<T> type) {
    this.type = type;
  }

  public final Class<T> getType() {
    return type;
  }
}
