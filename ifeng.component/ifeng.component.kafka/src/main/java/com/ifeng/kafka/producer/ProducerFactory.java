/*
* Producer.java 
* Created on  202016/11/4 17:48 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.kafka.producer;

import com.ifeng.configurable.Context;
import com.ifeng.configurable.kafka.KafkaConfiguration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import java.util.Map;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class ProducerFactory {
    public static Producer getInstnace(String dir){
        KafkaConfiguration kafkaConfiguration = new KafkaConfiguration(dir+"/kafka.properties");
        Map<String,Context> conf = kafkaConfiguration.load();
        return new KafkaProducer<>(conf.get("kafkaProducer").getContext());
    }

    public static KafkaProducerEx getBacthInstnace(String dir){
        KafkaConfiguration kafkaConfiguration = new KafkaConfiguration(dir+"/kafka.properties");
        Map<String,Context> conf = kafkaConfiguration.load();
        return new KafkaProducerEx(conf.get("kafkaProducer").getContext());
    }

    public static KafkaProducerEx getBacthInstnace(String dir,String topic){
        KafkaConfiguration kafkaConfiguration = new KafkaConfiguration(dir+"/kafka.properties");
        Map<String,Context> conf = kafkaConfiguration.load();
        return new KafkaProducerEx(conf.get("kafkaProducer").getContext(),topic);
    }
}
