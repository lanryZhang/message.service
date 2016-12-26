/*
* ClassDescriptorContainer.java 
* Created on  202016/10/21 12:52 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core.data;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class ClassDescriptorContainer{
    private static ConcurrentHashMap<String,Field[]> fieldsMap = new ConcurrentHashMap<>();
    public static Field[] getFields(Class<?> clazz){
        if (clazz != null){
            if (!fieldsMap.containsKey(clazz.getName())){
                Field[] fields = clazz.getDeclaredFields();
                AccessibleObject.setAccessible(fields,true);
                fieldsMap.put(clazz.getName(),fields);
            }
            return fieldsMap.get(clazz.getName());
        }else{
            throw new NullPointerException("参数不能为空，无法获取Null的类描述信息。");
        }
    }

    public static Field[] getFields(String className) throws ClassNotFoundException {
        Class<?> clazz ;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw e;
        }
        return getFields(clazz);
    }
}
