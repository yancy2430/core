package com.tdeado.core.config;


import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
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
     * 登录异常绑定异常
     * @date 2018/10/16
     * @param exception HttpMessageNotReadableException
     */
    @ExceptionHandler(value = RuntimeException.class)
    @ResponseBody
    public R<String> runtime(RuntimeException exception){
        exception.printStackTrace();
        log.error("RuntimeException {}",exception.getMessage());
        return R.failed(exception.getMessage());
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseBody
    public R<String> runtime(MissingServletRequestParameterException exception){
        log.error("MissingServletRequestParameterException {}",exception.getMessage());
        return R.failed(exception.getMessage());
    }
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public R<String> runtime(HttpRequestMethodNotSupportedException exception){
        log.error("HttpRequestMethodNotSupportedException {}",exception.getMessage());
        return R.failed(exception.getMessage());
    }


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public R<String> runtime(MethodArgumentNotValidException c){
        List<ObjectError> errors = c.getBindingResult().getAllErrors();
        StringBuffer errorMsg = new StringBuffer();
        errors.stream().forEach(x -> {

            errorMsg.append(x.getDefaultMessage()).append(";");
        });
        return R.failed(String.valueOf(errorMsg));
    }
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    public R<String> runtime(ConstraintViolationException exception){
        Set<ConstraintViolation<?>> cves = exception.getConstraintViolations();
        StringBuffer errorMsg = new StringBuffer();
        cves.forEach(ex -> errorMsg.append(ex.getMessage()));
        return R.failed(String.valueOf(errorMsg));
    }


    /***
     * 登录异常绑定异常
     * @date 2018/10/16
     * @param exception HttpMessageNotReadableException
     */
    @ExceptionHandler(value = AuthException.class)
    @ResponseBody
    public R<Object> authForBidden(AuthException exception){
//        exception.printStackTrace();
        log.error("AuthException {}",exception.getMessage());
        return R.failed(exception.getMessage());
    }
    /***
     * 登录异常绑定异常
     * @date 2018/10/16
     * @param exception HttpMessageNotReadableException
     */
    @ExceptionHandler(value = ApiException.class)
    @ResponseBody
    public R<Object> authForBidden(ApiException exception){
//        exception.printStackTrace();
        log.error("AuthException {}",exception.getMessage());
        return R.failed(exception.getMessage());
    }
    /***
     * 参数绑定异常
     * @date 2018/10/16
     * @param exception HttpMessageNotReadableException
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public R<Object> messageNotReadable(HttpMessageNotReadableException exception){
//        exception.printStackTrace();
        log.error("HttpMessageNotReadableException {}",exception.getMessage());
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
        e.printStackTrace();
        log.error("Exception {}",e.getMessage());
        return R.failed(e.getMessage());
    }
}