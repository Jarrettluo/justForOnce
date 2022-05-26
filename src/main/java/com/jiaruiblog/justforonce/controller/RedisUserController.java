package com.jiaruiblog.justforonce.controller;

import com.alibaba.fastjson.JSONObject;
import com.jiaruiblog.justforonce.entity.redis.UserRedis;
import com.jiaruiblog.justforonce.utils.redisUtils.RedisUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Jarrett Luo
 * @Date 2022/5/26 18:34
 * @Version 1.0
 */

@RestController
public class RedisUserController {
    @GetMapping(value = "/test_hash_user")
    public Long testHashUser(){
        UserRedis userRedis = new UserRedis();
        userRedis.setName("jack");
        userRedis.setAge("25");
        userRedis.setSex("1");
        userRedis.setCity("上海");
        return
                RedisUtil.hset("hash_key_user","jack", JSONObject.toJSONString(userRedis),0);
    }
    @GetMapping(value = "/test_hash_user_get")
    public JSONObject getHashUser(){
        String result = RedisUtil.hget("hash_key_user","jack",0);
        JSONObject jsonObject = JSONObject.parseObject(result);
        return jsonObject;
    }
    @GetMapping(value = "/test_hash_user_len")
    public Long getHashLen(){
        return RedisUtil.hlen("hash_key_user",0);
    }
    @GetMapping(value = "/test_hash_user_list")
    public List<JSONObject> getHashList(){
        List<String> userList = RedisUtil.hvals("hash_key_user",0);
        List<JSONObject> jsonObjectList = new ArrayList<>();
        if (userList != null && userList.size() > 0) {
            for (int i = 0; i < userList.size(); i++) {
                String userObj = userList.get(i);
                jsonObjectList.add(JSONObject.parseObject(userObj));
            }
        }
        return jsonObjectList;
    }
}
