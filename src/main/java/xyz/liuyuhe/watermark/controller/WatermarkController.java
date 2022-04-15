package xyz.liuyuhe.watermark.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.liuyuhe.watermark.common.BaseResponse;
import xyz.liuyuhe.watermark.common.Result;
import xyz.liuyuhe.watermark.enums.ErrorCodeEnum;
import xyz.liuyuhe.watermark.job.AsyncTask;
import xyz.liuyuhe.watermark.request.EmbedWatermarkRequest;
import xyz.liuyuhe.watermark.request.ExtractWatermarkRequest;

import java.util.UUID;

@RestController
@RequestMapping("/v1/watermark")
@Slf4j
public class WatermarkController {
    @Autowired
    private AsyncTask asyncTask;

    /**
     * 嵌入水印
     */
    @CrossOrigin
    @GetMapping("/embed")
    public BaseResponse<String> embed(EmbedWatermarkRequest embedWatermarkRequest) {
        // 校验参数
        if (null == embedWatermarkRequest) {
            return Result.error(ErrorCodeEnum.REQUEST_PARAMS_ERROR.getCode(), ErrorCodeEnum.REQUEST_PARAMS_ERROR.getMessage());
        }
        if (StringUtils.isBlank(embedWatermarkRequest.getImageUrl())) {
            return Result.error(ErrorCodeEnum.REQUEST_PARAMS_ERROR.getCode(), "imageUrl不能为空");
        }
        if (StringUtils.isBlank(embedWatermarkRequest.getWatermarkUrl())) {
            return Result.error(ErrorCodeEnum.REQUEST_PARAMS_ERROR.getCode(), "watermarkUrl不能为空");
        }
        if (StringUtils.isBlank(embedWatermarkRequest.getPassword())) {
            return Result.error(ErrorCodeEnum.REQUEST_PARAMS_ERROR.getCode(), "password不能为空");
        }
        String uuid = embedWatermarkRequest.getUuid();
        if (StringUtils.isBlank(uuid)) {
            return Result.error(ErrorCodeEnum.REQUEST_PARAMS_ERROR.getCode(), "uuid不能为空");
        }
        asyncTask.embed(embedWatermarkRequest.getImageUrl(), embedWatermarkRequest.getWatermarkUrl(), embedWatermarkRequest.getPassword(), uuid);
        return Result.success(uuid);
    }

    /**
     * 提取水印
     */
    @CrossOrigin
    @GetMapping("/extract")
    public BaseResponse<String> extract(ExtractWatermarkRequest extractWatermarkRequest) {
        // 校验参数
        if (null == extractWatermarkRequest) {
            return Result.error(ErrorCodeEnum.REQUEST_PARAMS_ERROR.getCode(), ErrorCodeEnum.REQUEST_PARAMS_ERROR.getMessage());
        }
        if (StringUtils.isBlank(extractWatermarkRequest.getImageUrl())) {
            return Result.error(ErrorCodeEnum.REQUEST_PARAMS_ERROR.getCode(), "imageUrl不能为空");
        }
        if (StringUtils.isBlank(extractWatermarkRequest.getPassword())) {
            return Result.error(ErrorCodeEnum.REQUEST_PARAMS_ERROR.getCode(), "password不能为空");
        }
        String uuid = extractWatermarkRequest.getUuid();
        if (StringUtils.isBlank(uuid)) {
            return Result.error(ErrorCodeEnum.REQUEST_PARAMS_ERROR.getCode(), "uuid不能为空");
        }
        asyncTask.extract(extractWatermarkRequest.getImageUrl(), extractWatermarkRequest.getPassword(), uuid);
        return Result.success(uuid);
    }

    @CrossOrigin
    @GetMapping("/uuid")
    public BaseResponse<String> getUUID() {
        return Result.success(UUID.randomUUID().toString());
    }
}
