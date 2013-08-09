package org.anhonesteffort.tnc.ax25;

import jssc.SerialPortException;

import java.util.ArrayList;

public class AX25Operator {
    AX25Port ax25Port;
    private byte[] call_sign;
    private ArrayList<AX25SecondaryStation> secondaryStations;

    public AX25Operator(AX25Port ax25Port, byte[] call_sign) {
        this.ax25Port = ax25Port;
        this.call_sign = AX25Protocol.padAddress(call_sign);
        secondaryStations = new ArrayList<AX25SecondaryStation>();
        ax25Port.addOperator(this);
    }

    public byte[] getCallSign() {
        return call_sign;
    }

    public void onFrameReceived(int destination_ssid, byte[] source_address, int source_ssid, byte control_field, byte pid_field, byte[] info_field) {
        System.out.println("Operator " + new String(call_sign) + " received AX25 frame: ");
        System.out.println("destination ssid: " + destination_ssid);
        System.out.println("source address: " + new String(source_address));
        System.out.println("source ssid: " + source_ssid);
        System.out.println("control field: " + Integer.toHexString(control_field & 0x000000FF));
        System.out.println("pid field: " + Integer.toHexString(pid_field & 0x000000FF));
        System.out.println("information field: " + new String(info_field));

        for(AX25SecondaryStation station : secondaryStations) {
            if(destination_ssid == station.getSSID())
                station.onFrameReceived(source_address, source_ssid, control_field, pid_field, info_field);
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
        AX25UIFrame frame = new AX25UIFrame(call_sign, AX25Protocol.padAddress(destination_address));
        frame.setInfoField(information);
        ax25Port.sendFrame(frame);
    }

    public void sendUnnumberedInformation(int source_ssid, byte[] destination_address, int destination_ssid, byte[] information) throws SerialPortException {
        AX25UIFrame frame = new AX25UIFrame(call_sign, AX25Protocol.createSSID(source_ssid), AX25Protocol.padAddress(destination_address), AX25Protocol.createSSID(destination_ssid));
        frame.setInfoField(information);
        ax25Port.sendFrame(frame);
    }

    public void close() {
        ax25Port.removeOperator(this);
    }

}
