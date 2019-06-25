package com.skava.messagecenter;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.annotation.PostConstruct;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

public class CustomMessageListner implements MessageListener {

  private Map<String, MessageListener> queueListeners = new HashMap<>();;

  private Map<String, SimpleMessageListenerContainer> queueContainers = new HashMap<>();;

  public static void main(String[] arg) {
    new CustomMessageListner().init();
  }

  @PostConstruct
  public void init() {

    RabbitMQProperties rabbitMQProperties = new RabbitMQProperties();

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
    String queueName = "CatalogTest";

    rabbitMQProperties.setUserName(userName);
    rabbitMQProperties.setPassword(passWord);
    rabbitMQProperties.setHost(host);
    rabbitMQProperties.setVirtualHost(vhost);
    rabbitMQProperties.setPort(port);
    rabbitMQProperties.setClusterEnabled(isClusterEnabled);
    rabbitMQProperties.setAddresses(addresses);
    rabbitMQProperties.setSsl(true);

    Scanner scan = new Scanner(System.in);

    ConnectionFactory connectionFactory = connectionFactory(rabbitMQProperties);

    AmqpAdmin amqpAdmin = getAmqpAdmin(connectionFactory);
    TopicExchange topicExchange = new TopicExchange(exchangeName, true, false);
    Queue queue = new Queue(queueName, true, false, false);

    amqpAdmin.declareExchange(topicExchange);
    amqpAdmin.declareQueue(queue);

    Binding binding = new Binding(queueName, DestinationType.QUEUE, exchangeName, routingKey, null);
    amqpAdmin.declareBinding(binding);
    EventProperties eventProperties = new EventProperties();
    listen(queue, message -> {
      if (null != message.getBody()) {
        catalogServiceProcess(message);
      }
    }, AcknowledgeMode.NONE, amqpAdmin, connectionFactory, eventProperties);
    System.out.println("Enter 1 to exit");
    int toRet = Integer.valueOf((scan.nextLine()));
    if (toRet == 1) {
      scan.close();
      System.exit(0);
    }
  }

  private void catalogServiceProcess(Message message) {
    System.out.println("Message Listen : " + new String(message.getBody()));
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
        com.rabbitmq.client.ConnectionFactory rabbitmqConnectionFactory = rcf.getObject();
        rabbitmqConnectionFactory.setHandshakeTimeout(60000);
        connectionFactory = new CachingConnectionFactory(rabbitmqConnectionFactory);
      } catch (Exception e) {
        System.out.println("Exception " + e);
      }
    }

    if (rabbitMQProperties.isClusterEnabled()) {
      String addresses = rabbitMQProperties.getAddresses();
      connectionFactory.setAddresses(addresses);
    } else {
      connectionFactory.setHost(rabbitMQProperties.getHost());
      connectionFactory.setPort(rabbitMQProperties.getPort());
    }

    connectionFactory.setConnectionTimeout(60000);
    connectionFactory.setRequestedHeartBeat(180);
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

  public boolean listen(Queue queue, MessageListener messageListener, AcknowledgeMode acknowledgeMode,
    AmqpAdmin amqpAdmin, ConnectionFactory connectionFactory, EventProperties eventProperties) {
    String queueName = queue.getName();
    if (messageListener == null || queueName == null || queueName.trim().isEmpty()) {
      return false;
    }
    if (acknowledgeMode == null) {
      acknowledgeMode = AcknowledgeMode.AUTO;
    }

    MessageListener listeners = this.queueListeners.get(queueName);
    if (null == listeners) {
      synchronized (this) {
        listeners = this.queueListeners.get(queueName);
        if (null == listeners) {
          listeners = messageListener;
          this.queueListeners.put(queueName, listeners);
          amqpAdmin.declareQueue(queue);

          SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
          simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
          simpleMessageListenerContainer.setQueueNames(queueName);
          simpleMessageListenerContainer.setAcknowledgeMode(acknowledgeMode);
          if (eventProperties.getConcurrentConsumers() > 0) {
            simpleMessageListenerContainer
              .setConcurrentConsumers(eventProperties.getConcurrentConsumers());
          }
          simpleMessageListenerContainer.setMessageListener(message -> {
            MessageListener listeners1 = queueListeners.get(queueName);
            if (null != listeners1) {
              listeners1.onMessage(message);
            }
          });
          simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
          simpleMessageListenerContainer.start();
          queueContainers.put(queueName, simpleMessageListenerContainer);
        }
      }
    }
    return true;
  }

  @Override
  public void onMessage(Message arg0) {
  }
}
