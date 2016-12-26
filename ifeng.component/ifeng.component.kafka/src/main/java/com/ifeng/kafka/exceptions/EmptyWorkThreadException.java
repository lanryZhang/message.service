/*
* EmptyWorkThreadException.java 
* Created on  202016/12/19 17:00 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.kafka.exceptions;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class EmptyWorkThreadException extends Exception {
    public EmptyWorkThreadException(){

    }

    public EmptyWorkThreadException(String message){
        super(message);
    }
}
