package com.seater.smartmining.mqtt;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

import java.util.UUID;

@Configuration
public class Config {

    @Value("${seater.mqtt.address}")
    private String address = "";

    @Autowired
    DeviceMessageHandler deviceMessageHandler;

    //private String serverTopic = "";


    @Bean
    public MqttPahoClientFactory mqttClientFactory()
    {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setServerURIs(new String[]{address});
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setKeepAliveInterval(10);
        mqttConnectOptions.setConnectionTimeout(60);
        factory.setConnectionOptions(mqttConnectOptions);

        return factory;
    }

    @Bean
    public MessageChannel mqttOutboundChannel()
    {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MqttPahoMessageHandler mqttOutbound()
    {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("smartminingSenderNew", mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultQos(2);
        messageHandler.setDefaultTopic("testTopic");
        return messageHandler;
    }

    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface MyGateway{
        public void sendToMqtt(String data);
    }

/*    @Bean
    public MessageProducerSupport mqttInbound()
    {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("escondidaServer" + UUID.randomUUID().toString(),
                mqttClientFactory(), serverTopic);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        return adapter;
    }*/

/*    @Bean
    public MessageProducerSupport mqttClientInbound()
    {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("escondidaOnlineCheck" + UUID.randomUUID().toString(),
                mqttClientFactory(), "$SYS/brokers/+/clients/#");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        return adapter;
    }*/

    @Bean
    public MessageProducerSupport deviceInbound()
    {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("detectorDeviceRequest" + UUID.randomUUID().toString(),
                mqttClientFactory(), "smartmining/+/device/+/+", "smartmining/device/+/+/#", "smartmining/app/+/+/#");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        return adapter;
    }

    @Bean
    public IntegrationFlow deviceInFlow()
    {
        return IntegrationFlows.from(deviceInbound())
                .handle(deviceMessageHandler)
                .get();
    }
}
