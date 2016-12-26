package com.ifeng.core.distribute.message;

/**
 * Created by zhanglr on 2016/8/28.
 */
public class BaseMessage{
    private Header header;
    private Object body;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
