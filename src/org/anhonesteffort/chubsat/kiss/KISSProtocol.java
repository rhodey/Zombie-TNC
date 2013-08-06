package org.anhonesteffort.chubsat.kiss;

public class KISSProtocol {
    public static final byte CODE_FEND = (byte)0xC0;
    public static final byte CODE_FESC = (byte)0xDB;
    public static final byte CODE_TFEND = (byte)0xDC;
    public static final byte CODE_TFESC = (byte)0xDD;

    public static final byte COMMAND_DATA = 0x00;
    public static final byte COMMAND_TX_DELAY = 0x01;
    public static final byte COMMAND_P = 0x02;
    public static final byte COMMAND_SLOT_TIME = 0x03;
    public static final byte COMMAND_TX_TAIL = 0x04;
    public static final byte COMMAND_FULL_DUPLEX = 0x05;
    public static final byte COMMAND_SET_HARDWARE = 0x06;
    public static final byte COMMAND_RETURN = (byte)0xFF;

    public static byte createCommandByte(int port, byte command) throws IllegalArgumentException {
        byte out;

        // Special case return command.
        if(command == COMMAND_RETURN)
            return COMMAND_RETURN;

        else if(port > 15 || port < 0)
            throw new IllegalArgumentException("Port out of range, values 0 - 15 allowed.");
        else if(command > 0x06)
            throw new IllegalArgumentException("Command out of range, values 0 - 6 allowed.");

        return (byte)((port << 4) | command);
    }

    // out[0] = port, out[1] = command.
    public static byte[] unpackCommandByte(byte command_byte) {
        byte[] out = new byte[2];

        out[0] = (byte)(command_byte >> 4);
        out[1] = (byte)(command_byte & 0x0F);
        return out;
    }

    public static byte[] escapeData(byte[] data) {
        int special_count = 0;
        byte[] out;

        for(int i = 0; i < data.length; i++) {
            if(data[i] == CODE_FEND || data[i] == CODE_FESC)
                special_count++;
        }
        out = new byte[data.length + special_count];
        special_count = 0;

        for(int i = 0; i < out.length; i++) {
            if(data[i - special_count] == CODE_FEND) {
                out[i++] = CODE_FESC;
                out[i] = CODE_TFEND;
                special_count++;
            }
            else if(data[i - special_count] == CODE_FESC) {
                out[i++] = CODE_FESC;
                out[i] = CODE_TFESC;
                special_count++;
            }
            else
                out[i] = data[i - special_count];
        }
        return out;
    }

    public static byte[] escapeData(char[] data) {
        int special_count = 0;
        byte[] out;

        for(int i = 0; i < data.length; i++) {
            if(data[i] == (char)CODE_FEND || data[i] == (char)CODE_FESC)
                special_count++;
        }
        out = new byte[data.length + special_count];
        special_count = 0;

        for(int i = 0; i < out.length; i++) {
            if(data[i - special_count] == (char)CODE_FEND) {
                out[i++] = CODE_FESC;
                out[i] = CODE_TFEND;
                special_count++;
            }
            else if(data[i - special_count] == (char)CODE_FESC) {
                out[i++] = CODE_FESC;
                out[i] = CODE_TFESC;
                special_count++;
            }
            else
                out[i] = (byte)data[i - special_count];
        }
        return out;
    }

    public static byte[] unescapeData(byte[] data) {
        int special_count = 0;
        boolean found;
        byte[] out;

        for(int i = 0; i < data.length - 1; i++) {
            if(data[i] == CODE_FESC && (data[i + 1] == CODE_TFESC || data[i + 1] == CODE_TFEND))
                special_count++;
        }
        out = new byte[data.length - special_count];
        special_count = 0;

        for(int i = 0; i < data.length; i++) {
            found = false;
            if(i != (data.length - 1) && data[i] == CODE_FESC) {
                if(data[i + 1] == CODE_TFEND) {
                    out[i++ - special_count++] = CODE_FEND;
                    found = true;
                }
                else if(data[i + 1] == CODE_TFESC) {
                    out[i++ - special_count++] = CODE_FESC;
                    found = true;
                }
            }

            if(!found)
                out[i - special_count] = data[i];
        }
        return out;
    }
}
