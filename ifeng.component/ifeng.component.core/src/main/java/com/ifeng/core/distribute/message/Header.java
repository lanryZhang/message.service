package com.ifeng.core.distribute.message;

import java.util.Map;

/**
 * Created by zhanglr on 2016/8/28.
 */
public class Header {
    /**
     * 消息验证
     * crcCode=两位16进制数字+主版本号+次版本号
     * 例如：crcCode=1001101
     */
    private int crcCode;
    /**
     * 报文长度（header+body）
     */
    private int length = 4;
    /**
     * 标识ID
     */
    private long sessionId;
    /**
     * 报文类型
     * 0	业务请求消息
     * 1	业务响应消息
     * 2	业务ONE WAY消息 （既是请求消息也是响应消息）
     * 3	握手请求消息
     * 4	握手响应消息
     * 5	心跳请求消息
     * 6	心跳应答消息
     */
    private byte type;
    /**
     * 消息优先级（0--25）
     *
     */
    private byte priority;
    /**
     * 可选字段
     */
    private Map<String, Object> attachment;

    public int getCrcCode() {
        return crcCode;
    }

    public void setCrcCode(int crcCode) {
        this.crcCode = crcCode;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public Map<String, Object> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }
}
