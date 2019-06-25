/*******************************************************************************
 * Copyright ©2002-2014 Skava.
 * All rights reserved.The Skava system, including
 * without limitation, all software and other elements
 * thereof, are owned or controlled exclusively by
 * Skava and protected by copyright, patent, and
 * other laws. Use without permission is prohibited.
 *
 * For further information contact Skava at info@skava.com.
 ******************************************************************************/
package com.skava.messagecenter;

import java.util.Scanner;

public class EventApplication {
  public static void main(String[] arg) {
    //Scanner scan = new Scanner(System.in);
    //System.out.println("Enter 1 for publish or 2 for listener : ");
    //int toRet = Integer.valueOf((scan.nextLine()));
    int toRet = 1;
    long time = System.currentTimeMillis();
    if (toRet == 1) {
      MessageSender messageSender = new MessageSender();
      messageSender.send();
    } else {
      CustomMessageListner customMessageListner = new CustomMessageListner();
      customMessageListner.init();
    }
    System.out.println("Time Taken In Sec : " + (System.currentTimeMillis() - time)/1000 );
    //scan.close();
  }
}
