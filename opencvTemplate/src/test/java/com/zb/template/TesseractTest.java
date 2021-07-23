package com.zb.template;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TesseractTest {

    @Test
    public void test() {
        File imageFile = new File("D:\\opencv\\opencvtest\\grayImage.jpg");
        ITesseract instance = new Tesseract();
        instance.setDatapath("D:\\tool\\tesseract-OCR\\tessdata");
        // 默认是英文（识别字母和数字），如果要识别中文(数字 + 中文），需要制定语言包
        instance.setLanguage("chi_sim");
        try {
            String result = instance.doOCR(imageFile);
            System.out.println(result);
        } catch (TesseractException e) {
            System.out.println(e.getMessage());
        }

    }
}
