package xyz.liuyuhe.watermark.watermark;

import java.awt.image.BufferedImage;
import java.util.Map;

public class WatermarkContext {
    private final Watermark watermark;

    public WatermarkContext(Watermark watermark) {
        this.watermark = watermark;
    }

    public String doEmbed(String imageUrl, String watermarkUrl, String key, Map<String, String> resultMap) {
        return watermark.embed(imageUrl, watermarkUrl, key, resultMap);
    }

    public String doExtract(BufferedImage image, String key, int w, int h) {
        return watermark.extract(image, key, w, h);
    }
}
