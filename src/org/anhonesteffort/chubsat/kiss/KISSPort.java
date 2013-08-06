package org.anhonesteffort.chubsat.kiss;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.anhonesteffort.chubsat.StringUtils;
import org.anhonesteffort.chubsat.ax25.AX25UIFrame;

import java.util.ArrayList;

public class KISSPort implements SerialPortEventListener {
    private SerialPort serialPort;
    private ArrayList<KISSFrameListener> kissFrameListeners;

    public KISSPort(SerialPort serialPort) {
        this.serialPort = serialPort;
        kissFrameListeners = new ArrayList<KISSFrameListener>();
    }

    public void serialEvent(SerialPortEvent serialPortEvent) {
        byte[] frame;

        if(serialPortEvent.isRXCHAR()) {
            try {
                frame = serialPort.readBytes();
                for(KISSFrameListener listener : kissFrameListeners)
                    listener.onKISSFrameReceived(frame);
            }
            catch (SerialPortException e) {
                System.out.println("Error reading bytes from port: " + e);
            }
        }
    }

    public void addKISSFrameListener(KISSFrameListener listener) {
        kissFrameListeners.add(listener);
    }

    public void removeKISSFrameListener(KISSFrameListener listener) {
        kissFrameListeners.remove(listener);
    }

    public void sendFrame(AX25UIFrame frame) throws SerialPortException {
        System.out.println("to tnc: " + StringUtils.getHexString(frame.toByteArray()));
        serialPort.writeBytes(frame.toByteArray());
    }

    public void sendData(int port, byte[] data) throws SerialPortException {
        byte[] escaped_data;
        byte[] frame;

        escaped_data = KISSProtocol.escapeData(data);
        frame = new byte[escaped_data.length + 3];

        frame[0] = (byte) KISSProtocol.FEND_CHAR;
        frame[1] = KISSProtocol.createCommandByte(port, KISSProtocol.DATA_CMD);
        for(int i = 0; i < escaped_data.length; i++)
            frame[i + 2] = escaped_data[i];
        frame[frame.length - 1] = (byte) KISSProtocol.FEND_CHAR;

        System.out.println("to tnc: " + StringUtils.getHexString(frame));
        serialPort.writeBytes(frame);
    }

    public void sendData(int port, char[] data) throws SerialPortException {
        byte[] escaped_data;
        byte[] frame;

        escaped_data = KISSProtocol.escapeData(data);
        frame = new byte[escaped_data.length + 3];

        frame[0] = (byte) KISSProtocol.FEND_CHAR;
        frame[1] = KISSProtocol.createCommandByte(port, KISSProtocol.DATA_CMD);
        for(int i = 0; i < escaped_data.length; i++)
            frame[i + 2] = escaped_data[i];
        frame[frame.length - 1] = (byte) KISSProtocol.FEND_CHAR;

        System.out.println("to tnc: " + StringUtils.getHexString(frame));
        serialPort.writeBytes(frame);
    }

    public void setHalfDuplex(int port) throws SerialPortException {
        byte[] frame = new byte[4];
        frame[0] = (byte) KISSProtocol.FEND_CHAR;
        frame[1] = KISSProtocol.createCommandByte(port, KISSProtocol.FULL_DUPLEX_CMD);
        frame[2] = (byte)0;
        frame[3] = (byte) KISSProtocol.FEND_CHAR;

        System.out.println("to tnc: " + StringUtils.getHexString(frame));
        serialPort.writeBytes(frame);
    }

    public void setFullDuplex(int port) throws SerialPortException {
        byte[] frame = new byte[4];
        frame[0] = (byte) KISSProtocol.FEND_CHAR;
        frame[1] = KISSProtocol.createCommandByte(port, KISSProtocol.FULL_DUPLEX_CMD);
        frame[2] = (byte)1;
        frame[3] = (byte) KISSProtocol.FEND_CHAR;

        System.out.println("to tnc: " + StringUtils.getHexString(frame));
        serialPort.writeBytes(frame);
    }

    public void setTransmitterDelay(int port, int ms) throws SerialPortException {
        byte[] escaped_data = new byte[] {(byte)(ms/10)};
        byte[] frame;

        if(ms > 2550 || ms < 0)
            throw new IllegalArgumentException("Transmitter delay out of bounds, values 0 - 2550 allowed.");

        escaped_data = KISSProtocol.escapeData(escaped_data);
        frame = new byte[escaped_data.length + 3];

        frame[0] = (byte) KISSProtocol.FEND_CHAR;
        frame[1] = KISSProtocol.createCommandByte(port, KISSProtocol.TX_DELAY_CMD);
        for(int i = 0; i < escaped_data.length; i++)
            frame[i + 2] = escaped_data[i];
        frame[frame.length - 1] = (byte) KISSProtocol.FEND_CHAR;

        System.out.println("to tnc: " + StringUtils.getHexString(frame));
        serialPort.writeBytes(frame);
    }

    public void setTransmitterTail(int port, int ms) throws SerialPortException {
        byte[] escaped_data = new byte[] {(byte)(ms/10)};
        byte[] frame;

        if(ms > 2550 || ms < 0)
            throw new IllegalArgumentException("Transmitter tail out of bounds, values 0 - 2550 allowed.");

        escaped_data = KISSProtocol.escapeData(escaped_data);
        frame = new byte[escaped_data.length + 3];

        frame[0] = (byte) KISSProtocol.FEND_CHAR;
        frame[1] = KISSProtocol.createCommandByte(port, KISSProtocol.TX_TAIL_CMD);
        for(int i = 0; i < escaped_data.length; i++)
            frame[i + 2] = escaped_data[i];
        frame[frame.length - 1] = (byte) KISSProtocol.FEND_CHAR;

        System.out.println("to tnc: " + StringUtils.getHexString(frame));
        serialPort.writeBytes(frame);
    }

    public void setPersistenceParameter(int port, byte p) throws SerialPortException {
        byte[] escaped_data = new byte[] {p};
        byte[] frame;

        escaped_data = KISSProtocol.escapeData(escaped_data);
        frame = new byte[escaped_data.length + 3];

        frame[0] = (byte) KISSProtocol.FEND_CHAR;
        frame[1] = KISSProtocol.createCommandByte(port, KISSProtocol.P_COMMAND);
        for(int i = 0; i < escaped_data.length; i++)
            frame[i + 2] = escaped_data[i];
        frame[frame.length - 1] = (byte) KISSProtocol.FEND_CHAR;

        System.out.println("to tnc: " + StringUtils.getHexString(frame));
        serialPort.writeBytes(frame);
    }

    public void setSlotTime(int port, int ms) throws SerialPortException {
        byte[] escaped_data = new byte[] {(byte)(ms/10)};
        byte[] frame;

        if(ms > 2550 || ms < 0)
            throw new IllegalArgumentException("Slot time out of bounds, values 0 - 2550 allowed.");

        escaped_data = KISSProtocol.escapeData(escaped_data);
        frame = new byte[escaped_data.length + 3];

        frame[0] = (byte) KISSProtocol.FEND_CHAR;
        frame[1] = KISSProtocol.createCommandByte(port, KISSProtocol.SLOT_TIME_CMD);
        for(int i = 0; i < escaped_data.length; i++)
            frame[i + 2] = escaped_data[i];
        frame[frame.length - 1] = (byte) KISSProtocol.FEND_CHAR;

        System.out.println("to tnc: " + StringUtils.getHexString(frame));
        serialPort.writeBytes(frame);
    }

    public void setHardware(int port, byte[] data) throws SerialPortException {
        byte[] escaped_data;
        byte[] frame;

        escaped_data = KISSProtocol.escapeData(data);
        frame = new byte[escaped_data.length + 3];

        frame[0] = (byte) KISSProtocol.FEND_CHAR;
        frame[1] = KISSProtocol.createCommandByte(port, KISSProtocol.SET_HARDWARE_CMD);
        for(int i = 0; i < escaped_data.length; i++)
            frame[i + 2] = escaped_data[i];
        frame[frame.length - 1] = (byte) KISSProtocol.FEND_CHAR;

        System.out.println("to tnc: " + StringUtils.getHexString(frame));
        serialPort.writeBytes(frame);
    }

    public void exitKISSMode() throws SerialPortException {
        byte[] frame = new byte[4];
        frame[0] = (byte) KISSProtocol.FEND_CHAR;
        frame[1] = KISSProtocol.createCommandByte(0, KISSProtocol.RETURN_CMD);
        frame[2] = (byte)0xFF;
        frame[3] = (byte) KISSProtocol.FEND_CHAR;

        System.out.println("to tnc: " + StringUtils.getHexString(frame));
        serialPort.writeBytes(frame);
    }
}
