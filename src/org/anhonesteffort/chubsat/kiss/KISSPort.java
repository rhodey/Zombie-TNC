package org.anhonesteffort.chubsat.kiss;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import org.anhonesteffort.chubsat.StringUtils;
import org.anhonesteffort.chubsat.ax25.AX25UIFrame;

import java.util.ArrayList;

public class KISSPort /*implements SerialPortEventListener*/ {
    private SerialPort serialPort;
    private ArrayList<KISSDataListener> kissFrameListeners;
    private KISSState state = KISSState.SEARCH_FEND;
    private byte received_port;
    private byte received_command;
    private ArrayList<Byte> receivedData;

    private enum KISSState {SEARCH_FEND, SEARCH_COMMAND, SEARCH_DATA};

    public KISSPort(SerialPort serialPort) throws SerialPortException {
        this.serialPort = serialPort;
        //this.serialPort.addEventListener(this);
        kissFrameListeners = new ArrayList<KISSDataListener>();
        receivedData = new ArrayList<Byte>();
    }

    private void callFrameListeners() {
        byte[] received_data = new byte[receivedData.size()];
        for(int i = 0; i < received_data.length; i++)
            received_data[i] = receivedData.get(i).byteValue();
        received_data = KISSProtocol.unescapeData(received_data);

        if(received_command == KISSProtocol.COMMAND_DATA) {
            for(KISSDataListener listener : kissFrameListeners)
                listener.onDataReceived(received_data);
        }
    }

    public void serialEvent(SerialPortEvent serialPortEvent, byte[] bytes) {
        //byte[] bytes;

        if(serialPortEvent.isRXCHAR()) {
            //try {
                //bytes = serialPort.readBytes();
                for(int i = 0; i < bytes.length; i++) {
                    switch (state) {
                        case SEARCH_FEND:
                            if(bytes[i] == KISSProtocol.CODE_FEND)
                                state = KISSState.SEARCH_COMMAND;
                            break;

                        case SEARCH_COMMAND:
                            if(bytes[i] == KISSProtocol.CODE_FEND)
                                break;

                            received_port = KISSProtocol.unpackCommandByte(bytes[i])[0];
                            received_command = KISSProtocol.unpackCommandByte(bytes[i])[1];
                            state = KISSState.SEARCH_DATA;
                            break;

                        case SEARCH_DATA:
                            if(bytes[i] == KISSProtocol.CODE_FEND) {
                                callFrameListeners();
                                receivedData = new ArrayList<Byte>();
                                state = KISSState.SEARCH_FEND;
                            }
                            else
                                receivedData.add(bytes[i]);
                            break;
                    }
                }
            //}
            //catch (SerialPortException e) {
            //    System.out.println("Error reading bytes from port: " + e);
            //}
        }
    }

    public void addKISSDataListener(KISSDataListener listener) {
        kissFrameListeners.add(listener);
    }

    public void removeKISSFrameListener(KISSDataListener listener) {
        kissFrameListeners.remove(listener);
    }

    public void sendFrame(AX25UIFrame frame) throws SerialPortException {
        System.out.println("to tnc: " + StringUtils.getHexString(frame.toByteArray()));
        //serialPort.writeBytes(frame.toByteArray());
    }

    public void sendData(int port, byte[] data) throws SerialPortException {
        byte[] escaped_data;
        byte[] frame;

        escaped_data = KISSProtocol.escapeData(data);
        frame = new byte[escaped_data.length + 3];

        frame[0] = KISSProtocol.CODE_FEND;
        frame[1] = KISSProtocol.createCommandByte(port, KISSProtocol.COMMAND_DATA);
        for(int i = 0; i < escaped_data.length; i++)
            frame[i + 2] = escaped_data[i];
        frame[frame.length - 1] = KISSProtocol.CODE_FEND;

        System.out.println("to tnc: " + StringUtils.getHexString(frame));
        //serialPort.writeBytes(frame);
    }

    public void sendData(int port, char[] data) throws SerialPortException {
        byte[] escaped_data;
        byte[] frame;

        escaped_data = KISSProtocol.escapeData(data);
        frame = new byte[escaped_data.length + 3];

        frame[0] = KISSProtocol.CODE_FEND;
        frame[1] = KISSProtocol.createCommandByte(port, KISSProtocol.COMMAND_DATA);
        for(int i = 0; i < escaped_data.length; i++)
            frame[i + 2] = escaped_data[i];
        frame[frame.length - 1] = KISSProtocol.CODE_FEND;

        System.out.println("to tnc: " + StringUtils.getHexString(frame));
        //serialPort.writeBytes(frame);
    }

    public void setHalfDuplex(int port) throws SerialPortException {
        byte[] frame = new byte[4];
        frame[0] = KISSProtocol.CODE_FEND;
        frame[1] = KISSProtocol.createCommandByte(port, KISSProtocol.COMMAND_FULL_DUPLEX);
        frame[2] = 0x00;
        frame[3] = KISSProtocol.CODE_FEND;

        System.out.println("to tnc: " + StringUtils.getHexString(frame));
        //serialPort.writeBytes(frame);
    }

    public void setFullDuplex(int port) throws SerialPortException {
        byte[] frame = new byte[4];
        frame[0] = KISSProtocol.CODE_FEND;
        frame[1] = KISSProtocol.createCommandByte(port, KISSProtocol.COMMAND_FULL_DUPLEX);
        frame[2] = 0x01;
        frame[3] = KISSProtocol.CODE_FEND;

        System.out.println("to tnc: " + StringUtils.getHexString(frame));
        //serialPort.writeBytes(frame);
    }

    public void setTransmitterDelay(int port, int ms) throws SerialPortException {
        byte[] escaped_data = new byte[] {(byte)(ms/10)};
        byte[] frame;

        if(ms > 2550 || ms < 0)
            throw new IllegalArgumentException("Transmitter delay out of bounds, values 0 - 2550 allowed.");

        escaped_data = KISSProtocol.escapeData(escaped_data);
        frame = new byte[escaped_data.length + 3];

        frame[0] = KISSProtocol.CODE_FEND;
        frame[1] = KISSProtocol.createCommandByte(port, KISSProtocol.COMMAND_TX_DELAY);
        for(int i = 0; i < escaped_data.length; i++)
            frame[i + 2] = escaped_data[i];
        frame[frame.length - 1] = KISSProtocol.CODE_FEND;

        System.out.println("to tnc: " + StringUtils.getHexString(frame));
        //serialPort.writeBytes(frame);
    }

    public void setTransmitterTail(int port, int ms) throws SerialPortException {
        byte[] escaped_data = new byte[] {(byte)(ms/10)};
        byte[] frame;

        if(ms > 2550 || ms < 0)
            throw new IllegalArgumentException("Transmitter tail out of bounds, values 0 - 2550 allowed.");

        escaped_data = KISSProtocol.escapeData(escaped_data);
        frame = new byte[escaped_data.length + 3];

        frame[0] = KISSProtocol.CODE_FEND;
        frame[1] = KISSProtocol.createCommandByte(port, KISSProtocol.COMMAND_TX_TAIL);
        for(int i = 0; i < escaped_data.length; i++)
            frame[i + 2] = escaped_data[i];
        frame[frame.length - 1] = KISSProtocol.CODE_FEND;

        System.out.println("to tnc: " + StringUtils.getHexString(frame));
        //serialPort.writeBytes(frame);
    }

    public void setPersistenceParameter(int port, byte p) throws SerialPortException {
        byte[] escaped_data = new byte[] {p};
        byte[] frame;

        escaped_data = KISSProtocol.escapeData(escaped_data);
        frame = new byte[escaped_data.length + 3];

        frame[0] = KISSProtocol.CODE_FEND;
        frame[1] = KISSProtocol.createCommandByte(port, KISSProtocol.COMMAND_P);
        for(int i = 0; i < escaped_data.length; i++)
            frame[i + 2] = escaped_data[i];
        frame[frame.length - 1] = KISSProtocol.CODE_FEND;

        System.out.println("to tnc: " + StringUtils.getHexString(frame));
        //serialPort.writeBytes(frame);
    }

    public void setSlotTime(int port, int ms) throws SerialPortException {
        byte[] escaped_data = new byte[] {(byte)(ms/10)};
        byte[] frame;

        if(ms > 2550 || ms < 0)
            throw new IllegalArgumentException("Slot time out of bounds, values 0 - 2550 allowed.");

        escaped_data = KISSProtocol.escapeData(escaped_data);
        frame = new byte[escaped_data.length + 3];

        frame[0] = KISSProtocol.CODE_FEND;
        frame[1] = KISSProtocol.createCommandByte(port, KISSProtocol.COMMAND_SLOT_TIME);
        for(int i = 0; i < escaped_data.length; i++)
            frame[i + 2] = escaped_data[i];
        frame[frame.length - 1] = KISSProtocol.CODE_FEND;

        System.out.println("to tnc: " + StringUtils.getHexString(frame));
        //serialPort.writeBytes(frame);
    }

    public void setHardware(int port, byte[] data) throws SerialPortException {
        byte[] escaped_data;
        byte[] frame;

        escaped_data = KISSProtocol.escapeData(data);
        frame = new byte[escaped_data.length + 3];

        frame[0] = KISSProtocol.CODE_FEND;
        frame[1] = KISSProtocol.createCommandByte(port, KISSProtocol.COMMAND_SET_HARDWARE);
        for(int i = 0; i < escaped_data.length; i++)
            frame[i + 2] = escaped_data[i];
        frame[frame.length - 1] = KISSProtocol.CODE_FEND;

        System.out.println("to tnc: " + StringUtils.getHexString(frame));
        //serialPort.writeBytes(frame);
    }

    public void exitKISSMode() throws SerialPortException {
        byte[] frame = new byte[4];
        frame[0] = KISSProtocol.CODE_FEND;
        frame[1] = KISSProtocol.createCommandByte(0, KISSProtocol.COMMAND_RETURN);
        frame[2] = (byte)0xFF;
        frame[3] = KISSProtocol.CODE_FEND;

        System.out.println("to tnc: " + StringUtils.getHexString(frame));
        //serialPort.writeBytes(frame);
    }
}
