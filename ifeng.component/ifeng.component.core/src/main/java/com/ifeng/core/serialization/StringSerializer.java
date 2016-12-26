/*
* StringSerializer.java 
* Created on  202016/12/22 14:44 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core.serialization;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class StringSerializer implements Serializable {
    @Override
    public Object serialize(Object obj) {
        return String.valueOf(obj);
    }
}
