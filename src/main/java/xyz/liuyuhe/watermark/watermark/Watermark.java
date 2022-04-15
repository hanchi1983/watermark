package xyz.liuyuhe.watermark.watermark;

import java.awt.image.BufferedImage;
import java.util.Map;

public interface Watermark {
    String embed(String imageUrl, String watermarkUrl, String key, Map<String, String> resultMap);
    String extract(BufferedImage image, String key, int w, int h);
}
