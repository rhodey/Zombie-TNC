package org.anhonesteffort.tnc.kiss;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.util.ArrayList;

public class KISSPort implements SerialPortEventListener {
    private byte hdlc_port;
    private byte received_hdlc_port;
    private byte received_command;
    private SerialPort serialPort;
    private ArrayList<Byte> receivedData = new ArrayList<Byte>();
    private ArrayList<KSSDataListener> kssDataFrameListeners = new ArrayList<KSSDataListener>();

    private enum KISSState {SEARCH_FEND, SEARCH_COMMAND, SEARCH_DATA};
    private KISSState state = KISSState.SEARCH_FEND;

    public KISSPort(SerialPort serialPort, int hdlc_port) throws SerialPortException {
        if(hdlc_port > 0x0F | hdlc_port < 0x00)
            throw new IllegalArgumentException("HDLC port out of range, values 0 - 15 allowed.");

        this.hdlc_port = (byte)hdlc_port;
        this.serialPort = serialPort;
        serialPort.addEventListener(this);
    }

    private void callFrameListeners() {
        if(received_hdlc_port != hdlc_port)
            return;

        byte[] received_data = new byte[receivedData.size()];
        for(int i = 0; i < received_data.length; i++)
            received_data[i] = receivedData.get(i).byteValue();
        received_data = KISSProtocol.unescapeData(received_data);

        if(received_command == KISSProtocol.COMMAND_DATA) {
            for(KSSDataListener listener : kssDataFrameListeners)
                listener.onKISSDataReceived(received_data);
        }
    }

    public void serialEvent(SerialPortEvent serialPortEvent) {
        byte[] bytes;

        if(serialPortEvent.isRXCHAR()) {
            try {
                bytes = serialPort.readBytes();
                for(int i = 0; i < bytes.length; i++) {
                    switch (state) {
                        case SEARCH_FEND:
                            if(bytes[i] == KISSProtocol.CODE_FEND)
                                state = KISSState.SEARCH_COMMAND;
                            break;

                        case SEARCH_COMMAND:
                            if(bytes[i] == KISSProtocol.CODE_FEND)
                                break;

                            received_hdlc_port = KISSProtocol.unpackCommandByte(bytes[i])[0];
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
            }
            catch (SerialPortException e) {
                System.out.println("Error reading bytes from port! " + e);
            }
        }
    }

    public void addKISSDataListener(KSSDataListener listener) {
        kssDataFrameListeners.add(listener);
    }

    public void removeKISSFrameListener(KSSDataListener listener) {
        kssDataFrameListeners.remove(listener);
    }

    public void transmitData(byte[] data) throws SerialPortException {
        byte[] escaped_data;
        byte[] frame;

        escaped_data = KISSProtocol.escapeData(data);
        frame = new byte[escaped_data.length + 3];

        frame[0] = KISSProtocol.CODE_FEND;
        frame[1] = KISSProtocol.createCommandByte(hdlc_port, KISSProtocol.COMMAND_DATA);
        for(int i = 0; i < escaped_data.length; i++)
            frame[i + 2] = escaped_data[i];
        frame[frame.length - 1] = KISSProtocol.CODE_FEND;

        //System.out.println("KISS frame transmitted: " + new String(frame));
        serialPort.writeBytes(frame);
    }

    public void setHalfDuplex() throws SerialPortException {
        byte[] frame = new byte[4];
        frame[0] = KISSProtocol.CODE_FEND;
        frame[1] = KISSProtocol.createCommandByte(hdlc_port, KISSProtocol.COMMAND_FULL_DUPLEX);
        frame[2] = 0x00;
        frame[3] = KISSProtocol.CODE_FEND;

        //System.out.println("KISS frame transmitted: " + new String(frame));
        serialPort.writeBytes(frame);
    }

    public void setFullDuplex() throws SerialPortException {
        byte[] frame = new byte[4];
        frame[0] = KISSProtocol.CODE_FEND;
        frame[1] = KISSProtocol.createCommandByte(hdlc_port, KISSProtocol.COMMAND_FULL_DUPLEX);
        frame[2] = 0x01;
        frame[3] = KISSProtocol.CODE_FEND;

        //System.out.println("KISS frame transmitted: " + new String(frame));
        serialPort.writeBytes(frame);
    }

    public void setTransmitterDelay(int ms) throws SerialPortException {
        byte[] escaped_data = new byte[] {(byte)(ms/10)};
        byte[] frame;

        if(ms > 2550 || ms < 0)
            throw new IllegalArgumentException("Transmitter delay out of bounds, values 0 - 2550 allowed.");

        escaped_data = KISSProtocol.escapeData(escaped_data);
        frame = new byte[escaped_data.length + 3];

        frame[0] = KISSProtocol.CODE_FEND;
        frame[1] = KISSProtocol.createCommandByte(hdlc_port, KISSProtocol.COMMAND_TX_DELAY);
        for(int i = 0; i < escaped_data.length; i++)
            frame[i + 2] = escaped_data[i];
        frame[frame.length - 1] = KISSProtocol.CODE_FEND;

        //System.out.println("KISS frame transmitted: " + new String(frame));
        serialPort.writeBytes(frame);
    }

    public void setTransmitterTail(int ms) throws SerialPortException {
        byte[] escaped_data = new byte[] {(byte)(ms/10)};
        byte[] frame;

        if(ms > 2550 || ms < 0)
            throw new IllegalArgumentException("Transmitter tail out of bounds, values 0 - 2550 allowed.");

        escaped_data = KISSProtocol.escapeData(escaped_data);
        frame = new byte[escaped_data.length + 3];

        frame[0] = KISSProtocol.CODE_FEND;
        frame[1] = KISSProtocol.createCommandByte(hdlc_port, KISSProtocol.COMMAND_TX_TAIL);
        for(int i = 0; i < escaped_data.length; i++)
            frame[i + 2] = escaped_data[i];
        frame[frame.length - 1] = KISSProtocol.CODE_FEND;

        //System.out.println("KISS frame transmitted: " + new String(frame));
        serialPort.writeBytes(frame);
    }

    public void setPersistenceParameter(byte p) throws SerialPortException {
        byte[] escaped_data = new byte[] {p};
        byte[] frame;

        escaped_data = KISSProtocol.escapeData(escaped_data);
        frame = new byte[escaped_data.length + 3];

        frame[0] = KISSProtocol.CODE_FEND;
        frame[1] = KISSProtocol.createCommandByte(hdlc_port, KISSProtocol.COMMAND_P);
        for(int i = 0; i < escaped_data.length; i++)
            frame[i + 2] = escaped_data[i];
        frame[frame.length - 1] = KISSProtocol.CODE_FEND;

        //System.out.println("KISS frame transmitted: " + new String(frame));
        serialPort.writeBytes(frame);
    }

    public void setSlotTime(int ms) throws SerialPortException {
        byte[] escaped_data = new byte[] {(byte)(ms/10)};
        byte[] frame;

        if(ms > 2550 || ms < 0)
            throw new IllegalArgumentException("Slot time out of bounds, values 0 - 2550 allowed.");

        escaped_data = KISSProtocol.escapeData(escaped_data);
        frame = new byte[escaped_data.length + 3];

        frame[0] = KISSProtocol.CODE_FEND;
        frame[1] = KISSProtocol.createCommandByte(hdlc_port, KISSProtocol.COMMAND_SLOT_TIME);
        for(int i = 0; i < escaped_data.length; i++)
            frame[i + 2] = escaped_data[i];
        frame[frame.length - 1] = KISSProtocol.CODE_FEND;

        //System.out.println("KISS frame transmitted: " + new String(frame));
        serialPort.writeBytes(frame);
    }

    public void setHardware(byte[] data) throws SerialPortException {
        byte[] escaped_data;
        byte[] frame;

        escaped_data = KISSProtocol.escapeData(data);
        frame = new byte[escaped_data.length + 3];

        frame[0] = KISSProtocol.CODE_FEND;
        frame[1] = KISSProtocol.createCommandByte(hdlc_port, KISSProtocol.COMMAND_SET_HARDWARE);
        for(int i = 0; i < escaped_data.length; i++)
            frame[i + 2] = escaped_data[i];
        frame[frame.length - 1] = KISSProtocol.CODE_FEND;

        //System.out.println("KISS frame transmitted: " + new String(frame));
        serialPort.writeBytes(frame);
    }

    public void exitKISSMode() throws SerialPortException {
        byte[] frame = new byte[4];
        frame[0] = KISSProtocol.CODE_FEND;
        frame[1] = KISSProtocol.createCommandByte(0, KISSProtocol.COMMAND_RETURN);
        frame[2] = (byte)0xFF;
        frame[3] = KISSProtocol.CODE_FEND;

        //System.out.println("KISS frame transmitted: " + new String(frame));
        serialPort.writeBytes(frame);
    }

    public void close() throws SerialPortException {
        serialPort.removeEventListener();
    }
}
