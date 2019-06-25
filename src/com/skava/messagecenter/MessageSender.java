package com.skava.messagecenter;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class MessageSender {

  public static void main(String[] arg) {
    new MessageSender().send();
  }

  public void send() {

    com.skava.messagecenter.Message<Object> catalogMessage = new com.skava.messagecenter.Message<Object>()
      .setMessageType("publish").setPayload("Sample message");

    // Stage
    boolean isClusterEnabled = true;
    String addresses = "testvhost.skavacommerce.com:5672";
    String userName = "test123";
    String passWord = "test123";
    String host = "testvhost.skavacommerce.com";
    String vhost = "test123";
    int port = 5672;
    String routingKey = "catalog.project.updated1";
    String exchangeName = "ecomm";

    RabbitMQProperties rabbitMQProperties = new RabbitMQProperties();
    rabbitMQProperties.setUserName(userName);
    rabbitMQProperties.setPassword(passWord);
    rabbitMQProperties.setVirtualHost(vhost);

    rabbitMQProperties.setHost(host);
    rabbitMQProperties.setPort(port);
    rabbitMQProperties.setClusterEnabled(isClusterEnabled);
    rabbitMQProperties.setAddresses(addresses);
    rabbitMQProperties.setSsl(true);

    ConnectionFactory connectionFactory = connectionFactory(rabbitMQProperties);
    AmqpAdmin amqpAdmin = getAmqpAdmin(connectionFactory);
    AmqpTemplate amqpTemplate = rabbitTemplate(connectionFactory, rabbitMQProperties);
    if (amqpTemplate != null) {
      TopicExchange topicExchange = new TopicExchange(exchangeName, true, false);
      amqpAdmin.declareExchange(topicExchange);
      MessageProperties messageProperties = getMessageProperties(null);
      messageProperties.setReplyTo("catalog.project.updated.callback");
      messageProperties.setHeader("business-id", 1);
      messageProperties.setHeader("x-collection-id", 1);
      Message message = new Message(catalogMessage.toString().getBytes(StandardCharsets.UTF_8),
        messageProperties);

      Scanner scan = new Scanner(System.in);
      boolean push = true;
      while (push) {
        System.out.println("Pushing the message");
        amqpTemplate.send("ecomm", routingKey, message);
        System.out.println("Enter 1 to push again or 2 to exit:");
        int toRet = Integer.valueOf((scan.nextLine()));
        if (toRet == 2) {
          push = false;
          scan.close();
          System.exit(0);
        } else if (toRet == 1) {
          continue;
        }
      }
      System.out.println("Done");
    }
  }

  private static MessageProperties getMessageProperties(Map<String, Object> messageHeader) {
    MessageProperties messageProperties = new MessageProperties();
    messageProperties.setContentType("text/plain");
    if (messageHeader != null) {
      messageHeader.entrySet().forEach(map -> messageProperties.setHeader(map.getKey(), map.getValue()));
    }
    return messageProperties;
  }

  public ConnectionFactory connectionFactory(RabbitMQProperties rabbitMQProperties) {
    RabbitConnectionFactoryBean rcf = new RabbitConnectionFactoryBean();
    CachingConnectionFactory connectionFactory = null;
    if (rabbitMQProperties.isSsl()) {
      rcf.setUseSSL(true);
      rcf.setSslAlgorithm("TLSv1.2");
      rcf.setAutomaticRecoveryEnabled(true);
      rcf.afterPropertiesSet();

      try {
        connectionFactory = new CachingConnectionFactory(rcf.getObject());
      } catch (Exception e) {
        System.out.println("Exception " + e);
      }
    } else {
      connectionFactory = new CachingConnectionFactory();
    }

    if (rabbitMQProperties.isClusterEnabled()) {
      connectionFactory.setAddresses(rabbitMQProperties.getAddresses());
    } else {
      connectionFactory.setHost(rabbitMQProperties.getHost());
      connectionFactory.setPort(rabbitMQProperties.getPort());
    }

    connectionFactory.setUsername(rabbitMQProperties.getUserName());
    connectionFactory.setPassword(rabbitMQProperties.getPassword());
    connectionFactory.setVirtualHost(rabbitMQProperties.getVirtualHost());
    return connectionFactory;
  }

  public AmqpAdmin getAmqpAdmin(ConnectionFactory connectionFactory) {
    return new RabbitAdmin(connectionFactory);
  }

  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, RabbitMQProperties rabbitMQProperties) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setReceiveTimeout(rabbitMQProperties.getReceiveTimeout());
    template.setReplyTimeout(rabbitMQProperties.getReplyTimeout());
    return template;
  }
}
