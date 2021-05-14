package com.tdeado.core.util;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tdeado.core.entity.Entity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class QueryConversionUtil {

    public static QueryWrapper requestToQuery(HttpServletRequest request,Class<?> aClass,String body) throws IOException {


        QueryWrapper<Entity> queryWrapper = new QueryWrapper<>();
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        for (Map.Entry<String, JsonElement> stringJsonElementEntry : json.entrySet()) {
            request.setAttribute(stringJsonElementEntry.getKey(), stringJsonElementEntry);
        }
        //通过反射获取泛型的类属性列表
        String table = StringUtils.firstToLowerCase(aClass.getSimpleName());
        IService impl=SpringUtils.getBean("sysTableCustomServiceImpl");
        List<Map<String,Object>> ls = impl.listMaps((Wrapper) new QueryWrapper().eq("table_name", table));
        JsonObject queryJson = json.getAsJsonObject("query");
        if (null!=queryJson && !queryJson.isJsonNull()){
            for (Map.Entry<String, JsonElement> stringJsonElementEntry : queryJson.entrySet()) {
                if (stringJsonElementEntry.getValue().isJsonArray() || stringJsonElementEntry.getValue().isJsonObject()){
                    request.setAttribute(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue());
                }else if (null!=stringJsonElementEntry.getValue() && !stringJsonElementEntry.getValue().isJsonNull() && StringUtils.isNotBlank(stringJsonElementEntry.getValue().getAsString())){
                    request.setAttribute(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue().getAsString());
                }else {
                    request.setAttribute(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue());
                }
            }
            for (Map<String,Object> declaredField : ls) {

                String fieldName = declaredField.get("tableField").toString();
                Integer search_type = Integer.parseInt(declaredField.get("searchType").toString());
                if (null==queryJson.get(fieldName) || queryJson.get(fieldName).isJsonNull()){
                    continue;
                }
                StringBuilder fieldValue = new StringBuilder();
                JsonElement jsonValue = queryJson.get(fieldName);
                if (jsonValue.isJsonArray()){
                    for (JsonElement element : jsonValue.getAsJsonArray()) {
                        fieldValue.append(element.getAsString());
                        fieldValue.append(",");
                    }
                }else {
                    fieldValue = new StringBuilder(jsonValue.getAsString());
                }
                if (search_type == 0 || StringUtils.isBlank(fieldValue.toString())) {
                    continue;
                }
                fieldName = StringUtils.camelToUnderline(fieldName);
                if (search_type == 1) {
                    queryWrapper.like(fieldName, fieldValue.toString());
                }
                if (search_type == 2 || search_type==3) {
                    queryWrapper.eq(fieldName, fieldValue.toString());
                }
                if (search_type == 5 || search_type == 7 ) {
                    queryWrapper.eq(fieldName, fieldValue.toString());
                }
                if (search_type == 4 || search_type == 6 || search_type == 8) {
                    List<String> datetimes = Arrays.asList(fieldValue.toString().split(","));
                    queryWrapper.between(datetimes.size()==2,fieldName,datetimes.get(0), datetimes.get(1));
                }

            }
        }
        JsonObject sortJson = json.getAsJsonObject("sort");

        if (null!=sortJson && !sortJson.isJsonNull()){
            for (Map.Entry<String, JsonElement> sort : sortJson.entrySet()) {
                queryWrapper.orderBy(!sort.getValue().isJsonNull(),sort.getValue().getAsString().equals("desc"),StringUtils.camelToUnderline(sort.getKey()));
            }
        }else {
            String field = "";
            for (Field declaredField : aClass.getDeclaredFields()) {
                TableId fieldAnnotation = declaredField.getAnnotation(TableId.class);
                if (null!=fieldAnnotation){
                    field = declaredField.getName();
                }
            }
            queryWrapper.orderByDesc(StringUtils.isNotBlank(field),StringUtils.camelToUnderline(field));
        }
        return queryWrapper;
    }
}