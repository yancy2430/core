package com.tdeado.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tdeado.core.entity.Entity;
import com.tdeado.core.utils.BeanUtils;
import com.tdeado.core.utils.SpringUtils;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/")
public class IController extends BaseController {

    /**
     * 说明
     *
     * @param service 服务
     * @param page    页码
     * @param size    大小
     * @param screens 筛选
     * @return
     * @ignore
     */
    @RequestMapping(value = "{service}/page", method = RequestMethod.POST)
    public R page(
            @PathVariable("service") String service,
            @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "20", required = false) Integer size,
            @RequestBody(required = false) List<Screen> screens
    ) {
        IService serviceImpl = SpringUtils.getBean("bg" + service + "ServiceImpl");
        QueryWrapper<Entity> queryWrapper = new QueryWrapper<>();
        if (!Objects.isNull(screens)) {

            for (Screen screen : screens) {
                screen.setName(StringUtils.camelToUnderline(screen.getName()));
                switch (screen.getType()) {
                    case "eq"://等于 =订单
                        queryWrapper.eq(screen.getName(), screen.getValue());
                        break;
                    case "ne"://不等于 <>
                        queryWrapper.ne(screen.getName(), screen.getValue());
                        break;
                    case "gt"://大于 >
                        queryWrapper.gt(screen.getName(), screen.getValue());
                        break;
                    case "ge"://大于等于 >=
                        queryWrapper.ge(screen.getName(), screen.getValue());
                        break;
                    case "lt"://小于 <
                        queryWrapper.lt(screen.getName(), screen.getValue());
                        break;
                    case "le"://小于等于 <=
                        queryWrapper.le(screen.getName(), screen.getValue());
                        break;
                    case "between"://BETWEEN 值1 AND 值2
                        String[] strs = screen.getValue().split(",");
                        queryWrapper.between(screen.getName(), strs[0], strs[1]);
                        break;
                    case "notBetween"://NOT BETWEEN 值1 AND 值2
                        String[] strs2 = screen.getValue().split(",");
                        queryWrapper.notBetween(screen.getName(), strs2[0], strs2[1]);
                        break;
                    case "like"://LIKE '%值%'
                        queryWrapper.like(screen.getName(), screen.getValue());
                        break;
                    case "notLike"://NOT LIKE '%值%'
                        queryWrapper.notLike(screen.getName(), screen.getValue());
                        break;
                    case "likeLeft"://LIKE '%值'
                        queryWrapper.likeLeft(screen.getName(), screen.getValue());
                        break;
                    case "likeRight"://LIKE '值%'
                        queryWrapper.likeRight(screen.getName(), screen.getValue());
                        break;
                    case "isNull"://字段 IS NULL
                        queryWrapper.isNull(screen.getName());
                        break;
                    case "isNotNull"://字段 IS NOT NULL
                        queryWrapper.isNotNull(screen.getName());
                        break;
                    case "in":// IN (value.get(0), value.get(1), ...)
                        queryWrapper.in(screen.getName(), screen.getValue().split(","));
                        break;
                    case "notIn":// IN (value.get(0), value.get(1), ...)
                        queryWrapper.notIn(screen.getName(), screen.getValue().split(","));
                        break;
                    case "orderByAsc":// IN (value.get(0), value.get(1), ...)
                        queryWrapper.orderByAsc(screen.getName());
                        break;
                    case "orderByDesc":// IN (value.get(0), value.get(1), ...)
                        queryWrapper.orderByDesc(screen.getName());
                        break;

                }
            }
        }
        return success(serviceImpl.page(new Page(page, size), queryWrapper));
    }


    /**
     * 保存实体
     *
     * @param entity 实体
     * @return
     */
    @RequestMapping(value = "{service}/save", method = RequestMethod.POST)
    public R save(@PathVariable("service") String service, @RequestBody Map<String, Object> entity)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        IService serviceImpl = SpringUtils.getBean("bg" + service + "ServiceImpl");
        Object bean = Class.forName("com.bingo.upup.common.entity.Bg" + service).getDeclaredConstructor().newInstance();
        BeanUtils.populate(bean, entity);
        return success(serviceImpl.save(bean));
    }

    /**
     * 根据ID删除实体
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "{service}/del", method = RequestMethod.POST)
    public R delete(@PathVariable("service") String service, @RequestBody List<String> id) {
        if(id.size() == 1){
            IService serviceImpl = SpringUtils.getBean("bg" + service + "ServiceImpl");
            return success(serviceImpl.removeByIds(id));
        }else {
            return failed("请求数据有误");
        }
    }
    /**
     * 根据ID删除实体
     *
     * @param ids 主键集合
     * @return
     */
    @RequestMapping(value = "{service}/dels", method = RequestMethod.POST)
    public R deleteByIds(@PathVariable("service") String service,@RequestBody List<String> ids) {
        IService serviceImpl = SpringUtils.getBean("bg" + service + "ServiceImpl");
        return success(serviceImpl.removeByIds(ids));
    }


    /**
     * 更新实体
     *
     * @param entity 实体
     * @return
     */
    @RequestMapping(value = "{service}/update", method = RequestMethod.POST)
    public R update(@PathVariable("service") String service, @RequestBody Map<String, Object> entity)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        IService serviceImpl = SpringUtils.getBean("bg" + service + "ServiceImpl");
        Object bean = Class.forName("com.bingo.upup.common.entity.Bg" + service).getDeclaredConstructor().newInstance();
        BeanUtils.populate(bean, entity);
        return success(serviceImpl.updateById(bean));
    }


    /**
     * 批量更新实体
     *
     * @param entityList 实体集合
     * @return
     */
    @RequestMapping(value = "{service}/updateBatch", method = RequestMethod.POST)
    public R update(@PathVariable("service") String service, @RequestBody List<Map<String, Object>> entityList)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        IService serviceImpl = SpringUtils.getBean("bg" + service + "ServiceImpl");
        Class<?> entityClass = Class.forName("com.bingo.upup.common.entity.Bg" + service);

        List<Object> beanList = new ArrayList<>();
        for (Map<String, Object> entity :
                entityList) {
            Object bean = entityClass.getDeclaredConstructor().newInstance();
            BeanUtils.populate(bean, entity);
            beanList.add(bean);
        }

        return success(serviceImpl.updateBatchById(beanList));
    }

    @Data
    @ToString
    private static class Screen {
        /**
         * 字段名
         */
        private String name;
        /**
         * 筛选类型 eq
         */
        private String type;
        /**
         * 值 多个用,分割
         */
        private String value;
    }

}