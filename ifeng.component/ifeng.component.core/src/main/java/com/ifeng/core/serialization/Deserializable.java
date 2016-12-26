/*
* Deserializable.java 
* Created on  202016/12/21 14:39 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core.serialization;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public interface Deserializable {
    Object deserialize(Object obj);
    <T> T deserialize(Object obj,Class<T> clazz);
}
