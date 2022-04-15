package xyz.liuyuhe.watermark.request;

import lombok.Data;

@Data
public class ExtractWatermarkRequest {
    private String imageUrl;
    private String password;
    private String uuid;
}
