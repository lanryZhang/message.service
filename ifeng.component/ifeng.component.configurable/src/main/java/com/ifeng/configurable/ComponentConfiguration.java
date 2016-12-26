/*
* ComponentConfiguration.java 
* Created on  202016/12/9 14:05 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.configurable;

import com.ifeng.configurable.kafka.KafkaConfigConstances;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class ComponentConfiguration extends Configuration{
    protected Properties properties = new Properties();
    private Logger logger = Logger.getLogger(ComponentConfiguration.class);
    private String configPath;
    private String rootSection = "";
    public ComponentConfiguration(String path){
        this.configPath = path;
        analysisProperties();
    }

    public List<String> getAllInstances(){
        if (!rootSection.isEmpty()){
            Object obj = properties.get(rootSection);
            if (obj != null){
                return Arrays.asList(obj.toString().split(",|\\s+"));
            }
        }
        return null;
    }

    @Override
    protected void analysisProperties() {
        try {
            InputStream inputStream = new FileInputStream(new File(configPath));// ComponentConfiguration.class.getClassLoader().getResourceAsStream(configPath); // new BufferedInputStream(fileInputStream);
            properties.load(inputStream);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public HashMap<String,Context> load(){
        Iterator<Map.Entry<Object,Object>> iterator = properties.entrySet().iterator();
        HashMap<String,Context> map = new HashMap<>();
        while (iterator.hasNext()){
            Map.Entry en = iterator.next();
            if (rootSection.isEmpty() || en.getKey().toString().length() < rootSection.length()){
                rootSection = en.getKey().toString();
            }
        }
        String root = rootSection+".";
        iterator = properties.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry en = iterator.next();
            String key = en.getKey().toString();
            if (key.length() < root.length()){
                continue;
            }
            key = key.substring(root.length(),key.length());

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
