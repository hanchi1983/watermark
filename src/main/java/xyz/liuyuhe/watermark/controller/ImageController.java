package xyz.liuyuhe.watermark.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.liuyuhe.watermark.common.BaseResponse;
import xyz.liuyuhe.watermark.common.Constant;
import xyz.liuyuhe.watermark.common.Result;
import xyz.liuyuhe.watermark.enums.ErrorCodeEnum;
import xyz.liuyuhe.watermark.exception.BusinessException;
import xyz.liuyuhe.watermark.utils.CommonUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;


@Controller
@Slf4j
@RequestMapping("/v1/image")
public class ImageController {

    @Value("${watermark.path.upload}")
    private String uploadPath;

    @Value("${watermark.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @CrossOrigin
    @GetMapping("{filename}")
    public void getImage(@PathVariable("filename") String filename, HttpServletResponse response) {
        // 服务器中存放图片的路径
        filename = uploadPath + '/' + filename;
        // 文件不存在
        if (!new File(filename).exists()) {
            throw new BusinessException(ErrorCodeEnum.REQUEST_PARAMS_ERROR);
        }
        // 文件后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                OutputStream os = response.getOutputStream();
                FileInputStream fileInputStream = new FileInputStream(filename);
        ) {
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fileInputStream.read(bytes)) != -1) {
                os.write(bytes, 0, length);
            }
        } catch (IOException e) {
            log.error("读取图片失败: " + e.getMessage());
        }
    }

    @CrossOrigin
    @PostMapping("/upload")
    @ResponseBody
    public BaseResponse<String> uploadImage(MultipartFile image) {
        // 文件校验
        if (null == image) {
            return Result.error(ErrorCodeEnum.REQUEST_PARAMS_ERROR.getCode(), "文件不能为空");
        }
        String filename = image.getOriginalFilename();
        if (null == filename) {
            return Result.error(ErrorCodeEnum.REQUEST_PARAMS_ERROR.getCode(), "文件名不能为空");
        }
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            return Result.error(ErrorCodeEnum.REQUEST_PARAMS_ERROR.getCode(), "文件格式错误");
        }
        if (!suffix.equalsIgnoreCase(".png") && !suffix.equalsIgnoreCase(".jpeg") && !suffix.equalsIgnoreCase(".jpg")) {
            return Result.error(ErrorCodeEnum.REQUEST_PARAMS_ERROR.getCode(), "文件格式错误");
        }
        if (image.getSize() > Constant.M2_TO_BYTE) {
            return Result.error(ErrorCodeEnum.REQUEST_PARAMS_ERROR.getCode(), "图片大小不能超过2M");
        }
        // 生成随机文件名
        filename = CommonUtil.generateUUID() + suffix;
        // 保存文件
        File dest = new File(uploadPath + "/" + filename);
        try {
            image.transferTo(dest);
        } catch (IOException e) {
            log.info("文件上传失败: " + e.getMessage());
            throw new BusinessException(ErrorCodeEnum.FILE_UPLOAD_ERROR);
        }
        // web访问路径
        String url = domain + contextPath + "/v1/image/" + filename;
        return Result.success(url);
    }
}
