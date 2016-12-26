/*
* IHandler.java 
* Created on  202016/12/14 15:57 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core;

import com.ifeng.configurable.Context;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public interface MessageProcessor {
    Object process(Context context);
}
