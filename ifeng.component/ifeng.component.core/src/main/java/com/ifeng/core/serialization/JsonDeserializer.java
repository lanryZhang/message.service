/*
* JsonDeserializer.java 
* Created on  202016/12/21 14:38 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core.serialization;

import com.alibaba.fastjson.JSONObject;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class JsonDeserializer implements Deserializable{
    @Override
    public Object deserialize(Object obj) {
        if (null != obj){
            return JSONObject.parseObject(obj.toString());
        }
        return null;
    }

    @Override
    public <T> T deserialize(Object obj, Class<T> clazz) {
        if (null != obj){
            return JSONObject.parseObject(obj.toString(),clazz);
        }
        return null;
    }
}
