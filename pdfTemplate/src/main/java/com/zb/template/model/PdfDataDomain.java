package com.zb.template.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PdfDataDomain {
    Map<String, String> dataMap;
    List<ImgDomain> imgList;
}
