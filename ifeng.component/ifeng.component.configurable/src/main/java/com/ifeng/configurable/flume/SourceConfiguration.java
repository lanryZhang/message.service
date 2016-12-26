package com.ifeng.configurable.flume;

import com.ifeng.configurable.ComponentConfiguration;
import com.ifeng.configurable.Context;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by zhanglr on 2016/9/26.
 */
public class SourceConfiguration extends ComponentConfiguration {
    private Logger logger = Logger.getLogger(SourceConfiguration.class);

    public SourceConfiguration(String path ){
        super(path);
    }

    public List<String> getSources(){
        return Arrays.asList(properties.get("collector.sources").toString().split("\\s+"));
    }

    @Override
    public HashMap<String,Context> load(){
        Iterator<Map.Entry<Object,Object>> iterator = properties.entrySet().iterator();
        HashMap<String,Context> map = new HashMap<>();
        while (iterator.hasNext()){
            Map.Entry en = iterator.next();
            String key = en.getKey().toString();
            if (key.length() < "collector.sources.".length()){
                continue;
            }
            key = key.replace("collector.sources.","");
            key = key.substring(0,key.lastIndexOf("."));
            Context context = map.get(key);
            if (context == null) {
                context = new Context();
                map.put(key,context);
            }
            key = en.getKey().toString();
            context.put(key.substring(key.lastIndexOf(".") + 1,key.length()),en.getValue());
        }
        return map;
    }
}