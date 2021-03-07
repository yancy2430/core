package com.tdeado.core.service;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sun.tools.javac.util.StringUtils;
import com.tdeado.core.annotations.MapBean;
import com.tdeado.core.annotations.MapId;
import com.tdeado.core.annotations.MapName;
import com.tdeado.core.util.PackagUtils;
import com.tdeado.core.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline;
import static com.baomidou.mybatisplus.core.toolkit.StringUtils.underlineToCamel;

@Slf4j
@Component
public class ForeignConversionService {
    @Autowired
    RedisService redisService;
    @Autowired
    SpringUtils springUtils;
    public Object conversion(Class cls,String data){
        String idField ="";
        for (Field declaredField : cls.getDeclaredFields()) {
            if (null != declaredField.getAnnotation(MapId.class)) {
                idField = declaredField.getName();
                break;
            }
        }
        return redisService.get(cls.getName()+"_"+idField+"_"+data);
    }
    public Object recover(Class cls,String data){
        String nameField ="";
        for (Field declaredField : cls.getDeclaredFields()) {
            if (null != declaredField.getAnnotation(MapName.class)) {
                nameField = declaredField.getName();
                break;
            }
        }
        return redisService.get(cls.getName()+"_"+nameField+"_"+data);
    }
    @Value("${mybatis-plus.type-aliases-package}")
    private String entityPackage;
    @Value("${mybatis-plus.global-config.db-config.table-prefix}")
    private String prefix;
    @PostConstruct
    public void init(){
        new Thread(() -> {
            try {
                initializeForeignCache();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void initializeForeignCache() throws IllegalAccessException {
        log.debug("开始初始化外键缓存");
        List<Class<?>> ps = PackagUtils.getClasssFromPackage(entityPackage);
        for (Class<?> p : ps) {
            MapBean map = p.getAnnotation(MapBean.class);
            if (null!=map){
                String idField ="";
                String nameField ="";
                for (Field declaredField : p.getDeclaredFields()) {
                    if (null != declaredField.getAnnotation(MapName.class)) {
                        nameField = declaredField.getName();
                    }
                    if (null != declaredField.getAnnotation(MapId.class)) {
                        idField = declaredField.getName();
                    }
                }
                TableName tableName = p.getAnnotation(TableName.class);
                String table = tableName.value().replace(prefix,"");
                BaseMapper baseMapper = springUtils.getBean(underlineToCamel(table) + "Mapper");
                List<Map<String,Object>> list = baseMapper.selectMaps(new QueryWrapper().select(camelToUnderline(idField)+" as value",camelToUnderline(nameField)+" as label"));
                for (Map<String, Object> stringMap : list) {
                    redisService.set(p.getName()+"_"+idField+"_"+stringMap.get("value").toString(),stringMap);
                    redisService.set(p.getName()+"_"+nameField+"_"+stringMap.get("label").toString(),stringMap);
                }
            }
        }
        log.debug("初始化外键缓存结束");
    }
}
