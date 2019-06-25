/*******************************************************************************
 * Copyright ©2002-2019 Skava - All rights reserved.
 * All information contained herein is, and remains the property of Skava.
 * Skava including, without limitation, all software and other elements thereof, 
 * are owned or controlled exclusively by Skava and protected by copyright, patent
 * and other laws. Use without permission is prohibited. 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * 
 * For further information contact Skava at info@skava.com.
 ******************************************************************************/
package com.skava.messagecenter;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * RabbitMQProperties class.
 * </p>
 *
 * @author Srinivasa.Rao09
 * @version $Id: $Id
 */
@Data
@NoArgsConstructor
public class RabbitMQProperties {
  /**
   * To enable or disable the rabbitmq component.
   */
  private boolean enabled;

  private String userName;

  private String password;

  private String host;

  private String virtualHost;

  private int port;

  private String queueName;

  private String topicExchange;

  private boolean embedded;
  
  private String routingKey;

  private int concurrentConsumers;
  private int receiveTimeout = 5000;
  private int replyTimeout = 0;
  private boolean clusterEnabled;
  private String addresses;
  
  private boolean ssl;
  private String algorithm;
  private String keyStore;
  private String keyStorePassword;
  private String trustStore;
  private String trustStorePassword;
  
}
