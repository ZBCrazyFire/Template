package com.zb.template;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
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
public class OpencvImgTest {

    private static final String LANG_OPTION = "-l";
    private static final String EOL = System.getProperty("line.separator");

    private static final String LIB_OPENCV = "lib/opencv_java453.dll";

    static {
        // 加载动态库
        URL url = ClassLoader.getSystemResource(LIB_OPENCV);
        System.load(url.getPath());
    }

    @Test
    public void test() {
        try {
            String result = recognizeText(new File("D:/opencv/opencvtest/resImage.jpg"));
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        //获取截图的范围--从第一行开始遍历,统计每一行的像素点值符合阈值的个数,再根据个数判断该点是否为边界
        //判断该行的黑色像素点是否大于一定值（此处为150）,大于则留下,找到上边界,下边界后立即停止
        int a = 0, b = 0, state = 0;
        for (int y = 0; y < destMat.height(); y++)//行
        {
            int count = 0;
            for (int x = 0; x < destMat.width(); x++) //列
            {
                //得到该行像素点的值
                byte[] data = new byte[1];
                destMat.get(y, x, data);
                if (data[0] == 0)
                    count = count + 1;
            }
            if (state == 0)//还未到有效行
            {
                if (count >= 150)//找到了有效行
                {//有效行允许十个像素点的噪声
                    a = y;
                    state = 1;
                }
            } else if (state == 1) {
                if (count <= 150)//找到了有效行
                {//有效行允许十个像素点的噪声
                    b = y;
                    state = 2;
                }
            }
        }
        System.out.println("过滤下界" + Integer.toString(a));
        System.out.println("过滤上界" + Integer.toString(b));
        //参数,坐标X,坐标Y,截图宽度,截图长度
        Rect rect = new Rect(0, a, destMat.width(), b - a);
        Mat resMat = new Mat(destMat, rect);
        BufferedImage resImage = toBufferedImage(resMat);
        saveJpgImage(resImage, "D:/opencv/opencvtest/resImage.jpg");
        System.out.println("保存切割后图像！");
        /**
         * 识别-
         */
       /* try {
            Process  pro = Runtime.getRuntime().exec(new String[]{"D:/Program Files (x86)/Tesseract-OCR/tesseract.exe", "D:/opencv/opencvtest/resImage.jpg","D:/opencv/opencvtest/result"});
            pro.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        try {
            String result = recognizeText(new File("D:/opencv/opencvtest/resImage.jpg"));
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

    public static String recognizeText(File imageFile) throws Exception {
        /**
         * 设置输出文件的保存的文件目录
         */
        File outputFile = new File(imageFile.getParentFile(), "output");

        StringBuffer strB = new StringBuffer();

        Process pro = Runtime.getRuntime().exec(
                new String[]{
                        "D:\\tool\\tesseract-OCR\\tesseract.exe",
                        imageFile.getPath(),
                        outputFile.getPath()}
        );
        int w = pro.waitFor();
        if (w == 0) // 0代表正常退出
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    new FileInputStream(outputFile.getAbsolutePath() + ".txt"),
                    "UTF-8"));
            String str;

            while ((str = in.readLine()) != null) {
                strB.append(str).append(EOL);
            }
            in.close();
        } else {
            String msg;
            switch (w) {
                case 1:
                    msg = "Errors accessing files. There may be spaces in your image's filename.";
                    break;
                case 29:
                    msg = "Cannot recognize the image or its selected region.";
                    break;
                case 31:
                    msg = "Unsupported image format.";
                    break;
                default:
                    msg = "Errors occurred.";
            }
            throw new RuntimeException(msg);
        }
        new File(outputFile.getAbsolutePath() + ".txt").delete();
        return strB.toString().replaceAll("\\s*", "");
    }
}
