package com.ouyang.demo.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.annotation.Resource;

/**
 * @ClassName WebSocketConfig
 * @Description WebSocketConfigurer 配置类
 * @Author OuYang
 * @Date 2023/8/14 11:32
 * @Version 1.0
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Resource
    private MyHandler handler;

    @Resource
    private OtherHandler otherHandler;

    /**
     * 注册自定义webSocketHandler方法，并指定对应的ServerEndPoint
     * 从Spring Framework 4.1.5开始，WebSocket和SockJS的默认行为是只接受同源请求。也可以允许所有或指定的来源列表。
     *
     * @param registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/myHandler")
                .setAllowedOrigins("*")
                //拦截器，允许在handshake之前做一些操作
                .addInterceptors(new HttpSessionHandshakeInterceptor());

        registry.addHandler(otherHandler, "/ws/{clientId}")
                .setAllowedOrigins("*");
    }


}
