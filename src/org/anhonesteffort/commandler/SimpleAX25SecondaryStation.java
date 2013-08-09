package org.anhonesteffort.commandler;

import jssc.SerialPortException;
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
    public void onFrameReceived(byte[] source_address, int source_ssid, byte control_field, byte pid_field, byte[] info_field) {
        System.out.println("Secondary Station " + Integer.toHexString(ssid & 0x000000FF) + " received AX25 frame: ");
        System.out.println("source address: " + new String(source_address));
        System.out.println("source ssid: " + source_ssid);
        System.out.println("control field: " + Integer.toHexString(control_field & 0x000000FF));
        System.out.println("pid field: " + Integer.toHexString(pid_field & 0x000000FF));
        System.out.println("information field: " + new String(info_field));
    }

    public void sendUnnumberedInformation(byte[] destination_address, byte[] information) throws SerialPortException {
        operator.sendUnnumberedInformation(ssid, destination_address, AX25Protocol.SSID_DEFAULT_DESTINATION, information);
    }

    public void sendUnnumberedInformation(byte[] destination_address, int destination_ssid, byte[] information) throws SerialPortException {
        operator.sendUnnumberedInformation(ssid, destination_address, destination_ssid, information);
    }

}
