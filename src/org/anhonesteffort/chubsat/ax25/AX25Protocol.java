package org.anhonesteffort.chubsat.ax25;

public class AX25Protocol {
    public static final char SSID_DEFAULT = 0x00;
    public static final char CONTROL_UI_P = 0x13;
    public static final char CONTROL_UI_F = 0x03;
    public static final char PID_NO_L3_PROTOCOL = 0xF0;

    public static byte[] createAddressField(char[] source_address, char[] destination_address) {
        if(source_address.length == 0 || source_address.length > 6)
            throw new IllegalArgumentException("Source address invalid, length must be less than 7 and non-zero.");
        if(destination_address.length == 0 || destination_address.length > 6)
            throw new IllegalArgumentException("Destination address invalid, length must be less than 7 and non-zero.");

        byte[] address_field = new byte[14];
        int pos;
        for(pos = 0; pos < address_field.length; pos++) {
            if(pos < destination_address.length)
                address_field[pos] = (byte)(destination_address[pos] << 2);
            else if(pos < 6)
                address_field[pos] = 0x20;
            else if(pos > 6 && (pos - 7) < source_address.length)
                address_field[pos] = (byte)(source_address[pos - 7] << 2);
            else if(pos > 6)
                address_field[pos] = 0x20;
        }
        address_field[6] = 0x60;
        address_field[13] = (byte)0xE1;
        return address_field;
    }

    public static byte[] createAddressField(char[] source_address, byte source_ssid, char[] destination_address, byte destination_ssid) {
        if(source_address.length == 0 || source_address.length > 6)
            throw new IllegalArgumentException("Source address invalid, length must be less than 7 and non-zero.");
        if(destination_address.length == 0 || destination_address.length > 6)
            throw new IllegalArgumentException("Destination address invalid, length must be less than 7 and non-zero.");

        byte[] address_field = new byte[14];
        int pos;
        for(pos = 0; pos < address_field.length; pos++) {
            if(pos < destination_address.length)
                address_field[pos] = (byte)(destination_address[pos] << 2);
            else if(pos < 6)
                address_field[pos] = 0x20;
            else if(pos > 6 && (pos - 7) < source_address.length)
                address_field[pos] = (byte)(source_address[pos - 7] << 2);
            else if(pos > 6)
                address_field[pos] = 0x20;
        }
        address_field[6] = 0x60;
        address_field[13] = (byte)0xE1;
        return address_field;
    }
}
