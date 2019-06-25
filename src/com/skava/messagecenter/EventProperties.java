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

@Data
public class EventProperties {
  private boolean enabled;
  private String className;
  private int concurrentConsumers;
}
