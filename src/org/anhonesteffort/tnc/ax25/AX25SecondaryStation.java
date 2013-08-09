package org.anhonesteffort.tnc.ax25;

public interface AX25SecondaryStation {

    public void setOperator(AX25Operator operator);

    public int getSSID();

    public void onFrameReceived(byte[] source_address, int source_ssid, byte control_field, byte pid_field, byte[] info_field);

}
