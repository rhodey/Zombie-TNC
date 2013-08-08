package org.anhonesteffort.tnc.ax25;

import jssc.SerialPortException;

public class AX25Operator {
    AX25Port ax25Port;
    private byte[] call_sign;

    public AX25Operator(AX25Port ax25Port, byte[] call_sign) {
        this.ax25Port = ax25Port;
        this.call_sign = AX25Protocol.padAddress(call_sign);
        ax25Port.addOperator(this);
    }

    public byte[] getCallSign() {
        return call_sign;
    }

    public void onFrameReceived(byte[] source_address, byte source_ssid, byte control_field, byte pid_field, byte[] info_field) {
        System.out.println("received AX25 frame for me! ");
        System.out.println("source address: " + new String(source_address));
        System.out.println("source ssid: " + Integer.toHexString(source_ssid & 0x000000FF));
        System.out.println("control field: " + Integer.toHexString(control_field & 0x000000FF));
        System.out.println("pid field: " + Integer.toHexString(pid_field & 0x000000FF));
        System.out.println("information field: " + new String(info_field));
    }

    public void sendUnnumberedInformation(byte[] destination_address, byte[] information) throws SerialPortException {
        AX25UIFrame frame = new AX25UIFrame(call_sign, AX25Protocol.padAddress(destination_address));
        frame.setInfoField(information);
        ax25Port.sendFrame(frame);
    }

    public void close() {
        ax25Port.removeOperator(this);
    }

}
