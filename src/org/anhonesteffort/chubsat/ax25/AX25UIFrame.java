package org.anhonesteffort.chubsat.ax25;

import org.anhonesteffort.chubsat.StringUtils;
import org.anhonesteffort.chubsat.ax25.AX25Protocol;

public class AX25UIFrame {
    private byte[] source_address;
    private byte source_ssid;
    private byte[] destination_address;
    private byte destination_ssid;
    private byte[] address_field;

    private byte control_field = AX25Protocol.CONTROL_UI_FINAL;
    private byte pid_field = AX25Protocol.PID_NO_LAYER_3_PROTOCOL;

    private byte[] info_field = new byte[] {};

    public AX25UIFrame(byte[] source_address, byte[] destination_address) {
        this.source_address = source_address;
        this.source_ssid = AX25Protocol.SSID_COMMAND_SOURCE_DEFAULT;
        this.destination_address = destination_address;
        this.destination_ssid = AX25Protocol.SSID_COMMAND_DESTINATION_DEFAULT;
        address_field = AX25Protocol.createAddressField(source_address, destination_address);
    }

    public AX25UIFrame(byte[] source_address, byte source_ssid, byte[] destination_address, byte destination_ssid) {
        this.source_address = source_address;
        this.source_ssid = source_ssid;
        this.destination_address = destination_address;
        this.destination_ssid = destination_ssid;
        address_field = AX25Protocol.createAddressField(source_address, source_ssid, destination_address, destination_ssid);
    }

    public byte[] getSourceAddress() {
        return source_address;
    }

    public byte getSourceSSID() {
        return source_ssid;
    }

    public byte[] getDestinationAddress() {
        return destination_address;
    }

    public byte getDestinationSSID() {
        return destination_ssid;
    }

    public byte[] getAddressField() {
        return address_field;
    }

    public byte getControlField() {
        return control_field;
    }

    public void setControlField(byte control_field) {
        this.control_field = control_field;
    }

    public byte getPIDField() {
        return pid_field;
    }

    public void setPIDField(byte pid_field) {
        this.pid_field = pid_field;
    }

    public void setInfoField(byte[] info_field) {
        this.info_field = info_field;
    }

    public void setInfoField(char[] info_field) {
        this.info_field = new byte[info_field.length];
        for(int i = 0; i < info_field.length; i++)
            this.info_field[i] = (byte)info_field[i];
    }

    public byte[] getInfoField() {
        return info_field;
    }

    public byte[] toByteArray() {
        byte[] out = new byte[16 + info_field.length];

        for(int i = 0; i < address_field.length; i++)
            out[i] = address_field[i];

        out[14] = control_field;
        out[15] = pid_field;

        for(int i = 0; i < info_field.length; i++)
            out[i + 16] = info_field[i];

        return out;
    }

}
