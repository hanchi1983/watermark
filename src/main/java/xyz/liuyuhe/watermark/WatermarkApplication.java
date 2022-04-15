package xyz.liuyuhe.watermark;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@MapperScan("xyz.liuyuhe.watermark.mapper")
@EnableWebSocket
public class WatermarkApplication {

    public static void main(String[] args) {
        SpringApplication.run(WatermarkApplication.class, args);
    }

}
