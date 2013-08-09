package org.anhonesteffort.tnc.ax25;

import jssc.SerialPortException;
import org.anhonesteffort.tnc.kiss.KSSDataListener;
import org.anhonesteffort.tnc.kiss.KISSPort;

import java.util.ArrayList;

// Does not support layer 2 repeater address encoding.
public class AX25Port implements KSSDataListener {
    private KISSPort kissPort;
    private ArrayList<AX25FrameListener> ax25FrameListeners;
    private ArrayList<AX25Operator> ax25Operators;

    private byte[] received_destination_address = new byte[6];
    private byte[] received_source_address = new byte[6];
    private int received_destination_ssid;
    private int received_source_ssid;
    private byte received_control_field;
    private byte received_pid_field;
    private ArrayList<Byte> receivedInfoField = new ArrayList<Byte>();

    private enum AX25State {SEARCH_DESTINATION_ADDRESS, SEARCH_SOURCE_ADDRESS, SEARCH_CONTROL, SEARCH_PID, SEARCH_INFO};
    private AX25State state = AX25State.SEARCH_DESTINATION_ADDRESS;

    public AX25Port(KISSPort kissPort) {
        this.kissPort = kissPort;
        this.kissPort.addKISSDataListener(this);
        ax25FrameListeners = new ArrayList<AX25FrameListener>();
        ax25Operators = new ArrayList<AX25Operator>();
    }

    public void addFrameListener(AX25FrameListener listener) {
        ax25FrameListeners.add(listener);
    }

    public void removeFrameListener(AX25FrameListener listener) {
        ax25FrameListeners.remove(listener);
    }

    public void addOperator(AX25Operator operator) {
        ax25Operators.add(operator);
    }

    public void removeOperator(AX25Operator operator) {
        ax25Operators.remove(operator);
    }

    private void frameComplete() {
        byte[] received_info_field = new byte[receivedInfoField.size()];
        for(int i = 0; i < received_info_field.length; i++)
            received_info_field[i] = receivedInfoField.get(i);

        for(AX25FrameListener listener : ax25FrameListeners) {
            listener.onAX25FrameReceived(received_destination_address,
                    received_destination_ssid,
                    received_source_address,
                    received_source_ssid,
                    received_control_field,
                    received_pid_field,
                    received_info_field);
        }

        for(AX25Operator operator : ax25Operators) {
            if(new String(received_destination_address).equals(new String(operator.getCallSign())))
                operator.onFrameReceived(received_destination_ssid,
                        received_source_address,
                        received_source_ssid,
                        received_control_field,
                        received_pid_field,
                        received_info_field);
        }
    }

    @Override
    public void onDataReceived(byte[] bytes) {
        for(int i = 0; i < bytes.length; i++) {
            switch (state) {
                case SEARCH_DESTINATION_ADDRESS:
                    if((bytes[i] & 0x01) == 0x01)
                        return;

                    if(i < 6)
                        received_destination_address[i] = (byte)((bytes[i] >> 1) & 0x7F) ;
                    else if(i == 6) {
                        received_destination_ssid = (bytes[i] & 0x1E) >> 1;
                        state = AX25State.SEARCH_SOURCE_ADDRESS;
                    }
                    break;

                case SEARCH_SOURCE_ADDRESS:
                    if((bytes[i] & 0x01) == 0x01 && i != 13)
                        return;

                    if(i < 13)
                        received_source_address[i - 7] = (byte)((bytes[i] >> 1) & 0x7F);
                    else if(i == 13) {
                        if((bytes[i] & 0x01) != 0x01)
                            return;

                        received_source_ssid = (bytes[i] & 0x1E) >> 1;
                        state = AX25State.SEARCH_CONTROL;
                    }
                    break;

                case SEARCH_CONTROL:
                    received_control_field = bytes[i];
                    state = AX25State.SEARCH_PID;
                    break;

                case SEARCH_PID:
                    received_pid_field = bytes[i];
                    state = AX25State.SEARCH_INFO;
                    break;

                case SEARCH_INFO:
                    receivedInfoField.add(bytes[i]);
                    break;
            }
        }
        if(state == AX25State.SEARCH_INFO)
            frameComplete();

        received_destination_address = new byte[6];
        received_source_address = new byte[6];
        receivedInfoField = new ArrayList<Byte>();
        state = AX25State.SEARCH_DESTINATION_ADDRESS;
    }

    public void sendFrame(AX25UIFrame frame) throws SerialPortException {
        kissPort.transmitData(frame.toByteArray());
    }

    public void close() {
        kissPort.removeKISSFrameListener(this);
    }
}