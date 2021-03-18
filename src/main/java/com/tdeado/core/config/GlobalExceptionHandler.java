package com.tdeado.core.config;


import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.tdeado.core.exception.WebException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.message.AuthException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 通用 Api Controller 全局异常处理
 * </p>
 *
 * @author jobob
 * @since 2018-09-27
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /***
     * 参数绑定异常
     * @date 2018/10/16
     * @param exception HttpMessageNotReadableException
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public R<Object> messageNotReadable(HttpMessageNotReadableException exception){
        log.error("HttpMessageNotReadableException",exception);
        InvalidFormatException formatException = (InvalidFormatException)exception.getCause();
        List<JsonMappingException.Reference> e = formatException.getPath();
        String fieldName = "";
        for (JsonMappingException.Reference reference :e){
            fieldName = reference.getFieldName();
        }
        return R.failed(fieldName+"参数类型不匹配");
    }

    /**
     * <p>
     * 自定义 REST 业务异常
     * <p>
     *
     * @param e 异常类型
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public R<Object> handleBadRequest(Exception e) {
        return WebException.handleBadRequest(e);
    }
}