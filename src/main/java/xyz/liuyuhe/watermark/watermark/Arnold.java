package xyz.liuyuhe.watermark.watermark;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Arnold {
    public static BufferedImage encrypt(BufferedImage image, int a, int b, int n) {
        int w = image.getWidth();
        int h = image.getHeight();
        if (w != h) {
            return null;
        }
        int[][] source = new int[w][h];
        int[][] result = new int[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                source[i][j] = image.getRGB(i, j);
            }
        }
        for (int i = 0; i < n; i++) {
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    int xx = mod(x + b * y, h);
                    int yy = mod(a * x + (a * b + 1) * y, h);
                    result[xx][yy] = source[x][y];
                }
            }
            source = copy(result, w, h);
        }
        return getResultBufferedImage(image, w, h, result);
    }

    public static BufferedImage decrypt(BufferedImage image, int a, int b, int n) {
        int h = image.getHeight();
        int w = image.getWidth();
        if (w != h) {
            return null;
        }
        int[][] source = new int[w][h];
        int[][] result = new int[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                source[i][j] = image.getRGB(i, j);
            }
        }
        for (int i = 0; i < n; i++) {
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    int xx = mod((a * b + 1) * x - b * y, h);
                    int yy = mod(-a * x + y, h);
                    result[xx][yy] = source[x][y];
                }
            }
            source = copy(result, w, h);
        }
        return getResultBufferedImage(image, w, h, result);
    }

    private static BufferedImage getResultBufferedImage(BufferedImage image, int w, int h, int[][] result) {
        BufferedImage bufferedImage = new BufferedImage(w, h, image.getType());
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                bufferedImage.setRGB(i, j, result[i][j]);
            }
        }
        return bufferedImage;
    }

    private static int mod(int number, int mod) {
        return (number % mod + mod) %mod;
    }

    private static int[][] copy(int[][] source, int w, int h) {
        int[][] dest = new int[w][h];
        for (int i = 0; i < w; i++) {
            dest[i] = Arrays.copyOf(source[i], h);
        }
        return dest;
    }
}
