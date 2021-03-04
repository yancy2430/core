package com.tdeado.core.annotations;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tdeado.core.jsonserializer.ImageURLSerialize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 如果是文件资源 自动添加域名
 */
@Retention (RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = ImageURLSerialize.class)
public @interface TdFileDomain {
}
