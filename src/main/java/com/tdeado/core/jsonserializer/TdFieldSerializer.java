package com.tdeado.core.jsonserializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.tdeado.core.annotations.TdField;
import com.tdeado.core.config.UploadFileProperties;
import com.tdeado.core.enums.ContentType;
import com.tdeado.core.service.ForeignConversionService;
import com.tdeado.core.util.SpringUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

public class TdFieldSerializer extends JsonSerializer<Object> implements ContextualSerializer {
    private TdField field;

    public TdFieldSerializer() {
    }

    public TdFieldSerializer(TdField field) {
        this.field = field;

    }

    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        Object newValue = value;
        if (field.contentType()== ContentType.FILE || field.contentType()== ContentType.IMAGE) {//是图片或文件 处理方式
            UploadFileProperties uploadFileProperties = SpringUtils.getBean(UploadFileProperties.class);
            String cdn = uploadFileProperties.getImgDomain() + uploadFileProperties.getImgDirectory();
            if (!StringUtils.isEmpty(value.toString())) {
                if (value.toString().contains(cdn) || value.toString().contains("http://") || value.toString().contains("https://")) {
                    newValue = value.toString();
                } else {
                    newValue = cdn + value.toString();
                }
            }
        }
        if (!field.foreign().equals(Void.class)) {//是外键 添加Str字段
            ForeignConversionService foreignConversionService = SpringUtils.getBean("com.tdeado.core.service.ForeignConversionService");
            newValue = foreignConversionService.conversion(field.foreign(), value.toString());
        }
        jsonGenerator.writeObject(newValue);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) { //为空直接跳过
            TdField field = beanProperty.getAnnotation(TdField.class);
            if (field == null) {
                field = beanProperty.getContextAnnotation(TdField.class);
            }
            if (field != null) { // 如果能得到注解，就将注解的 value 传入 ImageURLSerialize
//                if (!field.foreign().equals(Void.class)) {
                    return new TdFieldSerializer(field);
//                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(beanProperty);
    }
}
