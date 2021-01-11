package com.zb.template.service;

import com.zb.template.util.QRUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;

@Service
@Slf4j
public class QrService {
    //    获取系统文件目录
    String USR_HOME = System.getProperties().getProperty("user.home") + File.separator + "QR" + File.separator;
    //    二维码路径
    String QR_PATH = USR_HOME + "qrPath";
    // logo路径
    String LOGO_PATH = USR_HOME + "logoPath";

    public void CreateQR(String content, String logoUrl) {
//
        String logoPath = "";
        InputStream inputStream = null;
        ByteArrayOutputStream out = null;
        FileOutputStream fos = null;
        File outFile = null;
        try {
            log.info("-----createQRCodeWithFace try------");
            File qrDir = new File(QR_PATH);
            if (qrDir == null || !qrDir.exists()) {
                qrDir.mkdirs();
            }

            File logoDir = new File(LOGO_PATH);
            if (logoDir == null || !logoDir.exists()) {
                logoDir.mkdirs();
            }

            String fileName = new Random().nextInt(99999999) + "." + "png";
            if (!StringUtils.isEmpty(logoUrl)) {
                //下载头像
                downloadPicture(logoUrl, LOGO_PATH, fileName);
                logoPath = LOGO_PATH + File.separator + fileName;
                //获取头像输入流
                inputStream = new FileInputStream(logoPath);
            } else {
//                屏蔽默认头像
//                inputStream = getDefaultHeaderStream();
            }

            // 生成二维码
            out = new ByteArrayOutputStream();
            BufferedImage bi = QRUtils.CreateQr(content, inputStream, true);
            ImageIO.write(bi, "PNG", out);

            String outFileName = new Random().nextInt(99999999) + "." + "png";
            outFile = new File(QR_PATH + File.separator + outFileName);
            fos = new FileOutputStream(outFile);
            out.writeTo(fos);
            out.flush();
        } catch (Exception e) {
            log.info("-----生成二维码失败------", e);
        } finally {
            try {
                //删除临时头像，保留默认头像
                if (!logoPath.contains("defaultHeader")) {
                    File file = new File(logoPath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (out != null) {
                    out.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                log.info("-----生成二维码关闭流失败------", e);
            }
        }
    }

    private static void downloadPicture(String url, String dirPath, String filePath) throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet httpget = new HttpGet(url);

        httpget.setHeader(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.79 Safari/537.1");
        httpget.setHeader("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

        try {
            HttpResponse resp = httpClient.execute(httpget);
            if (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode()) {
                HttpEntity entity = resp.getEntity();

                InputStream in = entity.getContent();

                savePicToDisk(in, dirPath, filePath);
            }

        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {

            }
        }
    }

    private static void savePicToDisk(InputStream in, String dirPath, String filePath) {

        try {
            File dir = new File(dirPath);
            if (dir == null || !dir.exists()) {
                dir.mkdirs();
            }

            //文件真实路径
            String realPath = dirPath.concat(File.separator).concat(filePath);
            File file = new File(realPath);
            if (file == null || !file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = in.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String content = "test";
        String logoUrl = "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3162544256,2388940149&fm=26&gp=0.jpg";
        QrService qrService = new QrService();
        qrService.CreateQR(content, logoUrl);
    }
}
