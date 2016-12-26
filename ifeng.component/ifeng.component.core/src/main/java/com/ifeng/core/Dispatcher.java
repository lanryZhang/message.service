/*
* RequestDispatcher.java 
* Created on  202016/12/15 13:44 
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
public interface Dispatcher {
    Object dispatch(Context Context);
}
