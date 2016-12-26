/*
* RequestMethod.java 
* Created on  202016/12/15 11:39 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core.distribute.annotions;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public enum RequestMethod {
    GET,
    HEAD,
    POST,
    PUT,
    PATCH,
    DELETE,
    OPTIONS,
    TRACE;

    RequestMethod() {
    }
}
