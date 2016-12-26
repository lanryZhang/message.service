/*
* KafkaConfiguration.java 
* Created on  202016/12/9 14:02 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.configurable.kafka;

import com.ifeng.configurable.ComponentConfiguration;
import com.ifeng.configurable.Context;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class KafkaConfiguration extends ComponentConfiguration {

    public KafkaConfiguration(String path) {
        super(path);
    }

    @Override
    public HashMap<String, Context> load() {
        Iterator<Map.Entry<Object,Object>> iterator = properties.entrySet().iterator();
        HashMap<String,Context> map = new HashMap<>();
        while (iterator.hasNext()){
            Map.Entry en = iterator.next();
            String key = en.getKey().toString();
            if (key.equals(KafkaConfigConstances.KAFKA_CONFIG_PREFIX)){
                continue;
            }

            key = key.substring(KafkaConfigConstances.KAFKA_CONFIG_PREFIX.length()+1,key.length());

            String instanceName = key.substring(0,key.indexOf("."));
            Context context = map.get(instanceName);
            if (context == null) {
                context = new Context();
                map.put(instanceName,context);
            }
            String innerKey = key.substring(key.indexOf(".")+1,key.length());

            context.put(innerKey,en.getValue());
        }
        return map;
    }
}