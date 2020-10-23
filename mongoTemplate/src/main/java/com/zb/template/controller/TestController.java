package com.zb.template.controller;

import com.zb.template.domain.Student;
import com.zb.template.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/test")
@RestController
public class TestController {
    @Autowired
    private TestService testService;

    @PostMapping("/addStudent")
    public boolean addStudent(@RequestBody Student student) {
        return testService.addStudent(student);
    }
}
