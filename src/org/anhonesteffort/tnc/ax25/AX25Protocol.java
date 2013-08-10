package org.anhonesteffort.tnc.ax25;

public class AX25Protocol {
    public static final byte CONTROL_UIFRAME_POLL = 0x13;
    public static final byte CONTROL_UIFRAME_FINAL = 0x03;

    public static final byte PID_ISO_8208 = 0x01;
    public static final byte PID_COMPRESSED_TCP = 0x06;
    public static final byte PID_UNCOMPRESSED_TCP = 0x07;
    public static final byte PID_SEGMENTATION_FRAGMENT = 0x08;
    public static final byte PID_TEXNET_DATAGRAM_PROTOCOL = (byte)0xC3;
    public static final byte PID_LINK_QUALITY_PROTOCOL = (byte)0xC4;
    public static final byte PID_APPLETALK = (byte)0xCA;
    public static final byte PID_APPLETALK_ARP = (byte)0xCB;
    public static final byte PID_ARPA_INTERNET_PROTOCOL = (byte)0xCC;
    public static final byte PID_ARPA_ADDRESS_RESOLUTION_PROTOCOL = (byte)0xCD;
    public static final byte PID_FLEXNET = (byte)0xCE;
    public static final byte PID_NET_ROM = (byte)0xCF;
    public static final byte PID_NO_LAYER_3_PROTOCOL = (byte)0xF0;
    public static final byte PID_ESCAPRE_CHARACTER = (byte)0xFF;

    public static final int SSID_DEFAULT = 0;

    public static enum CommandResponseType {COMMAND, RESPONSE, OLD_VERSION};


    public static byte[] padAddress(byte[] address) {
        if(address.length == 0 || address.length > 6)
            throw new IllegalArgumentException("Address invalid, length must be less than 7 and non-zero.");
        byte[] out = new byte[6];
        for(int i = 0; i < out.length; i++) {
            if(i >= address.length)
                out[i] = 0x20;
            else
                out[i] = address[i];
        }
        return out;
    }

    private static byte createSourceSSID(int ssid) {
        if(ssid > 15 || ssid < 0)
            throw new IllegalArgumentException("SSID out of range, value 0 - 15 allowed.");
        return (byte)(((ssid << 1) | 0x80) | 0x01);
    }

    private static byte createDestinationSSID(int ssid) {
        if(ssid > 15 || ssid < 0)
            throw new IllegalArgumentException("SSID out of range, value 0 - 15 allowed.");
        return (byte)(((ssid << 1) & 0x7F) & 0xFE);
    }

    public static int unpackSSID(byte ssid_field) {
        return (ssid_field & 0x1E) >> 1;
    }

    public static CommandResponseType getCommandResponseType(byte source_ssid_field, byte destination_ssid_field) {
        if((destination_ssid_field & 0x80) == 0x80 && (source_ssid_field & 0x80) == 0x00)
            return CommandResponseType.COMMAND;
        else if((source_ssid_field & 0x80) == 0x80 && (destination_ssid_field & 0x80) == 0x00)
            return CommandResponseType.RESPONSE;
        else
            return CommandResponseType.OLD_VERSION;
    }

    public static byte[] createAddressField(CommandResponseType type, byte[] source_address, byte[] destination_address) {
        if(source_address.length == 0 || source_address.length > 6)
            throw new IllegalArgumentException("Source address invalid, length must be less than 7 and non-zero.");
        if(destination_address.length == 0 || destination_address.length > 6)
            throw new IllegalArgumentException("Destination address invalid, length must be less than 7 and non-zero.");

        byte[] address_field = new byte[14];
        source_address = padAddress(source_address);
        destination_address = padAddress(destination_address);

        for(int i = 0; i < (address_field.length - 1); i++) {
            if(i < 6)
                address_field[i] = (byte)(destination_address[i] << 1);
            else if(i > 6)
                address_field[i] = (byte)(source_address[i - 7] << 1);
        }

        if(type == CommandResponseType.RESPONSE) {
            address_field[6] = createDestinationSSID(SSID_DEFAULT);
            address_field[13] = createSourceSSID(SSID_DEFAULT);
        }
        else {
            address_field[6] = createDestinationSSID(SSID_DEFAULT);
            address_field[13] = createSourceSSID(SSID_DEFAULT);
        }

        return address_field;
    }

    public static byte[] createAddressField(CommandResponseType type, byte[] source_address, int source_ssid, byte[] destination_address, int destination_ssid) {
        if(source_address.length == 0 || source_address.length > 6)
            throw new IllegalArgumentException("Source address invalid, length must be less than 7 and non-zero.");
        if(destination_address.length == 0 || destination_address.length > 6)
            throw new IllegalArgumentException("Destination address invalid, length must be less than 7 and non-zero.");

        byte[] address_field = new byte[14];
        source_address = padAddress(source_address);
        destination_address = padAddress(destination_address);

        for(int i = 0; i < (address_field.length - 1); i++) {
            if(i < 6)
                address_field[i] = (byte)(destination_address[i] << 1);
            else if(i > 6)
                address_field[i] = (byte)(source_address[i - 7] << 1);
        }

        if(type == CommandResponseType.RESPONSE) {
            address_field[6] = createDestinationSSID(destination_ssid);
            address_field[13] = createSourceSSID(source_ssid);
        }
        else {
            address_field[6] = createDestinationSSID(destination_ssid);
            address_field[13] = createSourceSSID(source_ssid);
        }
        return address_field;
    }
}
