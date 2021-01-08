package com.zb.template;

import com.zb.template.model.ImgDomain;
import com.zb.template.service.PdfService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PdfTest.class})
@AutoConfigureMockMvc
@WebAppConfiguration
@Slf4j
public class PdfTest {

    @Autowired
    private PdfService pdfService;


    @Test
    public void test() {

    }

    @Test
    public void createPdf() {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("test1", "test1");
        paramMap.put("test2", "test2");
        List<ImgDomain> imgList = new ArrayList<ImgDomain>();
        pdfService.createPdfByTemplate(paramMap, imgList);
    }
}
