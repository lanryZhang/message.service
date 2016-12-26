package com.ifeng.core.data;

/**
 * Created by zhanglr on 2016/10/9.
 */
public interface ILoader {
    int getInt(String key, int defaultValue);
    int getInt(String key);
    String getString(String key);
    Long getLong(String key);
    ILoader getLoader(String key) throws Exception;

    Object getObject(String key);
}
