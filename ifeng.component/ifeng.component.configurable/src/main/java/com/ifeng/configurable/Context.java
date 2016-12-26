package com.ifeng.configurable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhanglr on 2016/6/27.
 */
public class Context {
    private HashMap<String,Object> context;

    public Context(){
        context = new HashMap<String,Object>();
    }
    public void put(String key,Object value){
         context.put(key,value);
    }
    public void putAll(Map<String,Object> m){
        context.putAll(m);
    }
    public void putAll(Context c){
        context.putAll(c.getContext());
    }
    private String get(String key) {
        return this.get(key, null);
    }

    public Long getLong(String key, Long defaultValue) {
        String value = this.get(key);
        return value != null?Long.valueOf(Long.parseLong(value.trim())):defaultValue;
    }

    public Integer getInt(String key, Integer defaultValue) {
        String value = this.get(key);
        return value != null?Integer.valueOf(Integer.parseInt(value.trim())):defaultValue;
    }

    public Integer getInt(String key) {
        return this.getInt(key, null);
    }
    public Long getLong(String key) {
        return this.getLong(key, null);
    }
    public String getString(String key, String defaultValue) {
        return this.get(key, defaultValue);
    }
    public Boolean getBoolean(String key,String defaultValue){return Boolean.valueOf(this.get(key, defaultValue));}
    public String getString(String key) {
        return this.get(key);
    }
    public Object getObject(String key){
        return context.get(key);
    }
    private String get(String key, String defaultValue) {
        String result = (String)this.context.get(key);
        return result != null?result:defaultValue;
    }

    public Map<String,Object> getContext(){
        return this.context;
    }
}
