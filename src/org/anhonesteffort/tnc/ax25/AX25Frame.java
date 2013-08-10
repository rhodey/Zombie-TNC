package org.anhonesteffort.tnc.ax25;

public class AX25Frame {
    private byte[] source_address;
    private int source_ssid;
    private byte[] destination_address;
    private int destination_ssid;
    private byte[] address_field;
    private byte control_field;
    private byte pid_field;
    private byte[] info_field;

    private AX25Protocol.CommandResponseType commandResponse;

    public AX25Frame(byte[] source_address, byte[] destination_address) {
        this.source_address = AX25Protocol.padAddress(source_address);
        this.destination_address = AX25Protocol.padAddress(destination_address);

        // Default to an empty UI frame.
        source_ssid = AX25Protocol.SSID_DEFAULT;
        destination_ssid = AX25Protocol.SSID_DEFAULT;
        commandResponse = AX25Protocol.CommandResponseType.COMMAND;
        control_field = AX25Protocol.CONTROL_UIFRAME_FINAL;
        pid_field = AX25Protocol.PID_NO_LAYER_3_PROTOCOL;
        info_field = new byte[] {};

        address_field = AX25Protocol.createAddressField(commandResponse, source_address, destination_address);
    }

    public AX25Frame(byte[] source_address, int source_ssid, byte[] destination_address, int destination_ssid) {
        this.source_address = AX25Protocol.padAddress(source_address);
        this.source_ssid = source_ssid;
        this.destination_address = AX25Protocol.padAddress(destination_address);
        this.destination_ssid = destination_ssid;

        // Default to an empty UI frame.
        commandResponse = AX25Protocol.CommandResponseType.COMMAND;
        control_field = AX25Protocol.CONTROL_UIFRAME_FINAL;
        pid_field = AX25Protocol.PID_NO_LAYER_3_PROTOCOL;
        info_field = new byte[] {};

        address_field = AX25Protocol.createAddressField(commandResponse, source_address, source_ssid, destination_address, destination_ssid);
    }

    public byte[] getSourceAddress() {
        return source_address;
    }

    public int getSourceSSID() {
        return source_ssid;
    }

    public byte[] getDestinationAddress() {
        return destination_address;
    }

    public int getDestinationSSID() {
        return destination_ssid;
    }

    public byte[] getAddressField() {
        return address_field;
    }

    public byte getControlField() {
        return control_field;
    }

    public AX25Protocol.CommandResponseType getCommandResponseType() {
        return commandResponse;
    }

    public void setCommandResponseType(AX25Protocol.CommandResponseType type) {
        commandResponse = type;
        address_field = AX25Protocol.createAddressField(commandResponse, source_address, source_ssid, destination_address, destination_ssid);
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
