package xyz.liuyuhe.watermark.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import xyz.liuyuhe.watermark.entity.Record;
import xyz.liuyuhe.watermark.entity.WatermarkInfo;
import xyz.liuyuhe.watermark.service.RecordService;
import xyz.liuyuhe.watermark.service.WatermarkInfoService;
import xyz.liuyuhe.watermark.utils.CommonUtil;
import xyz.liuyuhe.watermark.watermark.WatermarkContext;
import xyz.liuyuhe.watermark.watermark.algorithm.Algorithm1;
import xyz.liuyuhe.watermark.watermark.algorithm.Algorithm2;
import xyz.liuyuhe.watermark.websocket.WebSocketContext;

import javax.imageio.ImageIO;
import javax.websocket.Session;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class AsyncTask {
    @Value("${watermark.path.upload}")
    private String uploadPath;

    @Value("${watermark.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private RecordService recordService;

    @Autowired
    private WatermarkInfoService watermarkInfoService;

    @Autowired
    private Algorithm1 algorithm1;

    @Autowired
    private Algorithm2 algorithm2;

    @SneakyThrows
    @Async
    public void embed(String imageUrl, String watermarkUrl, String password, String uuid) {
        int type = 1; // 使用算法1
        Session session = WebSocketContext.SESSIONS_CACHE.get(uuid);
        WatermarkContext context = new WatermarkContext(algorithm1);
        Map<String, String> map = new HashMap<>();
        Record record = recordService.getOne(new QueryWrapper<Record>().eq("password", CommonUtil.md5(password)));
        String key;
        if (record != null) {
            key = record.getSecretKey();
        } else {
            key = null;
        }
        key = context.doEmbed(imageUrl, watermarkUrl, key, map);
        if (key == null) {
            // 水印嵌入失败，通知前端
            WebSocketContext.sendMessage(session, CommonUtil.getJSONString(-1, "水印嵌入失败"));
            return;
        }
        // 水印嵌入成功
        key = map.get("key");
        String width = map.get("width");
        String height = map.get("height");
        String filename = map.get("filename");
        String hash = CommonUtil.fileMD5(uploadPath + "/" + filename);
        boolean flag;
        if (record == null) {
            // 保存水印信息到数据库
            record = new Record()
                    .setSecretKey(key)
                    .setPassword(CommonUtil.md5(password));
            flag = recordService.save(record);
            if (!flag) {
                // 水印嵌入失败，通知前端
                WebSocketContext.sendMessage(session, CommonUtil.getJSONString(-1, "水印嵌入失败"));
                log.error("保存信息失败: " + record);
                return;
            }
        }
        WatermarkInfo watermarkInfo = watermarkInfoService.getOne(new QueryWrapper<WatermarkInfo>().eq("hash_value", hash));
        if (watermarkInfo == null) {
            watermarkInfo = new WatermarkInfo()
                    .setHashValue(hash)
                    .setWidth(Integer.parseInt(width))
                    .setHeight(Integer.parseInt(height))
                    .setType(type);
            flag = watermarkInfoService.save(watermarkInfo);
            if (!flag) {
                // 水印嵌入失败，通知前端
                WebSocketContext.sendMessage(session, CommonUtil.getJSONString(-1, "水印嵌入失败"));
                log.error("保存信息失败: " + watermarkInfo);
                return;
            }
        }
        // 生成web访问路径，通知前端水印嵌入成功
        String url = domain + contextPath + "/v1/image/" + filename;
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("url", url);
        WebSocketContext.sendMessage(session, CommonUtil.getJSONString(0, "水印嵌入成功", resultMap));
        log.info("水印嵌入成功: " + url);
    }


    @SneakyThrows
    @Async
    public void extract(String imageUrl, String password, String uuid) {
        Session session = WebSocketContext.SESSIONS_CACHE.get(uuid);
        BufferedImage image;
        try {
            image = ImageIO.read(new URL(imageUrl));
        } catch (IOException e) {
            WebSocketContext.sendMessage(session, CommonUtil.getJSONString(-1, "水印提取失败"));
            log.error("读取图片失败: " + e.getMessage());
            return;
        }
        if (image == null) {
            log.error("读取图片失败: " + imageUrl);
            return;
        }
        // 保存图片
        String filename = CommonUtil.generateUUID();
        String suffix = ".png";
        filename = filename + suffix;
        boolean flag;
        try {
            flag = ImageIO.write(image, "png", new FileOutputStream(uploadPath + "/" + filename));
        } catch (IOException e) {
            WebSocketContext.sendMessage(session, CommonUtil.getJSONString(-1, "水印提取失败"));
            log.error("保存图片失败: " + e.getMessage());
            return;
        }
        if (!flag) {
            WebSocketContext.sendMessage(session, CommonUtil.getJSONString(-1, "水印提取失败"));
            log.error("保存图片失败: " + filename);
            return;
        }
        // 计算文件hash
        String hash = CommonUtil.fileMD5(uploadPath + "/" + filename);
        Record record = recordService.getOne(new QueryWrapper<Record>().eq("password", CommonUtil.md5(password)));
        WatermarkInfo watermarkInfo = watermarkInfoService.getOne(new QueryWrapper<WatermarkInfo>().eq("hash_value", hash));
        if (record == null || watermarkInfo == null) {
            WebSocketContext.sendMessage(session, CommonUtil.getJSONString(-1, "图片中未检测到水印"));
            log.info("查询失败，未找到该记录[图片中不含有水印]");
            return;
        }
        String key = record.getSecretKey();
        Integer height = watermarkInfo.getHeight();
        Integer width = watermarkInfo.getWidth();
        Integer type = watermarkInfo.getType();
        WatermarkContext context;
        // 根据算法类型选择相应的算法
        switch (type) {
            case 1:
                context = new WatermarkContext(algorithm1);
                break;
            case 2:
                context = new WatermarkContext(algorithm2);
                break;
            default:
                context = null;
        }
        if (context == null) {
            WebSocketContext.sendMessage(session, CommonUtil.getJSONString(-1, "水印提取失败"));
            log.error("不支持的算法类型");
            return;
        }
        filename = context.doExtract(image, key, width, height);
        // 提取水印成功，生成web访问路径
        // web访问路径
        String url = domain + contextPath + "/v1/image/" + filename;
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("url", url);
        WebSocketContext.sendMessage(session, CommonUtil.getJSONString(0, "水印提取成功", resultMap));
        log.info("水印提取成功: " + url);
    }

    @SneakyThrows
    @Async
    public void testAsyncMethod() {
        log.info("开始执行");
        Thread.sleep(5000 * 2);
        log.info("结束执行");
    }
}
