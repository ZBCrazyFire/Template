package com.zb.template.service;

import com.zb.template.domain.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestService {
    @Autowired
    @Qualifier(value = "firstMongoTemplate")
    private MongoTemplate firstMongo;

    @Autowired
    @Qualifier(value = "secondMongoTemplate")
    private MongoTemplate secondMongo;

    public boolean firstAddStudent(Student student) {
        firstMongo.insert(student, "student");
        log.info("first添加成功");
        return true;
    }

    public boolean secondAddStudent(Student student) {
        secondMongo.insert(student, "student");
        log.info("second添加成功");
        return true;
    }
}
