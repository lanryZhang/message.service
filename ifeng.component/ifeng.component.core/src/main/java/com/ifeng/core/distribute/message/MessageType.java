package com.ifeng.core.distribute.message;

/**
 * Created by zhanglr on 2016/8/28.
 */
public class MessageType {
    /**
     * 业务请求消息
     */
    public static byte CONTENT_REQ = 0;
    /**
     * 业务响应消息
     */
    public static byte CONTENT_RESP = 1;
    /**
     * 业务ONE WAY消息 （既是请求消息也是响应消息）
     */
    public static byte ONE_WAY = 2;
    /**
     * 握手请求消息
     */
    public static byte AUTH_REQ = 3;
    /**
     * 握手响应消息
     */
    public static byte AUTH_RESP = 4;
    /**
     * 心跳请求消息
     */
    public static byte HEART_REQ = 5;
    /**
     * 心跳应答消息
     */
    public static byte HEART_RESP = 6;

    /**
     * 监控收集请求
     */
    public static byte MONITOR_REQ = 7;

    /**
     * 监控应答请求
     */
    public static byte MONITOR_RESP = 8;
}
