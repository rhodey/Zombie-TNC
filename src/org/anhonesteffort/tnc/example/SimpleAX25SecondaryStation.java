package org.anhonesteffort.tnc.example;

import jssc.SerialPortException;
import org.anhonesteffort.tnc.ax25.AX25Frame;
import org.anhonesteffort.tnc.ax25.AX25Operator;
import org.anhonesteffort.tnc.ax25.AX25Protocol;
import org.anhonesteffort.tnc.ax25.AX25SecondaryStation;

public class SimpleAX25SecondaryStation implements AX25SecondaryStation {

  private AX25Operator operator;
  private int ssid;

  public SimpleAX25SecondaryStation(int ssid) {
    this.ssid = ssid;
  }

  @Override
  public void setOperator(AX25Operator operator) {
    this.operator = operator;
  }

  @Override
  public int getSSID() {
    return ssid;
  }

  @Override
  public void onAX25FrameReceived(AX25Frame myFrame) {
    System.out.println("Secondary Station " + Integer.toHexString(ssid & 0x000000FF) + " received AX25 frame: ");
    System.out.println("source address: " + new String(myFrame.getSourceAddress()));
    System.out.println("source ssid: " + myFrame.getSourceSSID());
    System.out.println("control field: " + Integer.toHexString(myFrame.getControlField() & 0x000000FF));
    System.out.println("pid field: " + Integer.toHexString(myFrame.getPIDField() & 0x000000FF));
    System.out.println("information field: " + new String(myFrame.getInfoField()));
  }

  public void sendUnnumberedInformation(byte[] destination_address, byte[] information) {
    try {
      operator.sendUnnumberedInformation(ssid, destination_address, AX25Protocol.SSID_DEFAULT, information);
    } catch (SerialPortException e) {
      System.out.println("Serial port error! " + e);
    }
  }

  public void sendUnnumberedInformation(byte[] destination_address, int destination_ssid, byte[] information) {
    try {
      operator.sendUnnumberedInformation(ssid, destination_address, destination_ssid, information);
    } catch (SerialPortException e) {
      System.out.println("Serial port error! " + e);
    }
  }

}
