package com.ouyang.demo.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.net.URI;

/**
 * @ClassName OtherHandler
 * @Description endpoint消息处理类
 * @Author OuYang
 * @Date 2023/8/14 13:26
 * @Version 1.0
 */
@Component
public class OtherHandler extends AbstractWebSocketHandler {
    public static final Logger logger = LoggerFactory.getLogger(OtherHandler.class);


    /**
     *  socket 连接成功后被触发，同原生注解里的 @OnOpen 功能
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 当WebSocket连接建立成功时调用
        logger.info("OtherHandler,建立ws连接,session:{}",session);
        URI uri = session.getUri();
        String[] paths = uri.getPath().split("/");
        String queryPath = paths[2];
        WsSessionManager.add(session,queryPath);
    }

    /**
     * 客户端发送普通文件信息时触发，同原生注解里的 @OnMessage 功能。
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 处理接收到的WebSocket消息
        String payload = message.getPayload();
        logger.info("收到消息：{}",payload);

        // 发送回复消息给客户端
        String replyMessage = "收到消息：" + payload;
        session.sendMessage(new TextMessage(replyMessage));
    }

    /**
     *  socket 连接关闭后被触发，同原生注解里的 @OnClose 功能
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 当WebSocket连接关闭时调用
        logger.info("WebSocket连接已关闭");
    }

    /**
     * 心跳处理
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        logger.info("心跳机制，信息：{}",message);
    }

    /**
     * 同原生注解里的@OnError。
     * @param session
     * @param exception
     * @throws Exception
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("发生异常,",exception);
    }
}
