package xyz.liuyuhe.watermark.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class ImageUtil {
    public static int color2RGB(int alpha, int red, int green, int blue) {
        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red;
        newPixel = newPixel << 8;
        newPixel += green;
        newPixel = newPixel << 8;
        newPixel += blue;
        return newPixel;
    }

    public static Map<String, BufferedImage> splitRGB(BufferedImage image) {
        Map<String, BufferedImage> result = new HashMap<>();
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage redImage = new BufferedImage(width, height, image.getType());
        BufferedImage greenImage = new BufferedImage(width, height, image.getType());
        BufferedImage blueImage = new BufferedImage(width, height, image.getType());
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                final int color = image.getRGB(i, j);
                final int r = (color >> 16) & 0xff;
                final int g = (color >> 8) & 0xff;
                final int b = color & 0xff;
                redImage.setRGB(i, j, color2RGB(255, r, 0, 0));
                greenImage.setRGB(i, j, color2RGB(255, 0, g, 0));
                blueImage.setRGB(i, j, color2RGB(255, 0, 0, b));
            }
        }
        result.put("red", redImage);
        result.put("green", greenImage);
        result.put("blue", blueImage);
        return result;
    }

    public static BufferedImage rgb2Gray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage grayImage = new BufferedImage(width, height, image.getType());
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                final int color = image.getRGB(i, j);
                final int r = (color >> 16) & 0xff;
                final int g = (color >> 8) & 0xff;
                final int b = color & 0xff;
                int gray = (int) (0.3 * r + 0.59 * g + 0.11 * b);
                grayImage.setRGB(i, j, color2RGB(255, gray, gray, gray));
            }
        }
        return grayImage;
    }


    public static BufferedImage binaryImage(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        float[] rgb = new float[3];
        double[][] pos = new double[w][h];
        int black = new Color(0, 0, 0).getRGB();
        int white = new Color(255, 255, 255).getRGB();
        BufferedImage b = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int pixel = image.getRGB(x, y);
                rgb[0] = (pixel & 0xff0000) >> 16;
                rgb[1] = (pixel & 0xff00) >> 8;
                rgb[2] = pixel & 0xff;
                float avg = (rgb[0] + rgb[1] + rgb[2]) / 3.0f;
                pos[x][y] = avg;
            }
        }
        double s = 192;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (pos[x][y] < s) {
                    b.setRGB(x, y, black);
                } else {
                    b.setRGB(x, y, white);
                }
            }
        }
        return b;
    }
}
