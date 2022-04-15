package xyz.liuyuhe.watermark.watermark.algorithm;

import org.springframework.stereotype.Service;
import xyz.liuyuhe.watermark.watermark.Watermark;

import java.awt.image.BufferedImage;
import java.util.Map;

@Service
public class Algorithm2 implements Watermark {
    @Override
    public String embed(String imageUrl, String watermarkUrl, String key, Map<String, String> resultMap) {
        return null;
    }

    @Override
    public String extract(BufferedImage image, String key, int w, int h) {
        return null;
    }
}
