package com.tdeado.core.exception;


import com.baomidou.mybatisplus.extension.api.IErrorCode;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.enums.ApiErrorCode;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Web 异常
 * </p>
 *
 * @author jobob
 * @since 2018-11-26
 */
@Slf4j
public class WebException extends RuntimeException {


    public WebException() {
        super("Crab Exception");
    }

    public WebException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebException(String message) {
        super(message);
    }

    /**
     * <p>
     * 自定义 REST 业务异常
     * <p>
     *
     * @param e 异常类型
     * @return
     */
    public static R<Object> handleBadRequest(Exception e) {
        /*
         * 业务逻辑异常
         */
        if (e instanceof ApiException) {
            IErrorCode errorCode = ((ApiException) e).getErrorCode();
            if (null != errorCode) {
                log.debug("Rest request error, {}", errorCode.toString());
                return R.failed(errorCode);
            }
            log.debug("Rest request error, {}", e.getMessage());
            return R.failed(e.getMessage());
        }

        /*
         * 参数校验异常
         */
        if (e instanceof BindException) {
            BindingResult bindingResult = ((BindException) e).getBindingResult();
            if (null != bindingResult && bindingResult.hasErrors()) {
                List<Object> jsonList = new ArrayList<>();
                bindingResult.getFieldErrors().stream().forEach(fieldError -> {
                    Map<String, Object> jsonObject = new HashMap<>(2);
                    jsonObject.put("name", fieldError.getField());
                    jsonObject.put("msg", fieldError.getDefaultMessage());
                    jsonList.add(jsonObject);
                });
                return R.restResult(jsonList, ApiErrorCode.FAILED);
            }
        }

        /**
         * 系统内部异常，打印异常栈
         */
        log.error(ExceptionUtils.getFullStackTrace(e));
        return R.failed(ApiErrorCode.FAILED);
    }
}
