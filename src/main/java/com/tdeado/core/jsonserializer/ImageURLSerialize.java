package com.tdeado.core.jsonserializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.tdeado.core.annotations.TdFileDomain;
import com.tdeado.core.config.UploadFileProperties;
import com.tdeado.core.util.SpringUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Objects;

public class ImageURLSerialize extends JsonSerializer<String> implements ContextualSerializer {

    @Override
    public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String url = "";
        UploadFileProperties uploadFileProperties =  SpringUtils.getBean(UploadFileProperties.class);
        String cdn = uploadFileProperties.getImgDomain()+uploadFileProperties.getImgDirectory();
        if (!StringUtils.isBlank(value)) {
            if (value.contains(cdn)) {
                url = value;
            } else {
                url = cdn + value;
            }
        }
        jsonGenerator.writeString(url);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) { // 为空直接跳过
            if (Objects.equals(beanProperty.getType().getRawClass(), String.class)) { // 非 String 类直接跳过
                TdFileDomain imageURL = beanProperty.getAnnotation(TdFileDomain.class);
                if (imageURL == null) {
                    imageURL = beanProperty.getContextAnnotation(TdFileDomain.class);
                }
                if (imageURL != null) { // 如果能得到注解，就将注解的 value 传入 ImageURLSerialize
                    return new ImageURLSerialize();
                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(beanProperty);
    }
}
