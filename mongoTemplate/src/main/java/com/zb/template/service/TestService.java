package com.zb.template.service;

import com.zb.template.domain.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public boolean addStudent(Student student) {
        mongoTemplate.insert(student, "student");
        log.info("添加成功");
        return true;
    }
}
