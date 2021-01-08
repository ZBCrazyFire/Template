package com.zb.template.controller;

import com.zb.template.model.ImgDomain;
import com.zb.template.model.PdfDataDomain;
import com.zb.template.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("pdf")
public class PdfController {
    @Autowired
    private PdfService pdfService;

    @PostMapping("createPdf")
    public String createPdf(@RequestBody PdfDataDomain pdfDataDomain) {
        Map<String, String> dataMap = pdfDataDomain.getDataMap();
        List<ImgDomain> imgList = pdfDataDomain.getImgList();
        pdfService.createPdfByTemplate(dataMap, imgList);
        return "success";
    }
}
