package com.bellakin.core.services;

import java.util.Optional;

/**
 * Converts from type R into a java class of type T
 * @param <R> Type to convert from
 *
 * @author dan.rees.thomas@gmail.com
 */
public interface IConverter<R> {

  <T> Optional<T> convert(R data, Class<T> clazz);

}
