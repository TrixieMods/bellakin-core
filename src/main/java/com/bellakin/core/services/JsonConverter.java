package com.bellakin.core.services;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class JsonConverter implements IConverter<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonConverter.class);

    private final ObjectMapper mapper;

    public JsonConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T> Optional<T> convert(String rawJson, Class<T> clazz) {
        try {
            T object = mapper.readValue(rawJson, clazz);
            return Optional.ofNullable(object);
        } catch (JsonMappingException | JsonParseException e) {
            LOGGER.error("Invalid JSON: {}", rawJson.substring(0, rawJson.length() > 128 ? 128 : rawJson.length()));
            return Optional.empty();
        } catch (IOException e) {
            LOGGER.error("Exception", e);
            return Optional.empty();
        }
    }

}
