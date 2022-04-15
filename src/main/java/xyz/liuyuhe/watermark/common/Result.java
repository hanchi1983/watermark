package xyz.liuyuhe.watermark.common;

public class Result {
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, "操作成功", data);
    }

    public static <T> BaseResponse<T> error(Integer code, String msg) {
        return new BaseResponse<>(code, msg, null);
    }
}
