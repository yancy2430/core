package com.tdeado.core.provider;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tdeado.core.entity.Entity;
import com.tdeado.core.util.QueryConversionUtil;
import com.tdeado.core.util.SpringUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class QueryHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 解析Content-Type为application/json的默认解析器是RequestResponseBodyMethodProcessor
     */
    private RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor;

    /**
     * 解析Content-Type为application/x-www-form-urlencoded的默认解析器是ServletModelAttributeMethodProcessor
     */
    private ServletModelAttributeMethodProcessor servletModelAttributeMethodProcessor;

    /**
     * 全参构造
     */
    public QueryHandlerMethodArgumentResolver(RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor,
                                              ServletModelAttributeMethodProcessor servletModelAttributeMethodProcessor) {
        this.requestResponseBodyMethodProcessor = requestResponseBodyMethodProcessor;
        this.servletModelAttributeMethodProcessor = servletModelAttributeMethodProcessor;
    }

    /**
     * 当参数前有@RequestBody注解时， 解析该参数 会使用此 解析器
     *
     * 注:此方法的返回值将决定:是否使用此解析器解析该参数
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(RequestBody.class);
    }

    /**
     * 解析参数
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory)
            throws Exception {
        RepeatableRequestWrapper request = new RepeatableRequestWrapper(nativeWebRequest.getNativeRequest(HttpServletRequest.class));

        if (methodParameter.getParameterType().getName().equals("com.baomidou.mybatisplus.core.conditions.query.QueryWrapper")){
            String body = IOUtils.toString(request.getInputStream(),request.getCharacterEncoding());
            if (StringUtils.isBlank(body)){
                return new QueryWrapper<>();
            }
            QueryWrapper<Entity> queryWrapper = new QueryWrapper<>();
            //泛型类型
            Type type = ((ParameterizedType) methodParameter.getGenericParameterType()).getActualTypeArguments()[0];
            //通过反射获取泛型的类属性列表
            Class<?> aClass = Class.forName(type.getTypeName());
            return QueryConversionUtil.requestToQuery(request,aClass,body);

        }
        Object obj = requestResponseBodyMethodProcessor.resolveArgument(methodParameter,
                modelAndViewContainer, new ServletWebRequest(request), webDataBinderFactory);
        JsonElement jsonObj = JsonParser.parseString(new String(request.getInputStream().readAllBytes()));
        if (jsonObj.isJsonObject()) {
            for (Map.Entry<String, JsonElement> stringJsonElementEntry : jsonObj.getAsJsonObject().entrySet()) {
                request.setAttribute(stringJsonElementEntry.getKey(),stringJsonElementEntry.getValue());
            }
        }

        return obj;
    }
}