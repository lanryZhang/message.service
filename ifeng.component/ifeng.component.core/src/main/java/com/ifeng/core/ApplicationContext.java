/*
* ApplicationContext.java 
* Created on  202016/12/15 13:58 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core;

import com.ifeng.core.distribute.handlers.http.HandlerMapper;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class ApplicationContext {
    public ApplicationContext(){
        this.mapper = new HandlerMapper();
    }
    private HandlerMapper mapper;

    public HandlerMapper getMapper() {
        return mapper;
    }

    public void setMapper(HandlerMapper mapper) {
        this.mapper = mapper;
    }
}
