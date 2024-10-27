package com.grocery.app.utils;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.exceptions.ResourceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public  void saveData(String key, String data) {
        try{
            redisTemplate.opsForValue().set(key, data);
        }catch (Exception e){
            throw new RuntimeException(ResCode.REDIS_ERROR.getMessage());
        }
    }
    public Object getData(String key) {
        Object data= redisTemplate.opsForValue().get((Object) key);
        if(data==null){
            throw new ResourceException();
        }
        return data;
    }

    public void removeData(String key){
        try{
            redisTemplate.delete(key);
        }catch (Exception e){
            throw new RuntimeException(ResCode.REDIS_ERROR.getMessage());
        }
    }
}
