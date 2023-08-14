package com.ouyang.demo.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket 链接集合
 *
 * @author ruoyi
 */
public class WebSocketPool {
    /**
     * WebSocketPool 日志控制器
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketPool.class);

    /**
     * 画面链接集合
     */
    private static Map<Session, String> connections = new ConcurrentHashMap<Session, String>();

    /**
     * 存储画面链接
     *
     * @param session 唯一键
     * @param stageId 画面id
     */
    public static void put(Session session, String stageId) {
        connections.put(session, stageId);
    }

    /**
     * 移出用户
     *
     * @param key 键
     */
    public static boolean remove(Session key) {
        LOGGER.info("\n 正在移出链接 - {}", key);
        String remove = connections.remove(key);
        if (remove != null) {
            boolean containsKey = connections.containsKey(remove);
            LOGGER.info("\n 移出结果 - {},当前链接数 - {}", containsKey ? "失败" : "成功", WebSocketPool.getConnections().size());
            return containsKey;
        } else {
            return true;
        }
    }

    /**
     * 获取画面所有链接
     *
     * @return 链接集合
     */
    public static Map<Session, String> getConnections() {
        return connections;
    }

    /**
     * 向特定画面id发送消息，同一画面StageId代码有可能在多台客户机上连接 ，这时就会在多台客户机接受到了数据
     *
     * @param message 发送的消息
     *                stageid  画面id
     */
    public static void sendMessage(String message, String stageid) {
        Set<Map.Entry<Session, String>> entries = connections.entrySet();
        for (Map.Entry<Session, String> entry : entries) {
            Session KEY = entry.getKey();
            String value = entry.getValue();
            if (value.equals(stageid)) {
                if (KEY.isOpen()) {
                    try {
                        KEY.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        LOGGER.error("\n[发送消息异常]", e);
                    }
                } else {
                    LOGGER.info("\n[你已离线]");
                }
            }
        }
    }

}
