package com.ifeng.core.distribute.message;

import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhanglr on 2016/8/28.
 */
public class SchemaGenerator<T> {
    private static ConcurrentHashMap<Class<?>,Schema<?>> schemaCache = new ConcurrentHashMap<>();

    public static <T> Schema<T> getSchema(Class<T> clazz){
        Schema<T> schema;
        if (schemaCache.containsKey(clazz)){
            schema = (Schema<T>) schemaCache.get(clazz);
        }else{
            schema = RuntimeSchema.getSchema(clazz);
            schemaCache.put(clazz,schema);
        }
        return schema;
    }
}
