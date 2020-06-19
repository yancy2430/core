package com.tdeado.core.config;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

@Configuration
public class GsonConfigurer {
    @Bean
    @ConditionalOnMissingBean
    public Gson gsonInit(){
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                ).registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))).registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> {
                    String datetime = json.getAsJsonPrimitive().getAsString();
                    return LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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
                .registerTypeAdapter(Long.class, new TypeAdapter<Object>() {
                    @Override
                    public void write(JsonWriter out, Object value) throws IOException {
                        out.value(value.toString());
                    }
                    @Override
                    public Object read(JsonReader in) throws IOException {
                        if (in.peek() == JsonToken.NULL) {
                            in.nextNull();
                            return null;
                        }
                        try {
                            String result = in.nextString();
                            if ("".equals(result)) {
                                return null;
                            }
                            return result;
                        } catch (NumberFormatException e) {
                            throw new JsonSyntaxException(e);
                        }
                    }
                })
                .registerTypeAdapter(long.class, new TypeAdapter<Object>() {
                    @Override
                    public void write(JsonWriter out, Object value) throws IOException {
                        out.value(value.toString());
                    }

                    @Override
                    public Object read(JsonReader in) throws IOException {
                        if (in.peek() == JsonToken.NULL) {
                            in.nextNull();
                            return null;
                        }
                        try {
                            String result = in.nextString();
                            if ("".equals(result)) {
                                return null;
                            }
                            return result;
                        } catch (NumberFormatException e) {
                            throw new JsonSyntaxException(e);
                        }
                    }
                })
                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                .create();
    }
    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters customConverters() {

        Collection<HttpMessageConverter<?>> messageConverters = new ArrayList<>();

        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter(gsonInit());
        messageConverters.add(gsonHttpMessageConverter);

        return new HttpMessageConverters(true, messageConverters);
    }
}