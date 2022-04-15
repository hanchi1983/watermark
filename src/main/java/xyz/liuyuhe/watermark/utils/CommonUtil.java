package xyz.liuyuhe.watermark.utils;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.util.DigestUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class CommonUtil {
    private final static String SALT = "!@#$%^&*()";

    /**
     * 生成随机字符串
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * md5加密
     */
    public static String md5(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        String text = SALT + str;
        return DigestUtils.md5DigestAsHex(text.getBytes());
    }

    /**
     * 计算文件的MD5
     */
    public static String fileMD5(String path) throws IOException {
        return DigestUtils.md5DigestAsHex(new FileInputStream(path));
    }

    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                jsonObject.put(key, map.get(key));
            }
        }
        return jsonObject.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }
}
