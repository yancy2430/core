package com.tdeado.core.provider;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tdeado.core.annotations.TdField;
import com.tdeado.core.entity.Entity;
import com.tdeado.core.enums.OperateType;
import com.tdeado.core.enums.SearchType;
import com.tdeado.core.util.SpringUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline;

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

        if (methodParameter.getParameterType().getName().equals("com.baomidou.mybatisplus.core.conditions.query.QueryWrapper")){
            ContentCachingRequestWrapper request = new ContentCachingRequestWrapper(nativeWebRequest.getNativeRequest(HttpServletRequest.class));
            if (request == null) {
                throw  new RuntimeException(" request must not be null!");
            }

            ContentCachingRequestWrapper requestWapper = null;
            if(request instanceof HttpServletRequest){
                requestWapper = (ContentCachingRequestWrapper) request;
            }
            String body = IOUtils.toString(requestWapper.getInputStream(),request.getCharacterEncoding());
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
            QueryWrapper<Entity> queryWrapper = new QueryWrapper<>();
            //泛型类型
            Type type = ((ParameterizedType) methodParameter.getGenericParameterType()).getActualTypeArguments()[0];
            //通过反射获取泛型的类属性列表
            Class<?> aClass = Class.forName(type.getTypeName());
            String table = StringUtils.firstToLowerCase(aClass.getSimpleName());
            IService impl=SpringUtils.getBean("sysTableCustomServiceImpl");


            List<Map<String,Object>> ls = impl.listMaps((Wrapper) new QueryWrapper().eq("table_name", table));

            for (Map<String,Object> declaredField : ls) {
                String fieldName = declaredField.get("tableField").toString();
               Integer search_type = Integer.parseInt(declaredField.get("searchType").toString());
                if (null==json.get(fieldName)){
                    continue;
                }
                String fieldValue = json.get(fieldName).getAsString();
                if (search_type == 0 || StringUtils.isBlank(fieldValue)) {
                    continue;
                }

                if (search_type == 1) {
                    queryWrapper.like(fieldName, fieldValue);
                }
                if (search_type == 2 || search_type==3) {
                    queryWrapper.eq(fieldName, fieldValue);
                }
                if (search_type == 4 || search_type == 6 || search_type == 8) {
                    List<String> datetimes = Arrays.asList(fieldValue.split(","));
                    queryWrapper.between(datetimes.size()==2,fieldName,datetimes.get(0), datetimes.get(1));
                }
            }
            return queryWrapper;
        }
        return requestResponseBodyMethodProcessor.resolveArgument(methodParameter,
                modelAndViewContainer, nativeWebRequest, webDataBinderFactory);
    }
}