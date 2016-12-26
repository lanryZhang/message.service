/*
* ModelAttribute.java 
* Created on  202016/12/18 11:40 
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
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModelAttribute {
    String value() default "";
}
