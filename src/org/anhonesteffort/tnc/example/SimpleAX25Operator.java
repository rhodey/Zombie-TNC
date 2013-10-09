package org.anhonesteffort.tnc.example;

import org.anhonesteffort.tnc.ax25.AX25Frame;
import org.anhonesteffort.tnc.ax25.AX25Operator;
import org.anhonesteffort.tnc.ax25.AX25Port;

public class SimpleAX25Operator extends AX25Operator {

  public SimpleAX25Operator(AX25Port ax25Port, byte[] call_sign) {
    super(ax25Port, call_sign); // Always call the Superclass method!
  }

  @Override
  public void onMyFrameReceived(AX25Frame myFrame) {
    super.onMyFrameReceived(myFrame); // Always call the Superclass method!

    System.out.println("Operator " + new String(super.getCallSign()) + " received AX25 frame: ");
    System.out.println("destination ssid: " + myFrame.getDestinationSSID());
    System.out.println("source address: " + new String(myFrame.getSourceAddress()));
    System.out.println("source ssid: " + myFrame.getSourceSSID());
    System.out.println("control field: " + Integer.toHexString(myFrame.getControlField() & 0x000000FF));
    System.out.println("pid field: " + Integer.toHexString(myFrame.getPIDField() & 0x000000FF));
    System.out.println("information field: " + new String(myFrame.getInfoField()));
  }

}
