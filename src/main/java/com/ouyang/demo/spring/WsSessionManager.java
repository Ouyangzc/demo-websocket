package com.ouyang.demo.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName WsSessionManager
 * @Description session管理类
 * @Author OuYang
 * @Date 2023/8/14 13:15
 * @Version 1.0
 */
@Slf4j
public class WsSessionManager {
    /**
     * 保存连接 session 的地方
     */
    public static ConcurrentHashMap<WebSocketSession, String> SESSION_POOL = new ConcurrentHashMap<>();

    /**
     * 添加 session
     *
     * @param key
     */
    public static void add(WebSocketSession session, String key) {
        // 添加 session
        SESSION_POOL.put(session, key);
    }

    /**
     * 删除 session,会返回删除的 session
     *
     * @param session
     * @return
     */
    public static void remove(WebSocketSession session) {
        // 删除 session
        SESSION_POOL.remove(session);
    }

    /**
     * 删除并同步关闭连接
     *
     * @param session
     */
    public static void removeAndClose(WebSocketSession session) {
        remove(session);
        if (session != null) {
            try {
                // 关闭连接
                session.close();
            } catch (IOException e) {
                // todo: 关闭出现异常处理
                e.printStackTrace();
            }
        }
    }
}

