package com.tdeado.core.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

import java.io.Serializable;

public  interface BaseEnum<T extends Serializable> extends IEnum<T> {

    public int getCode();

    public String getDescp();
}
