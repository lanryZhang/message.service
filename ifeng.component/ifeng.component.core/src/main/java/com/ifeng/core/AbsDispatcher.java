/*
* KafkaRecordDispatcher.java 
* Created on  202016/12/12 15:58 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core;

import com.ifeng.core.distribute.handlers.http.HandlerMapper;
import com.ifeng.core.serialization.Deserializable;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public abstract class AbsDispatcher implements Dispatcher{
    protected HandlerMapper mapper ;
    protected Deserializable deserializable;

    public void setDeserializable(Deserializable deserializable) {
        this.deserializable = deserializable;
    }

    public void put(String key, MessageProcessor processor){
        mapper.registHandler(key ,processor);
    }
    public void setMapper(HandlerMapper mapper) {
        this.mapper = mapper;
    }
}
