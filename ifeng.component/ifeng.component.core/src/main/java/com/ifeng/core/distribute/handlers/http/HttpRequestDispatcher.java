/*
* HttpRequestDispatcher.java 
* Created on  202016/12/15 13:46 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core.distribute.handlers.http;

import com.ifeng.configurable.Context;
import com.ifeng.core.AbsDispatcher;
import com.ifeng.core.Dispatcher;
import com.ifeng.core.distribute.constances.ContextConstances;
import com.ifeng.core.MessageProcessor;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class HttpRequestDispatcher extends AbsDispatcher {

    @Override
    public Object dispatch(Context context) {
        ResponseModel model = new ResponseModel();
        model.setStatus(HttpResponseStatus.NOT_FOUND);
        model.setContent("404");

        if (null != context) {
            String key = context.getString(ContextConstances.PROCESSOR_KEY,"");
            MessageProcessor processor = mapper.get(key);
            if (null != processor) {
                Object res = processor.process(context);
                model.setStatus(HttpResponseStatus.OK);
                model.setContent(res);
            }
        }
        return model;
    }
}
