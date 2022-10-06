package com.tdeado.core.service;

import java.util.List;

public interface RedisService {

    /**
     * set存数据
     * @param key
     * @param value
     * @return
     */
    boolean set(String key, Object value);
    /**
     * set存数据
     * @param key
     * @param value
     * @return
     */
    boolean set(String key, String value, long expire);

    /**
     * get获取数据
     * @param key
     * @return
     */
    Object get(String key);

    /**
     * 设置有效天数
     * @param key
     * @param expire
     * @return
     */
    boolean expire(String key, long expire);

    /**
     * 移除数据
     * @param key
     * @return
     */
    boolean remove(String key);

    /**
     * 查找key 支持通配符
     * @param key 关键词
     * @return
     */
    List keys(String key);

    /**
     * 移除数据列表
     * @param key
     * @return
     */
    public boolean removes(List<String> key);

    /**
     * 左侧添加一个元素
     * @param key 列名
     * @param value 值
     * @return
     */
    boolean lpush(String key, String value);
    /**
     * 右侧添加一个元素
     * @param key 列名
     * @param value 值
     * @return
     */
    boolean rpush(String key, String value);
    /**
     * 队列长度
     * @param key 队列名
     * @return
     */
    long llen(String key);
    /**
     * 右侧弹出一个元素
     * @param key 队列长度
     * @return
     */
    String lpop(String key);
    /**
     * 右侧弹出一个元素
     * @param key 队列长度
     * @return
     */
    String rpop(String key);

    /**
     * 添加一个集合
     * @param key 集合名
     * @param value 值
     * @return
     */
    boolean sadd(String key, String value);

    /**
     * 判断一个元素是否已经在集合里
     * @param key 集合名
     * @param value 值
     * @return
     */
    boolean ismember(String key, String value);

    /**
     * 通过迭代器游标获取key列表
     * @param match
     * @return
     */
    List<String> scanIter(String match);


}