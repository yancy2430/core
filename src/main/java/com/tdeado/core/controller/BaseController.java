package com.tdeado.core.controller;

import com.baomidou.mybatisplus.extension.api.ApiController;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Slf4j
public class BaseController extends ApiController {
    @Resource
    protected HttpServletRequest request;
    @Resource
    protected HttpServletResponse response;

    public BaseController() {
    }
}
