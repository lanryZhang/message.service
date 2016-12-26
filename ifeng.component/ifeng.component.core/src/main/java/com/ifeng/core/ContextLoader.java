/*
* ContextLoader.java 
* Created on  202016/12/15 13:55 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core;

import com.ifeng.core.ApplicationContext;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public interface ContextLoader {
    ApplicationContext load(String classPath);
    ApplicationContext load(String classPath,String regx);

}
