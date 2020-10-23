package com.zb.template.service;


import com.zb.template.domain.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoService {
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存记录
     *
     * @param student
     */
    public void save(Student student) {
        mongoTemplate.save(student);
    }

    /**
     * 批量保存记录
     *
     * @param callBackMsgList
     */
    public void saveList(List<Student> callBackMsgList, String collectionName) {
        mongoTemplate.insert(callBackMsgList, collectionName);
    }

    /**
     * 根据id查询
     *
     * @param groupId
     * @return
     */
    public List<Student> findMsgByGroupId(String groupId) {
        return mongoTemplate.findAll(Student.class, groupId);
    }

    /**
     * 删除记录
     *
     * @param groupId
     */
    public void removeByGroupId(String groupId) {
        Query query = new Query(Criteria.where("GroupId").is(groupId));
        mongoTemplate.remove(query, Student.class);
    }
}
