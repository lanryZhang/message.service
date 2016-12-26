/*
* JsonSerializer.java 
* Created on  202016/12/21 14:36 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core.serialization;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class JsonSerializer implements Serializable {
    @Override
    public Object serialize(Object obj) {
        if (null != obj) {
            return JSONObject.toJSONString(obj);
        }
        return null;
    }
}
