package xyz.liuyuhe.watermark.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.liuyuhe.watermark.entity.WatermarkInfo;
import xyz.liuyuhe.watermark.mapper.WatermarkInfoMapper;
import xyz.liuyuhe.watermark.service.WatermarkInfoService;

@Service
public class WatermarkInfoServiceImpl extends ServiceImpl<WatermarkInfoMapper, WatermarkInfo> implements WatermarkInfoService {
}
