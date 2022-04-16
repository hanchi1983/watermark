package xyz.liuyuhe.watermark.watermark;

import java.awt.image.BufferedImage;

public class Logistic {
    public static BufferedImage encrypt(BufferedImage image, double x0, int u, int num) {
        int[][] array = getRGBArray(image);
        int w = image.getWidth(), h = image.getHeight();
        int[][] key = generate(x0, u, num, h, w);
        int[][] result = xor(array, key);
        if (result == null) {
            return null;
        }
        return getImage(result, w, h, image.getType());
    }

    public static BufferedImage decrypt(BufferedImage image, double x0, int u, int num) {
        return encrypt(image, x0, u, num);
    }

    private static BufferedImage getImage(int[][] array, int w, int h, int type) {
        BufferedImage image = new BufferedImage(w, h, type);
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                image.setRGB(j, i, array[i][j]);
            }
        }
        return image;
    }

    private static int[][] getRGBArray(BufferedImage image) {
        int w = image.getWidth(), h = image.getHeight();
        int[][] result = new int[h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                result[i][j] = image.getRGB(j, i);
            }
        }
        return result;
    }

    private static int[][] generate(double x0, int u, int num, int m, int n) {
        int[][] result = new int[m][n];
        double x = x0;
        for (int i = 1; i <= num; i++) {
            x = u * x * (1 - x);
        }
        double[] a = new double[m * n];
        a[0] = x;
        for (int i = 0; i < m * n - 1; i++) {
            a[i + 1] = u * a[i] * (1 - a[i]);
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = (int) (a[i * n + j] * Integer.MAX_VALUE);
            }
        }
        return result;
    }

    private static int[][] xor(int[][] img1, int[][] img2) {
        int h1 = img1.length, w1 = img1[0].length;
        int h2 = img2.length, w2 = img2[0].length;
        if (h1 != h2 || w1 != w2) {
            return null;
        }
        int[][] result = new int[h1][w1];
        for (int i = 0; i < h1; i++) {
            for (int j = 0; j < w1; j++) {
                result[i][j] = img1[i][j] ^ img2[i][j];
            }
        }
        return result;
    }
}
