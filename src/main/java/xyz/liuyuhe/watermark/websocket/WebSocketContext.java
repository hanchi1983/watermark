package xyz.liuyuhe.watermark.websocket;

import lombok.extern.slf4j.Slf4j;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class WebSocketContext {
    /**
     * 模拟存储 websocket session
     */
    public static final Map<String, Session> SESSIONS_CACHE = new ConcurrentHashMap<>();

    /**
     * 向所有用户发送消息
     */
    public static void sendMessageAll(String message) {
        SESSIONS_CACHE.forEach((sessionId, session) -> sendMessage(session, message));
    }

    /**
     * 向指定用户发送消息
     */
    public static void sendMessage(Session session, String message) {
        if (session == null) {
            log.error("session为空");
            return;
        }
        final RemoteEndpoint.Basic basicRemote = session.getBasicRemote();
        if (basicRemote == null) {
            log.error("basicRemote为空");
            return;
        }
        try {
            basicRemote.sendText(message);
            log.info("发送消息: " + message);
        } catch (IOException e) {
            log.error("发送消息失败: " + e.getMessage());
        }
    }
}
