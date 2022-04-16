package xyz.liuyuhe.watermark.watermark.algorithm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.liuyuhe.watermark.utils.CommonUtil;
import xyz.liuyuhe.watermark.utils.ImageUtil;
import xyz.liuyuhe.watermark.watermark.Arnold;
import xyz.liuyuhe.watermark.watermark.Logistic;
import xyz.liuyuhe.watermark.watermark.Watermark;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@Slf4j
@Service
public class Algorithm1 implements Watermark {
    @Value("${watermark.path.upload}")
    private String uploadPath;

    @Override
    public String embed(String imageUrl, String watermarkUrl, String key, Map<String, String> resultMap) {
        BufferedImage image;
        BufferedImage watermark;
        try {
            image = ImageIO.read(new URL(imageUrl));
            watermark = ImageIO.read(new URL(watermarkUrl));
        } catch (IOException e) {
            log.error("读取图片失败: " + e.getMessage());
            return null;
        }
        if (image == null || watermark == null) {
            log.error("读取图片失败");
            return null;
        }
        // 格式转换
        if (!imageUrl.endsWith("png")) {
            try {
                image = toPng(image);
            } catch (Exception e) {
                log.error("格式转换失败: " + e.getMessage());
                return null;
            }
        }
        if (!watermarkUrl.endsWith("png")) {
            try {
                watermark = toPng(image);
            } catch (Exception e) {
                log.error("格式转换失败: " + e.getMessage());
                return null;
            }
        }
        int imageWidth = image.getWidth(), imageHeight = image.getHeight();
        int watermarkWidth = watermark.getWidth(), watermarkHeight = watermark.getHeight();
        if (watermarkHeight != watermarkWidth) {
            // 调整水印的尺寸
            int val = Math.min(watermarkWidth, watermarkHeight);
            watermark = resize(watermark, val, val);
        }
        watermarkWidth = watermark.getWidth();
        watermarkHeight = watermark.getHeight();
        if (imageWidth * imageHeight < watermarkWidth * watermarkHeight) {
            log.error("水印图片尺寸不合适");
            return null;
        }
        int a, b, n, u, num, k;
        double x0;
        if (key == null) {
            // 7-11-5-0.1-4-500-127
            // 生成密钥
            Random random = new Random();
            a = random.nextInt(10) + 7;
            b = random.nextInt(10) + 11;
            n = 5 + random.nextInt(5);
            x0 = 0.1;
            u = random.nextInt(10) + 4;
            num = 500;
            k = random.nextInt(1000);
            key = a + "-" + b + "-" + n + "-" + x0 + "-" + u + "-" + num + "-" + k;
        } else {
            String[] strings = key.split("-");
            a = Integer.parseInt(strings[0]);
            b = Integer.parseInt(strings[1]);
            n = Integer.parseInt(strings[2]);
            x0 = Double.parseDouble(strings[3]);
            u = Integer.parseInt(strings[4]);
            num = Integer.parseInt(strings[5]);
            k = Integer.parseInt(strings[6]);
        }
        // 水印二值化
        watermark = ImageUtil.binaryImage(watermark);
        // 水印加密
        BufferedImage watermarkEnc = Arnold.encrypt(watermark, a, b, n);
        if (watermarkEnc == null) {
            log.error("Arnold加密失败");
            return null;
        }
        // 载体图像加密
        BufferedImage imageEnc = Logistic.encrypt(image, x0, u, num);
        if (imageEnc == null) {
            log.error("Logistic加密失败");
            return null;
        }
        // 确定嵌入位置
        int[] positionArray = generatePositionArray(imageHeight, imageWidth, watermarkHeight * watermarkWidth, k);
        // 水印嵌入
        int[][] watermarkArray = new int[watermarkHeight][watermarkWidth];
        int[][] imageArray = new int[imageHeight][imageWidth];
        for (int i = 0; i < watermarkHeight; i++) {
            for (int j = 0; j < watermarkWidth; j++) {
                final int color = watermarkEnc.getRGB(j, i);
                final int r = (color >> 16) & 0xff;
                if (r == 0) {
                    watermarkArray[i][j] = 0;
                } else {
                    watermarkArray[i][j] = 1;
                }
            }
        }
        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {
                imageArray[i][j] = imageEnc.getRGB(j, i);
            }
        }
        for (int i = 0; i < positionArray.length; i++) {
            int pos = positionArray[i];
            int watermarkValue = watermarkArray[i / watermarkWidth][i % watermarkWidth];
            int pixelPos = getEmbeddedPosition(pos);
            int pixel = imageArray[pos / imageWidth][pos % imageWidth];
            int val = watermarkValue ^ bitGet(pixel, pixelPos + 1);
            imageArray[pos / imageWidth][pos % imageWidth] = bitSet(pixel, pixelPos, val);
        }
        BufferedImage result = new BufferedImage(imageWidth, imageHeight, image.getType());
        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {
                result.setRGB(j, i, imageArray[i][j]);
            }
        }
        // 载体图像解密
        result = Logistic.decrypt(result, x0, u, num);
        if (result == null) {
            log.error("Logistic解密失败");
            return null;
        }
        // 保存图片
        String filename = CommonUtil.generateUUID();
        String suffix = ".png";
        filename = filename + suffix;
        boolean flag;
        try {
            flag = ImageIO.write(result, "png", new FileOutputStream(uploadPath + "/" + filename));
        } catch (IOException e) {
            log.error("保存图片失败: " + e.getMessage());
            return null;
        }
        if (!flag) {
            log.error("保存图片失败: " + filename);
            return null;
        }
        log.info("嵌入水印成功: " + uploadPath + "/" + filename);
        resultMap.put("key", key);
        resultMap.put("width", String.valueOf(watermarkWidth));
        resultMap.put("height", String.valueOf(watermarkHeight));
        resultMap.put("filename", filename);
        return key;
    }

    @Override
    public String extract(BufferedImage image, String key, int width, int height) {
        String[] strings = key.split("-");
        int a = Integer.parseInt(strings[0]);
        int b = Integer.parseInt(strings[1]);
        int n = Integer.parseInt(strings[2]);
        double x0 = Double.parseDouble(strings[3]);
        int u = Integer.parseInt(strings[4]);
        int num = Integer.parseInt(strings[5]);
        int k = Integer.parseInt(strings[6]);
        // 载体图像加密
        BufferedImage imageEnc = Logistic.encrypt(image, x0, u, num);
        if (imageEnc == null) {
            log.error("Logistic加密失败");
            return null;
        }
        int imageWidth = image.getWidth(), imageHeight = image.getHeight();
        // 确定嵌入位置
        int[] positionArray = generatePositionArray(imageHeight, imageWidth, width * height, k);
        // 水印提取
        int[][] watermarkArray = new int[height][width];
        int[][] imageArray = new int[imageHeight][imageWidth];
        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {
                imageArray[i][j] = imageEnc.getRGB(j, i);
            }
        }
        int black = new Color(0, 0, 0).getRGB();
        int white = new Color(255, 255, 255).getRGB();
        for (int i = 0; i < positionArray.length; i++) {
            int pos = positionArray[i];
            int pixelPos = getEmbeddedPosition(pos);
            int pixel = imageArray[pos / imageWidth][pos % imageWidth];
            int v1 = bitGet(pixel, pixelPos);
            int v2 = bitGet(pixel, pixelPos + 1);
            int w = v1 ^ v2;
            if (w == 0) {
                watermarkArray[i / width][i % width] = black;
            } else {
                watermarkArray[i / width][i % width] = white;
            }
        }
        BufferedImage result = new BufferedImage(width, height, image.getType());
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                result.setRGB(j, i, watermarkArray[i][j]);
            }
        }
        result = Arnold.decrypt(result, a, b, n);
        if (result == null) {
            log.error("Arnold解密失败");
            return null;
        }
        String filename = CommonUtil.generateUUID();
        String suffix = ".png";
        filename = filename + suffix;
        boolean flag;
        try {
            flag = ImageIO.write(result, "png", new FileOutputStream(uploadPath + "/" + filename));
        } catch (IOException e) {
            log.error("保存图片失败: " + e.getMessage());
            return null;
        }
        if (!flag) {
            log.error("保存图片失败: " + filename);
            return null;
        }
        log.info("提取水印成功: " + filename);
        return filename;
    }


    private static int bitSet(int n, int pos, int value) {
        if (value == 0) {
            int m = 0;
            return n & ((~m) ^ (1 << pos));
        } else {
            return n | (1 << pos);
        }
    }

    private static int bitGet(int n, int pos) {
        return (n & (1 << pos)) != 0 ? 1 : 0;
    }

    private static int[] generatePositionArray(int m, int n, int count, int k) {
        int[] result = new int[count];
        Random random = new Random(k);
        Set<Integer> set = new HashSet<>(m * n);
        int cnt = 0;
        do {
            int i = random.nextInt(m * n);
            if (!set.contains(i)) {
                set.add(i);
                result[cnt++] = i;
            }
        } while (cnt != count);
        Arrays.sort(result);
        return result;
    }

    private static int getEmbeddedPosition(int pos) {
        Random random = new Random(pos);
        return random.nextInt(3) + 3;
    }

    private static BufferedImage resize(BufferedImage image, int newWidth, int newHeight) {
        Image temp = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage result = new BufferedImage(newWidth, newHeight, image.getType());
        Graphics2D graphics2D = result.createGraphics();
        graphics2D.drawImage(temp, 0, 0, null);
        graphics2D.dispose();
        return result;
    }

    private BufferedImage toPng(BufferedImage image) throws Exception {
        String path = uploadPath + "/" + CommonUtil.generateUUID() + ".png";
        ImageIO.write(image, "png", new FileOutputStream(path));
        return ImageIO.read(new FileInputStream(path));
    }
}
