package com.zb.template.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.zb.template.model.ImgDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class PdfService {
    Object o = new Object();
    //    获取系统文件目录
    String USR_HOME = System.getProperties().getProperty("user.home") + File.separator + "pdf" + File.separator;
    //    模板文件路径
    String LOCAL_TEMPLATE = USR_HOME + "template";
    //    生成pdf文件路径
    String CREATE_PDF_PATH = USR_HOME + "createPdf";

    public void createPdfByTemplate(Map<String, String> paramMap, List<ImgDomain> imgList) {
        PdfReader reader = null;
        FileOutputStream out = null;
        ByteArrayOutputStream bos = null;
        PdfStamper stamper = null;
        Document doc = null;
        try {
//          获取本地模板
            String templatePath = getLocalFileResource("template" + File.separator + "pdfTest.pdf");
//            读取本地字体
            String fontFilePath = getLocalFileResource("font" + File.separator + "simhei.ttf");

            String dirPath = CREATE_PDF_PATH;
            File dirFile = new File(dirPath);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }

            //            生成的pdf文件地址
            String newPDFPath = CREATE_PDF_PATH + File.separator + UUID.randomUUID() + ".pdf";
            log.info("--生成pdf文件--" + newPDFPath);

            BaseFont bf = BaseFont.createFont(fontFilePath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            out = new FileOutputStream(newPDFPath);// 输出流
            reader = new PdfReader(templatePath);// 读取pdf模板
            bos = new ByteArrayOutputStream();
            stamper = new PdfStamper(reader, bos);
            AcroFields form = stamper.getAcroFields();

//            填写数据
            java.util.Iterator<String> it = form.getFields().keySet().iterator();
            form.addSubstitutionFont(bf);
            while (it.hasNext()) {
                String name = it.next();
                String value = paramMap.get(name);
                form.setField(name, value);
            }

            if (!CollectionUtils.isEmpty(imgList)) {
                // 添加图片
                String baseImgStr = "image";
                for (int i = 0; i < imgList.size(); i++) {
                    ImgDomain imgDomain = imgList.get(i);
                    int sortNo = imgDomain.getSortNo();
                    String signPath = imgDomain.getImgPath();
                    String signFiled = baseImgStr + (sortNo);
                    int width = 80;
                    int height = 80;
                    addImg(stamper, form, signFiled, signPath, width, height);
                }
            }

            stamper.setFormFlattening(true);// 如果为false，生成的PDF文件可以编辑，如果为true，生成的PDF文件不可以编辑
            stamper.close();
            doc = new Document();
            PdfCopy copy = new PdfCopy(doc, out);
            doc.open();
            PdfImportedPage importPage = copy.getImportedPage(new PdfReader(bos.toByteArray()), 1);
            copy.addPage(importPage);
            doc.close();
        } catch (Exception e) {
            log.error("异常--", e);
        }
    }

    private static void addImg(PdfStamper stamper, AcroFields form, String fieldName, String imagePath, int w, int h) {
        try {
            // 通过域名获取所在页和坐标，左下角为起点
            int pageNo = form.getFieldPositions(fieldName).get(0).page;
            Rectangle signRect = form.getFieldPositions(fieldName).get(0).position;
            float x = signRect.getLeft();
            float y = signRect.getBottom();

            // 读图片
            Image image = Image.getInstance(imagePath);
            // 获取操作的页面
            PdfContentByte under = stamper.getOverContent(pageNo);
            // 根据域的大小缩放图片
            //image.scaleToFit(signRect.getWidth(), signRect.getHeight());
//            image.scaleToFit(120, 80);
            image.scaleToFit(w, h);
            // 添加图片
            image.setAbsolutePosition(x, y);
            under.addImage(image);
        } catch (Exception e) {
            log.error("添加图片失败--", e);
        }
    }

    //   获取本地文件资源(打成jar包读取不到本地就将，就将本地文件拷贝出来)
    private String getLocalFileResource(String relativePath) {
        String sourceRoot = LOCAL_TEMPLATE;
        String filePath = sourceRoot + File.separator + relativePath;
        File file = new File(filePath);
        if (!file.exists()) {
            synchronized (o) {
                file = new File(filePath);
                if (!file.exists()) {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    InputStream in = null;
                    BufferedInputStream buffIn = null;
                    BufferedOutputStream buffOut = null;
                    try {
                        in = this.getClass().getClassLoader().getResourceAsStream(relativePath);
                        buffIn = new BufferedInputStream(in);
                        buffOut = new BufferedOutputStream(new FileOutputStream(file));
                        int len = -1;
                        byte[] b = new byte[1024];
                        while ((len = buffIn.read(b)) != -1) {
                            buffOut.write(b, 0, len);
                        }
                    } catch (Exception e) {
                        log.error("读取本地文件资源失败", e);
                    } finally {
                        try {
                            if (in != null) in.close();
                            if (buffIn != null) buffIn.close();
                            if (buffOut != null) buffOut.close();
                        } catch (Exception e) {
                            log.error("关闭流失败", e);
                        }
                    }
                }
            }

        }
        return filePath;
    }
}
