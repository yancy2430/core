package com.tdeado.core.annotations;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tdeado.core.enums.FieldAlign;
import com.tdeado.core.enums.FileType;
import com.tdeado.core.enums.OperateType;
import com.tdeado.core.enums.SearchType;
import com.tdeado.core.jsonserializer.TdFieldSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * DTO字段
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@JacksonAnnotationsInside
@JsonSerialize(using = TdFieldSerializer.class)
public @interface TdField {

    /**
     * 导出字段名（默认调用当前字段的“get”方法，如指定导出字段为对象，请填写“对象名.对象属性”，例：“city.name”、“office.name”）
     */
    String name() default "";

    /**
     * 导出字段标题（需要添加批注请用“**”分隔，标题**批注，仅对导出模板有效）
     */
    String title();

    /**
     * 字段类型（0：导出导入；1：仅导出；2：仅导入）
     */
    OperateType operate() default OperateType.ALL;

    /**
     * 导出字段对齐方式（0：自动；1：靠左；2：居中；3：靠右）
     */
    FieldAlign align() default FieldAlign.CENTER;

    /**
     * 字段搜索
     */
    SearchType search() default SearchType.NOT;
    /**
     * 字段搜索排序
     */
    int searchSort() default Integer.MAX_VALUE;
    /**
     * 字段排序
     */
    int sort() default Integer.MAX_VALUE;

    /**
     * 文件类型 是则添加域名地址
     */
    FileType fileType() default FileType.NOT;

    /**
     * 外键对象
     */
    Class<?> foreign() default Void.class;

    /**
     * 导出字段宽度
     */
    int width() default 0;

}
