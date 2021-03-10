package com.tdeado.core.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tdeado.core.annotations.TdField;
import com.tdeado.core.enums.OperateType;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class HeadPage<T> extends Page<T> {
    public List<Head> heads;
    @JsonIgnore
    private Class<?> entity;

    public HeadPage(long current, long size,Class<?> entity) {
        super(current, size);
        this.entity = entity;
    }

    public HeadPage(long current, long size) {
        super(current, size);
    }

    public HeadPage(long current, long size, long total) {
        super(current, size, total);
    }

    public HeadPage(long current, long size, boolean isSearchCount) {
        super(current, size, isSearchCount);
    }

    public HeadPage(long current, long size, long total, boolean isSearchCount) {
        super(current, size, total, isSearchCount);
    }

    public HeadPage(Class<?> entity) {
        this.entity = entity;
    }

    /**
     * 过滤没有TdField注解的字段
     *
     * @return
     */
    @Override
    public List<T> getRecords() {
        List<T> list = super.getRecords();
        for (T t : list) {
            for (Field declaredField : t.getClass().getDeclaredFields()) {
                TdField field = declaredField.getAnnotation(TdField.class);
                if (field != null && !field.show()) {
                    try {
                        try {
                            Method m2 = t.getClass().getMethod("set" + capitalized(declaredField.getName()), declaredField.getType());
                            m2.invoke(t, new Object[]{null});
                        } catch (NoSuchMethodException | InvocationTargetException e) {
                            e.printStackTrace();
                        }

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return list;
    }

    public static String capitalized(String content) {
        return content != null && content.length() != 0 ? content.substring(0, 1).toUpperCase() + content.substring(1) : "";
    }

    /**
     * 过滤没有TdField注解的头
     *
     * @return
     */
    public List<Head> getHeads() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (StringUtils.isNotBlank(request.getParameter("header"))) {
            List<Head> list = new ArrayList<>();
            for (Field declaredField : entity.getDeclaredFields()) {
                TdField field = declaredField.getAnnotation(TdField.class);
                if (field == null) {
                    continue;
                }
                Boolean key = null;
                if (null!=declaredField.getAnnotation(TableId.class)){
                    key = true;
                }
                list.add(new Head()
                        .setKey(key)
                        .setName(declaredField.getName())
                        .setTitle(field.title())
                        .setAlign(field.align().getDescp())
                        .setWidth(field.width())
                        .setSearch(field.search().getCode())
                        .setSort(field.sort())
                        .setContentType(field.contentType().getDescp())
                        .setOptions(field.foreign().equals(Void.class)?declaredField.getType().isEnum()?declaredField.getType().getName():null:field.foreign().getName())
                        .setShow(field.show())
                        .setAdd(field.add())
                        .setEdit(field.edit())
                        .setRequired(
                                (null!=declaredField.getAnnotation(NotNull.class))
                        )
                );
            }
            Collections.sort(list);
            return list;
        } else {
            return null;
        }
    }

    @Data
    @Accessors(chain = true)
    private static class Head implements Comparable<Head>{
        private Boolean key;
        private String name;
        private String title;
        private String align;
        private Integer width;
        private Integer search;
        private String foreign;
        private String options;
        private Integer sort;
        private Boolean show;
        private Boolean add;
        private Boolean edit;
        private String contentType;
        private Boolean required;
        public int compareTo(Head o) {
            int i = this.getSort() - o.getSort();//先按照年龄排序
            return i;
        }
    }

    @Data
    @Accessors(chain = true)
    public static class Option {
        private String value;
        private String label;
    }
}
