package xyz.liuyuhe.watermark.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("record")
public class Record {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String password;
    private String secretKey;
}
