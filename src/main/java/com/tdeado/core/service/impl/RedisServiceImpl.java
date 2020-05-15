package com.tdeado.core.service.impl;

import com.tdeado.core.service.RedisService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    @Resource
    private RedisTemplate<String, ?> redisTemplate;

    @Override
    public boolean set(final String key, final Object value) {
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer serializer = redisTemplate.getValueSerializer();
                connection.set(key.getBytes(), serializer.serialize(value));
                return true;
            }
        });
        return result;
    }

    @Override
    public boolean set(String key, String value, long expire) {
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer serializer = redisTemplate.getValueSerializer();
                connection.set(key.getBytes(), serializer.serialize(value), Expiration.from(expire,TimeUnit.SECONDS), RedisStringCommands.SetOption.UPSERT);
                return true;
            }
        });
        return result;
    }

    @Override
    public Object get(final String key) {
        Object result = redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer serializer = redisTemplate.getValueSerializer();
                byte[] value = connection.get(key.getBytes());
                return serializer.deserialize(value);
            }
        });
        return result;
    }

    @Override
    public boolean expire(final String key, long expire) {
        return redisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    @Override
    public boolean remove(final String key) {
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                connection.del(key.getBytes());
                return true;
            }
        });
        return result;
    }

    @Override
    public List keys(String key) {
        List result = redisTemplate.execute(new RedisCallback<List>() {
            @Override
            public List doInRedis(RedisConnection connection) throws DataAccessException {

                return new ArrayList<>(connection.keys(key.getBytes())) ;
            }
        });
        return result;
    }

    @Override
    public boolean removes(final List<String> key) {
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                connection.openPipeline();
                for (String s : key) {
                    connection.del(s.getBytes());
                }
                connection.closePipeline();
                return true;
            }
        });
        return result;
    }
    @Override
    public boolean lpush(String key, String value) {
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                connection.lPush(serializer.serialize(key), serializer.serialize(value));
                return true;
            }
        });
        return result;
    }

    @Override
    public long llen(String key) {
        long result = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                return connection.lLen(serializer.serialize(key));
            }
        });
        return result;
    }

    @Override
    public String rpop(String key) {
        String result = redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] value = connection.rPop(serializer.serialize(key));
                return serializer.deserialize(value);
            }
        });
        return result;
    }

    @Override
    public boolean sadd(String key, String value) {
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                connection.sAdd(serializer.serialize(key), serializer.serialize(value));
                return true;
            }
        });
        return result;
    }

    @Override
    public boolean ismember(String key, String value) {
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                return connection.sIsMember(serializer.serialize(key), serializer.serialize(value));
            }
        });
        return result;
    }

    @Override
    public List<String> scanIter(String match) {

        List<String> result = redisTemplate.execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                ScanOptions options = ScanOptions.scanOptions().match(match).count(Integer.MAX_VALUE).build();
                Cursor<byte[]> list = connection.scan(options);
                System.err.println(list.getCursorId());
                List<String> strs = new ArrayList<>();
                while (list.hasNext()){
                    strs.add(new String(list.next()));
                }
                return strs;
            }
        });
        return result;
    }


}
