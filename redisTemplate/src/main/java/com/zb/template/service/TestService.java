package com.zb.template.service;

import com.alibaba.fastjson.JSONObject;
import com.zb.template.domain.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestService {
    @Autowired
    private RedisService redisService;

    public boolean addStudent(Student student) {
        String name = student.getName();
        String studentStr = JSONObject.toJSONString(student);
        redisService.addString("name", studentStr);
        log.info("添加成功");
        return true;
    }
}
