/*
* RequestMapping.java 
* Created on  202016/12/15 11:27 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core.distribute.annotions;

import java.lang.annotation.*;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String value();
    RequestMethod[] method() default {};
}
