package xyz.liuyuhe.watermark.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.liuyuhe.watermark.entity.Record;
import xyz.liuyuhe.watermark.mapper.RecordMapper;
import xyz.liuyuhe.watermark.service.RecordService;

@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {
}
