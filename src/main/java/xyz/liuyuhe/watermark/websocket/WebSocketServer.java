package xyz.liuyuhe.watermark.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;


@ServerEndpoint("/websocket/{uuid}")
@Slf4j
@Component
public class WebSocketServer {

    @OnOpen
    public void onOpen(@PathParam("uuid") String uuid, Session session) {
        WebSocketContext.SESSIONS_CACHE.put(uuid, session);
        log.info(uuid + "开启了会话");
    }

    @OnClose
    public void onClose(@PathParam("uuid") String uuid, Session session) {
        WebSocketContext.SESSIONS_CACHE.remove(uuid);
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info(uuid + "关闭了会话");
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("我们收到消息: " + message);
    }
}
