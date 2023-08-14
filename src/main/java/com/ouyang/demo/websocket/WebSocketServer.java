package com.ouyang.demo.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 * websocket 消息处理
 * 
 * @author ruoyi
 */
@Component
@ServerEndpoint("/websocket/message/{stageId}")
public class WebSocketServer
{
    /**
     * WebSocketServer 日志控制器
     */
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("stageId") String stageId) throws Exception
    {
             // 添加链接
            WebSocketPool.put(session, stageId);
        logger.info("\n 建立连接 - {},当前链接数 - {}", session,WebSocketPool.getConnections().size());
    }

    /**
     * 连接关闭时处理
     */
    @OnClose
    public void onClose(Session session)
    {
        logger.info("\n 关闭连接 - {}", session);
        // 移除用户
        WebSocketPool.remove(session);

    }

    /**
     * 抛出异常时处理
     */
    @OnError
    public void onError(Session session, Throwable exception) throws Exception
    {
        if (session.isOpen())
        {
            // 关闭连接
            session.close();
        }
        String sessionId = session.getId();
        logger.info("\n 连接异常 - {}", sessionId);
        logger.info("\n 异常信息 - {}", exception);
        // 移出用户
        WebSocketPool.remove(session);
    }

    /**
     * 收到客户端消息时触发
     * conn 连接
     * message 收到的消息
     * 客户端离开时 会发送消息 场景id+LEAVE  客户端不会发送这个消息， 一般客户不用了都会关关浏览器，不会触发到这个事件，每次了解进来的时候都会清掉无效链接，所以不主动离开也没有关系。
     * 客户端的场景点击按钮后会发送 场景id+CONTROLCMD+操作命令+CONTROLCMD+传感器id ，收到操作命令后，可将命令发送到对应的设备
     * 客户端会定时发出心跳 格式为 场景id+HEARTCMD+00000
     */
    @OnMessage
    public void onMessage(String message, Session conn)
    {
        message = message.toString();
        logger.info("onMessage:{}",message);
        if(null != message &&message.indexOf("LEAVE")!=-1){//场景离开
             WebSocketPool.remove(conn);
        }
        if(null != message && message.indexOf("CONTROLCMD")!=-1){//客户端传来了控制命令 格式   场景控制代码CONTROLCMD控制命令CONTROLCMD传感器id
            String stageId  = message.split("CONTROLCMD")[0];//场景id
            String controlCMD  = message.split("CONTROLCMD")[1];//操作命令
            String sensorId  = message.split("CONTROLCMD")[2];//传感器id
            System.out.println("接收到客户端发送的命令："+controlCMD+"  传感器id是："+sensorId);
            //在这里编写代码，将命令发送到现场设备 这里已经拿到了 按钮命令和场景id
            //在这里编写代码，将命令发送到现场设备
            //在这里编写代码，将命令发送到现场设备
            //在这里编写代码，将命令发送到现场设备
            sendMessages("04","'命令发送成功'",stageId);
        }
        if(null != message && message.indexOf("NETTESTCMD")!=-1){//其他命令自定义

        }
        if(null != message && message.indexOf("GPLCMD")!=-1){////其他命令自定义

        }
        //心跳处理
        if(null != message && message.indexOf("HEARTCMD")!=-1){//  心跳 不用处理
            String CONTROLCMD  = message.split("HEARTCMD")[1];//心跳标识
            String CONTROLDM  = message.split("HEARTCMD")[0];//场景id
            System.out.println("接收到客户端发送的心跳："+CONTROLCMD);
            sendMessages("05","'心跳正常'",CONTROLDM);

        }
    }
    /**
     *
     * @param messagetype /01 业务数据 02 提醒数据 03 网络检测 04 命令发送后的提醒数据  05 心跳处理  现场传过来的数据为01 业务数据
     *                    该类型代码 对应 WebSocketClilent.js websocket.onmessage = function 的 几个状态，根据情况自行使用
     * @param messagecontent 内容
     * @param stageid 场景编码
     */
    public static void sendMessages(String messagetype,String messagecontent,String stageid){
        String CJNR =  "{"+
                "MESSAGETYPE:'"+messagetype+"', "+//01 业务数据 02 提醒数据
                "MESSAGECONTENT:"+messagecontent+
                "}";
        WebSocketPool.sendMessage(CJNR, stageid);
    }
}
