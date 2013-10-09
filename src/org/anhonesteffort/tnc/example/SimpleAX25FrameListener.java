package org.anhonesteffort.tnc.example;

import org.anhonesteffort.tnc.ax25.AX25Frame;
import org.anhonesteffort.tnc.ax25.AX25FrameListener;

public class SimpleAX25FrameListener implements AX25FrameListener {

  @Override
  public void onAX25FrameReceived(AX25Frame myFrame) {
    System.out.println("AX25 frame received: ");
    System.out.println("destination address: " + new String(myFrame.getDestinationAddress()));
    System.out.println("destination ssid: " + myFrame.getDestinationSSID());
    System.out.println("source address: " + new String(myFrame.getSourceAddress()));
    System.out.println("source ssid: " + myFrame.getSourceSSID());
    System.out.println("control field: " + Integer.toHexString(myFrame.getControlField() & 0x000000FF));
    System.out.println("pid field: " + Integer.toHexString(myFrame.getPIDField() & 0x000000FF));
    System.out.println("information field: " + new String(myFrame.getInfoField()));
  }

}
