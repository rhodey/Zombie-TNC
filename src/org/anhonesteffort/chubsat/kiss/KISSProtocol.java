package org.anhonesteffort.chubsat.kiss;

public class KISSProtocol {
    public static final char FEND_CHAR = 0xC0;
    public static final char FESC_CHAR = 0xDB;
    public static final char TFEND_CHAR = 0xDC;
    public static final char TFESC_CHAR = 0xDD;

    public static final char DATA_CMD = 0x00;
    public static final char TX_DELAY_CMD = 0x01;
    public static final char P_COMMAND = 0x02;
    public static final char SLOT_TIME_CMD = 0x03;
    public static final char TX_TAIL_CMD = 0x04;
    public static final char FULL_DUPLEX_CMD = 0x05;
    public static final char SET_HARDWARE_CMD = 0x06;
    public static final char RETURN_CMD = 0xFF;

    public static byte createCommandByte(int port, char command) throws IllegalArgumentException {
        byte out;

        // Special case return command.
        if(command == RETURN_CMD)
            return (byte)RETURN_CMD;

        // Make sure port and commands are valid.
        else if(port > 15 || port < 0)
            throw new IllegalArgumentException("Port out of range, values 0 - 15 allowed.");
        else if(command > 0x06)
            throw new IllegalArgumentException("Command out of range, values 0 - 6 allowed.");

        return (byte)((port << 4) | command);
    }

    public static byte[] escapeData(byte[] data) {
        int special_count = 0;
        byte[] out;

        for(int i = 0; i < data.length; i++) {
            if(data[i] == (byte)FEND_CHAR || data[i] == (byte)FESC_CHAR)
                special_count++;
        }
        out = new byte[data.length + special_count];

        for(int i = 0; i < out.length; i++) {
            if(data[i] == (byte)FEND_CHAR) {
                out[i++] = (byte)FESC_CHAR;
                out[i] = (byte)TFEND_CHAR;
            }
            else if(data[i] == (byte)FESC_CHAR) {
                out[i++] = (byte)FESC_CHAR;
                out[i] = (byte)TFESC_CHAR;
            }
            else
                out[i] = data[i];
        }
        return out;
    }

    public static byte[] escapeData(char[] data) {
        int special_count = 0;
        byte[] out;

        for(int i = 0; i < data.length; i++) {
            if(data[i] == FEND_CHAR || data[i] == FESC_CHAR)
                special_count++;
        }
        out = new byte[data.length + special_count];

        for(int i = 0; i < out.length; i++) {
            if(data[i] == FEND_CHAR) {
                out[i++] = (byte)FESC_CHAR;
                out[i] = (byte)TFEND_CHAR;
            }
            else if(data[i] == FESC_CHAR) {
                out[i++] = (byte)FESC_CHAR;
                out[i] = (byte)TFESC_CHAR;
            }
            else
                out[i] = (byte)data[i];
        }
        return out;
    }

    public static byte[] unescapeData(byte[] data) {
        int special_count = 0;
        boolean found;
        byte[] out;

        for(int i = 0; i < data.length - 1; i++) {
            if(data[i] == (byte)FESC_CHAR && (data[i + 1] == (byte)TFESC_CHAR || data[i + 1] == (byte)TFEND_CHAR))
                special_count++;
        }
        out = new byte[data.length - special_count];

        for(int i = 0; i < data.length; i++) {
            found = false;
            if(i != (data.length - 1) && data[i] == (byte)FESC_CHAR) {
                if(data[i + 1] == (byte)TFEND_CHAR) {
                    out[i++] = (byte)FEND_CHAR;
                    found = true;
                }
                else if(data[i + 1] == (byte)TFESC_CHAR) {
                    out[i++] = (byte)FESC_CHAR;
                    found = true;
                }
            }

            if(!found)
                out[i] = data[i];
        }
        return out;
    }

    public static byte[] unescapeData(char[] data) {
        int special_count = 0;
        boolean found;
        byte[] out;

        for(int i = 0; i < data.length - 1; i++) {
            if(data[i] == FESC_CHAR && (data[i + 1] == TFESC_CHAR || data[i + 1] == TFEND_CHAR))
                special_count++;
        }
        out = new byte[data.length - special_count];

        for(int i = 0; i < data.length; i++) {
            found = false;
            if(i != (data.length - 1) && data[i] == FESC_CHAR) {
                if(data[i + 1] == TFEND_CHAR) {
                    out[i++] = (byte)FEND_CHAR;
                    found = true;
                }
                else if(data[i + 1] == TFESC_CHAR) {
                    out[i++] = (byte)FESC_CHAR;
                    found = true;
                }
            }

            if(!found)
                out[i] = (byte)data[i];
        }
        return out;
    }
}
