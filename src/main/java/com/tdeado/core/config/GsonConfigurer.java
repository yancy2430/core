package com.tdeado.core.config;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class GsonConfigurer {
    @Bean
    @ConditionalOnMissingBean
    public Gson gsonInit(){

        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                ).registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))).registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                    @Override
                    public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                        String datetime = json.getAsJsonPrimitive().getAsString();
                        return LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    }
                }).registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, jsonDeserializationContext) -> {
                    String datetime = json.getAsJsonPrimitive().getAsString();
                    return LocalDate.parse(datetime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }).registerTypeAdapter(Number.class, new TypeAdapter<Number>() {
                    @Override
                    public void write(JsonWriter out, Number value) throws IOException {
                        out.value(value);
                    }
                    @Override
                    public Number read(JsonReader in) throws IOException {
                        if (in.peek() == JsonToken.NULL) {
                            in.nextNull();
                            return null;
                        }
                        try {
                            String result = in.nextString();
                            if ("".equals(result)) {
                                return null;
                            }
                            return Integer.parseInt(result);
                        } catch (NumberFormatException e) {
                            throw new JsonSyntaxException(e);
                        }
                    }
                })
                .registerTypeAdapter(Long.class, new TypeAdapter<Number>() {
                    @Override
                    public void write(JsonWriter out, Number value) throws IOException {
                        out.value(value);
                    }
                    @Override
                    public Number read(JsonReader in) throws IOException {
                        if (in.peek() == JsonToken.NULL) {
                            in.nextNull();
                            return null;
                        }
                        try {
                            String result = in.nextString();
                            if ("".equals(result)) {
                                return null;
                            }
                            return Long.parseLong(result);
                        } catch (NumberFormatException e) {
                            throw new JsonSyntaxException(e);
                        }
                    }
                })
                .registerTypeAdapter(long.class, new TypeAdapter<Number>() {
                    @Override
                    public void write(JsonWriter out, Number value) throws IOException {
                        out.value(value);
                    }
                    @Override
                    public Number read(JsonReader in) throws IOException {
                        if (in.peek() == JsonToken.NULL) {
                            in.nextNull();
                            return null;
                        }
                        try {
                            String result = in.nextString();
                            if ("".equals(result)) {
                                return null;
                            }
                            return Long.parseLong(result);
                        } catch (NumberFormatException e) {
                            throw new JsonSyntaxException(e);
                        }
                    }
                })

                .create();


    }
}
