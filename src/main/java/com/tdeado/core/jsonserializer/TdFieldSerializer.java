package com.tdeado.core.jsonserializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.tdeado.core.annotations.TdField;
import com.tdeado.core.config.UploadFileProperties;
import com.tdeado.core.enums.BaseEnum;
import com.tdeado.core.enums.ContentType;
import com.tdeado.core.service.ForeignConversionService;
import com.tdeado.core.util.SpringUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TdFieldSerializer extends JsonSerializer<Object> implements ContextualSerializer {
    private TdField field;
    private BeanProperty beanProperty;
    public TdFieldSerializer() {
    }

    public TdFieldSerializer(TdField field,BeanProperty beanProperty) {
        this.field = field;
        this.beanProperty = beanProperty;
    }
    public static List<String> getEnumTitleByClassAndCode(Class<?> clazz,String code){
        List<String> list = new ArrayList<>();
        try {
            Object[] objects = clazz.getEnumConstants();
            Method getCode = clazz.getMethod("getCode");
            for (Object object:objects) {
                if (getCode.invoke(object).toString().equals(code)) {
                    list.add(object.toString());
                    return list;
                }
            }
            return list;
        }catch (Exception e) {
            return list;
        }
    }
    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        Object newValue = value;
        if (beanProperty.getType().isEnumType()){
            BaseEnum baseEnum= ((BaseEnum<Serializable>) newValue);
            Map<String,Object> map = Map.of("label",baseEnum.getDescp(),"value",baseEnum.getCode());
            newValue = map;
        } else if (field.contentType()== ContentType.FILE || field.contentType()== ContentType.IMAGE) {//是图片或文件 处理方式
            UploadFileProperties uploadFileProperties = SpringUtils.getBean(UploadFileProperties.class);
            String cdn = uploadFileProperties.getImgDomain() + uploadFileProperties.getImgDirectory();
            if (!StringUtils.isEmpty(value.toString())) {
                if (value.toString().contains(cdn) || value.toString().contains("http://") || value.toString().contains("https://")) {
                    newValue = value.toString();
                } else {
                    newValue = cdn + value.toString();
                }
            }
        }else if (!field.foreign().equals(Void.class)) {//是外键 添加Str字段
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
            if (field != null || beanProperty.getType().isEnumType()) { // 如果能得到注解，就将注解的 value 传入 ImageURLSerialize
//                if (!field.foreign().equals(Void.class)) {
                    return new TdFieldSerializer(field,beanProperty);
//                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(beanProperty);
    }
}
