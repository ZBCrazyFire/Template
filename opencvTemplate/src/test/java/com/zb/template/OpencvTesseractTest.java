package com.zb.template;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;

import static org.opencv.imgcodecs.Imgcodecs.imread;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class OpencvTesseractTest {

    private static final String EOL = System.getProperty("line.separator");

    private static final String LIB_OPENCV = "lib/opencv_java453.dll";

    static {
        // 加载动态库
        URL url = ClassLoader.getSystemResource(LIB_OPENCV);
        System.load(url.getPath());
    }
    @Test
    public void testOpencv() throws Exception {
        // 解决awt报错问题
        System.setProperty("java.awt.headless", "false");
        System.out.println(System.getProperty("java.library.path"));
        // 读取图像
        Mat srcMat = imread("D:/opencv/opencvtest/resImage.jpg");
        if (srcMat.empty()) {
            throw new Exception("image is empty");
        }

        // 去噪
//        Mat denoisingMat = new Mat(); //去噪
//        Imgproc.blur(srcMat, denoisingMat, new Size(3,3), new Point(-1, -1), Core.BORDER_DEFAULT);
//
//        BufferedImage denoisingImage = toBufferedImage(denoisingMat);
//        saveJpgImage(denoisingImage, "D:/opencv/opencvtest/denoisingImage.jpg");
//        System.out.println("保存去噪图像！");

        // 原始图像转化为灰度图像
        Mat grayMat = new Mat(); //灰度图像
        Imgproc.cvtColor(srcMat, grayMat, Imgproc.COLOR_RGB2GRAY);
        BufferedImage grayImage = toBufferedImage(grayMat);
        saveJpgImage(grayImage, "D:/opencv/opencvtest/grayImage.jpg");
        System.out.println("保存灰度图像！");

        /**
         * 3、对灰度图像进行二值化处理
         */
        Mat binaryMat = new Mat(grayMat.height(), grayMat.width(), CvType.CV_8UC1);
        Imgproc.threshold(grayMat, binaryMat, 100, 255, Imgproc.THRESH_BINARY);
        BufferedImage binaryImage = toBufferedImage(binaryMat);
        saveJpgImage(binaryImage, "D:/opencv/opencvtest/binaryImage.jpg");
        System.out.println("保存二值化图像！");

        /**
         * 4、图像腐蚀---腐蚀后变得更加宽,粗.便于识别--使用3*3的图片去腐蚀
         */
        Mat destMat = new Mat(); //腐蚀后的图像
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.erode(binaryMat, destMat, element);
        BufferedImage destImage = toBufferedImage(destMat);
        saveJpgImage(destImage, "D:/opencv/opencvtest/destImage.jpg");
        System.out.println("保存腐蚀化后图像！");

        /**
         * 5 图片切割
         */

        try {
            String result = recognizeText(new File("D:/opencv/opencvtest/grayImage.jpg"));
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将Mat图像格式转化为 BufferedImage
     *
     * @param matrix mat数据图像
     * @return BufferedImage
     */
    private static BufferedImage toBufferedImage(Mat matrix) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (matrix.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
        byte[] buffer = new byte[bufferSize];
        matrix.get(0, 0, buffer); // 获取所有的像素点
        log.info("width---{}----height---{}", matrix.cols(), matrix.rows());
        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }


    /**
     * 将BufferedImage内存图像保存为图像文件
     *
     * @param image    BufferedImage
     * @param filePath 文件名
     */
    private static void saveJpgImage(BufferedImage image, String filePath) {

        try {
            ImageIO.write(image, "jpg", new File(filePath));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String recognizeText(File file) {
        ITesseract instance = new Tesseract();
        instance.setDatapath("D:\\tool\\tesseract-OCR\\tessdata");
        // 默认是英文（识别字母和数字），如果要识别中文(数字 + 中文），需要制定语言包
        instance.setLanguage("chi_sim");
        String result = "";
        try {
            result = instance.doOCR(file);

        } catch (TesseractException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
}
