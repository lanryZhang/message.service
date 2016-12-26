/*
* HandlerMapper.java 
* Created on  202016/12/15 13:30 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core.distribute.handlers.http;

import com.ifeng.core.MessageProcessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class HandlerMapper {
    private Map<String,MessageProcessor> mapper = new ConcurrentHashMap<>();

    public void registHandler(String key, MessageProcessor processor){
        this.mapper.put(key,processor);
    }
    public MessageProcessor get(String key){
        return mapper.get(key);
    }
}
