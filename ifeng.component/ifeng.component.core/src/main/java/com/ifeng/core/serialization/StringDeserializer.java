/*
* StringDeserializer.java 
* Created on  202016/12/22 14:42 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core.serialization;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class StringDeserializer implements Deserializable {
    @Override
    public Object deserialize(Object obj) {
        if (null != obj){
            return obj.toString();
        }
        return obj;
    }

    @Override
    public <T> T deserialize(Object obj, Class<T> clazz) {
        if (null != obj){
            return (T) obj.toString();
        }
        return null;
    }
}
