package xyz.liuyuhe.watermark.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.liuyuhe.watermark.enums.ErrorCodeEnum;

@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    /**
     * 详细描述
     */
    private String description;

    public BusinessException(int code, String message, String description) {
        this(code, message);
        this.description = description;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
        this.description = "";
    }

    public BusinessException(ErrorCodeEnum errorCodeEnum) {
        this(errorCodeEnum.getCode(), errorCodeEnum.getMessage());
    }

    public BusinessException(ErrorCodeEnum errorCodeEnum, String description) {
        this(errorCodeEnum.getCode(), errorCodeEnum.getMessage(), description);
    }
}
