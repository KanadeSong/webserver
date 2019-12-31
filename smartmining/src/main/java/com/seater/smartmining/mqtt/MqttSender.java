package com.seater.smartmining.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.helpers.JsonHelper;
import org.omg.CORBA.StringHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class MqttSender {
    @Autowired
    AbstractApplicationContext applicationContext;

    private MqttPahoMessageHandler messageHandler;

    private MqttPahoMessageHandler getMessageHandler() {
        if (messageHandler == null) messageHandler = applicationContext.getBean(MqttPahoMessageHandler.class);
        return messageHandler;
    }

    public void sendDeviceReply(String topic, Object plyload) throws JsonProcessingException {
        Message<String> message = MessageBuilder.withPayload(JsonHelper.toJsonString(plyload)).setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MqttHeaders.QOS, 2).build();
        getMessageHandler().handleMessage(message);
    }
}
