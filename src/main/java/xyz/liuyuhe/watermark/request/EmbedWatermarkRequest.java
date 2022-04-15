package xyz.liuyuhe.watermark.request;

import lombok.Data;

@Data
public class EmbedWatermarkRequest {
    private String imageUrl;
    private String watermarkUrl;
    private String password;
    private String uuid;
}
