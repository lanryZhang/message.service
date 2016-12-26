/*
* ApplicationContextLoader.java 
* Created on  202016/12/15 13:54 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core;

import com.ifeng.core.distribute.annotions.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class ApplicationContextLoader implements ContextLoader {

    private static final Logger logger = Logger.getLogger(ApplicationContextLoader.class);
    @Override
    public ApplicationContext load(String classPath) {
        return load( classPath, null);
    }

    @Override
    public ApplicationContext load(String classPath, String regx) {
        Pattern pattern = Pattern.compile("\\.*");
        ApplicationContext context = new ApplicationContext();
        if (null != regx){
            pattern = Pattern.compile(regx);
        }

        JarInputStream jarInputStream = null;
        try {
            jarInputStream = new JarInputStream(new FileInputStream(classPath));
            JarEntry en = jarInputStream.getNextJarEntry();
            while(en != null){
                String name = en.getName();
                Matcher m = pattern.matcher(en.getName());
                if (m.find() && isClassFile(name)) {
                    String className = name.replaceAll("/|\\\\",".");
                    className = className.replace(".class","");
                    Class clazz = Class.forName(className);

                    createApplicationContext(clazz,context);
                }
                en = jarInputStream.getNextJarEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (jarInputStream!= null){
                try {
                    jarInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return context;
    }

    private void createApplicationContext(Class<? extends MessageProcessor> clazz, ApplicationContext context) {

        if (null == clazz) {
            throw new NullPointerException("clazz can not be null.");
        }
        try {
            if (clazz.isInterface()){
                return;
            }
            Annotation[] ans = clazz.getDeclaredAnnotations();
            if (null != ans && ans.length > 0){
                for(Annotation an : ans){
                    if (an instanceof WebController){
                        parseWebController((WebController) an,clazz,context);
                    }else if (an instanceof KafkaController){
                        parseKafkaController((KafkaController)an ,clazz,context);
                    }
                }
            }

        } catch (Exception e) {
            logger.error(e);
        }
    }
    private void parseKafkaController(KafkaController an, Class<? extends MessageProcessor> clazz, ApplicationContext context){
        MessageProcessor processor;

        StringBuilder sb = new StringBuilder();
        if (null != an) {
            try {
                sb.append(an.value());
                processor = clazz.newInstance();

                Method[] ms = clazz.getDeclaredMethods();
                if (null != ms) {
                    for (int i = 0; i < ms.length; i++) {
                        RecordMapping rm = ms[i].getAnnotation(RecordMapping.class);

                        if (null != rm) {
                            String key = rm.topic();
                            context.getMapper().registHandler(key,processor);
                        }
                    }
                }
            }catch (InstantiationException e) {
                logger.error(e);
            } catch (IllegalAccessException e) {
                logger.error(e);
            }catch (Exception e){

            }
        }
    }
    private void parseWebController(WebController an, Class<? extends MessageProcessor> clazz, ApplicationContext context){
        MessageProcessor processor;

        StringBuilder sb = new StringBuilder();
        if (null != an) {
            try {
                sb.append(an.value());
                processor = clazz.newInstance();

                Method[] ms = clazz.getDeclaredMethods();
                if (null != ms) {
                    for (int i = 0; i < ms.length; i++) {
                        RequestMapping rm = ms[i].getAnnotation(RequestMapping.class);
                        if (null != rm) {
                            sb.append("/").append(rm.value());
                            RequestMethod[] requestMethod = rm.method();
                            RequestMethod[] arr;
                            if (requestMethod.length == 0) {
                                arr = RequestMethod.values();
                            } else {
                                arr = rm.method();
                            }

                            for (RequestMethod s : arr) {
                                context.getMapper().registHandler(StringUtils.strip(sb.toString() + "/$" + s, "/"), processor);
                            }
                        }
                    }
                }
            }catch (InstantiationException e) {
                logger.error(e);
            } catch (IllegalAccessException e) {
                logger.error(e);
            }catch (Exception e){

            }
        }
    }
    private boolean isClassFile(String name) {
        return name.endsWith(".class");
    }

}
