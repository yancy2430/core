package com.tdeado.core.provider;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tdeado.core.annotations.RequestQuery;
import com.tdeado.core.annotations.TdField;
import com.tdeado.core.enums.OperateType;
import com.tdeado.core.enums.SearchType;
import org.apache.commons.io.IOUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
public class QueryParamProvider implements HandlerMethodArgumentResolver {
    /**
     * 判断是否是需要我们解析的参数类型
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(RequestQuery.class);
    }

    /**
     * 参数查询组装
     * @param methodParameter
     * @param modelAndViewContainer
     * @param nativeWebRequest
     * @param webDataBinderFactory
     * @return
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        String body = getBodyTxt(request);
        RequestQuery requestQuery = methodParameter.getParameterAnnotation(RequestQuery.class);
        methodParameter.getParameterType().getField("");
        QueryWrapper queryWrapper = new QueryWrapper();
        if (requestQuery.dto().equals(Void.class)){


        }else{
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
            for (Field declaredField : requestQuery.dto().getDeclaredFields()) {
                TdField field = declaredField.getAnnotation(TdField.class);
                String fieldName = declaredField.getName();
                String fieldValue = json.get(fieldName).getAsString();
                if (Objects.isNull(field) || field.search() == SearchType.NOT || StringUtils.isBlank(fieldValue) || field.operate() == OperateType.EXPORT) {
                    continue;
                }
                fieldName = camelToUnderline(declaredField.getName());
                declaredField.setAccessible(true);
                if (field.search() == SearchType.LIKE) {
                    queryWrapper.like(fieldName, fieldValue);
                }
                if (field.search() == SearchType.EQ) {
                    queryWrapper.eq(fieldName, fieldValue);
                }
                if (field.search() == SearchType.IN) {
                    List<String> id = Arrays.asList(fieldValue.split(","));
                    queryWrapper.in(id.size() > 0, fieldName, id);
                }
            }
        }
        return queryWrapper;
    }
    public String getBodyTxt(HttpServletRequest request) throws IOException {
        BufferedReader br = request.getReader();
        String str;
        StringBuilder wholeStr = new StringBuilder();
        while((str = br.readLine()) != null){
            wholeStr.append(str);
        }
        return wholeStr.toString();
    }
    /**
     * 驼峰转下划线
     *
     * @return
     */
    public static String camelToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append("_");
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
