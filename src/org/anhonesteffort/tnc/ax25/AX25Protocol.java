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

    public static final byte SSID_COMMAND_DESTINATION_DEFAULT = (byte)0xE0;
    public static final byte SSID_COMMAND_SOURCE_DEFAULT = 0x61;

    public static final byte SSID_RESPONSE_DESTINATION_DEFAULT = 0x60;
    public static final byte SSID_RESPONSE_SOURCE_DEFAULT = (byte)0xE1;


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

    public static byte[] createAddressField(byte[] source_address, byte[] destination_address) {
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

        address_field[6] = SSID_COMMAND_DESTINATION_DEFAULT;
        address_field[13] = SSID_COMMAND_SOURCE_DEFAULT;
        return address_field;
    }

    public static byte[] createAddressField(byte[] source_address, byte source_ssid, byte[] destination_address, byte destination_ssid) {
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

        // Make sure the extension bit rules are enforced.
        address_field[6] = (byte)(source_ssid & 0xFE);
        address_field[13] = (byte)(destination_ssid | 0x01);
        return address_field;
    }
}
