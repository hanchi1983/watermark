package xyz.liuyuhe.watermark.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.liuyuhe.watermark.common.BaseResponse;
import xyz.liuyuhe.watermark.common.Result;
import xyz.liuyuhe.watermark.job.AsyncTask;

@RestController
public class TestController {
    @Autowired
    private AsyncTask asyncTask;

    @CrossOrigin
    @GetMapping("/ping")
    public BaseResponse<String> ping() {
        return Result.success("pong");
    }

    @CrossOrigin
    @GetMapping("/test")
    public BaseResponse<String> test() {
        asyncTask.testAsyncMethod();
        return Result.success("successful");
    }
}
