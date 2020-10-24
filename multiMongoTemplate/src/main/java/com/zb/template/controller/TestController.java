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

    @PostMapping("/firstAddStudent")
    public boolean firstAddStudent(@RequestBody Student student) {
        return testService.firstAddStudent(student);
    }

    @PostMapping("/secondAddStudent")
    public boolean secondAddStudent(@RequestBody Student student) {
        return testService.secondAddStudent(student);
    }
}
