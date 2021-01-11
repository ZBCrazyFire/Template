package com.zb.template.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class QRUtils {
    private static MultiFormatWriter multiWriter = new MultiFormatWriter();
    private static int QR_WIDTH = 300;
    private static int QR_HEIGHT = 300;

    public static BufferedImage CreateQr(String content, InputStream inputStream, boolean needCompress) {
        BufferedImage image = null;
        try {
            Map<EncodeHintType, Object> hint = new HashMap();
            hint.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

//            生成二维码
            BitMatrix bitMatrix = multiWriter.encode(content, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hint);

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();

            image = new BufferedImage(width, height, 1);

            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? -16777216 : -1);
                }
            }

            if (inputStream != null) {
                insertImage(image, inputStream, needCompress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 如果inputStream 图片长宽大于 二维码长度 且不需要压缩 就是一张图片
     */
    public static void insertImage(BufferedImage bufferedImage, InputStream inputStream, boolean needCompress) {
        try {
            Image src = ImageIO.read(inputStream);
            int width = ((Image) src).getWidth((ImageObserver) null);
            int height = ((Image) src).getHeight((ImageObserver) null);
            if (needCompress) {
                if (width > 60) {
                    width = 60;
                }

                if (height > 60) {
                    height = 60;
                }

                Image image = ((Image) src).getScaledInstance(width, height, 4);
                BufferedImage tag = new BufferedImage(width, height, 1);
                Graphics g = tag.getGraphics();
                g.drawImage(image, 0, 0, (ImageObserver) null);
                g.dispose();
                src = image;
            }

            Graphics2D graph = bufferedImage.createGraphics();
            int x = (300 - width) / 2;
            int y = (300 - height) / 2;
            graph.drawImage((Image) src, x, y, width, height, (ImageObserver) null);
            Shape shape = new RoundRectangle2D.Float((float) x, (float) y, (float) width, (float) width, 6.0F, 6.0F);
            graph.setStroke(new BasicStroke(3.0F));
            graph.draw(shape);
            graph.dispose();
        } catch (IOException var13) {
            var13.printStackTrace();
            throw new RuntimeException(var13);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
