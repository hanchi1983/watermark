package xyz.liuyuhe.watermark.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import xyz.liuyuhe.watermark.common.BaseResponse;
import xyz.liuyuhe.watermark.common.Result;
import xyz.liuyuhe.watermark.enums.ErrorCodeEnum;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 业务异常
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<String> handleBusinessException(BusinessException e) {
        log.error("BusinessException", e);
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 运行时异常
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<String> handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException", e);
        return Result.error(ErrorCodeEnum.SYSTEM_ERROR.getCode(), ErrorCodeEnum.SYSTEM_ERROR.getMessage());
    }
}
