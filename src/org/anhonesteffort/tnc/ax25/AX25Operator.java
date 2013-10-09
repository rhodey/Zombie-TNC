package org.anhonesteffort.tnc.ax25;

import jssc.SerialPortException;

import java.util.LinkedList;
import java.util.List;

public class AX25Operator {

  private AX25Port ax25Port;
  private byte[] call_sign;
  private List<AX25SecondaryStation> secondaryStations;

  public AX25Operator(AX25Port ax25Port, byte[] call_sign) {
    this.ax25Port = ax25Port;
    this.call_sign = AX25Protocol.padAddress(call_sign);
    secondaryStations = new LinkedList<AX25SecondaryStation>();

    ax25Port.addOperator(this);
  }

  public byte[] getCallSign() {
    return call_sign;
  }

  public void onMyFrameReceived(AX25Frame myFrame) {
    for (AX25SecondaryStation station : secondaryStations) {
      if (myFrame.getDestinationSSID() == station.getSSID())
        station.onAX25FrameReceived(myFrame);
    }
  }

  public void addSecondaryStation(AX25SecondaryStation station) {
    station.setOperator(this);
    secondaryStations.add(station);
  }

  public void removeSecondaryStation(AX25SecondaryStation station) {
    secondaryStations.remove(station);
  }

  public void sendUnnumberedInformation(byte[] destination_address, byte[] information) throws SerialPortException {
    AX25Frame txFrame = new AX25Frame(call_sign, destination_address);
    txFrame.setCommandResponseType(AX25Protocol.CommandResponseType.COMMAND);
    txFrame.setControlField(AX25Protocol.CONTROL_UIFRAME_FINAL);
    txFrame.setPIDField(AX25Protocol.PID_NO_LAYER_3_PROTOCOL);
    txFrame.setInfoField(information);
    ax25Port.sendFrame(txFrame);
  }

  public void sendUnnumberedInformation(int source_ssid, byte[] destination_address, int destination_ssid, byte[] information) throws SerialPortException {
    AX25Frame txFrame = new AX25Frame(call_sign, source_ssid, destination_address, destination_ssid);
    txFrame.setCommandResponseType(AX25Protocol.CommandResponseType.COMMAND);
    txFrame.setControlField(AX25Protocol.CONTROL_UIFRAME_FINAL);
    txFrame.setPIDField(AX25Protocol.PID_NO_LAYER_3_PROTOCOL);
    txFrame.setInfoField(information);
    ax25Port.sendFrame(txFrame);
  }

  public void sendFrame(AX25Frame frame) throws SerialPortException {
    ax25Port.sendFrame(frame);
  }

  public void quit() {
    ax25Port.removeOperator(this);
  }

}
