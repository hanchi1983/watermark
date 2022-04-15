package xyz.liuyuhe.watermark.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("watermark_info")
public class WatermarkInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String hashValue;
    private Integer width;
    private Integer height;
    private Integer type;
}
