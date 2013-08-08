package org.anhonesteffort.scdp;

public class SCDPProtocol {

    public boolean isCommandPacket(byte[] bytes) {
        if(bytes.length > 0 && (bytes[0] & 0x80) == 0x00)
            return true;
        return false;
    }

    public boolean isDataPacket(byte[] bytes) {
        if(bytes.length > 3 && (bytes[0] & 0x80) == 0x80)
            return true;
        return false;
    }

    public int getCommand(byte[] command_packet) {
        if(command_packet.length == 0 || (command_packet[0] & 0x80) != 0x00)
            throw new IllegalArgumentException("Supplied byte array does not contain a command packet!");

        return command_packet[0];
    }

    public byte[] getCommandArguments(byte[] command_packet) {
        if(command_packet.length == 0 || (command_packet[0] & 0x80) != 0x00)
            throw new IllegalArgumentException("Supplied byte array does not contain a command packet!");

        byte[] out = new byte[command_packet.length - 1];
        for(int i = 0; i < out.length; i++)
            out[i] = command_packet[i + 1];

        return out;
    }

    public int getFileID(byte[] data_packet) {
        if(data_packet.length < 4 || (data_packet[0] & 0x80) != 0x80)
            throw new IllegalArgumentException("Supplied byte array does not contain a data packet!");

        return (data_packet[0] & 0x7F);
    }

    public int getPacketIndex(byte[] data_packet) {
        if(data_packet.length < 4 || (data_packet[0] & 0x80) != 0x80)
            throw new IllegalArgumentException("Supplied byte array does not contain a data packet!");

        return ((int)data_packet[1] << 8) | data_packet[2];
    }

    public byte[] getData(byte[] data_packet) {
        if(data_packet.length < 4 || (data_packet[0] & 0x80) != 0x80)
            throw new IllegalArgumentException("Supplied byte array does not contain a data packet!");

        byte[] out = new byte[data_packet.length - 3];
        for(int i = 0; i < out.length; i++)
            out[i] = data_packet[i + 3];

        return out;
    }

}
