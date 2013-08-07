package org.anhonesteffort.chubsat.ax25;

import org.anhonesteffort.chubsat.StringUtils;
import org.anhonesteffort.chubsat.kiss.KISSDataListener;
import org.anhonesteffort.chubsat.kiss.KISSProtocol;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class AX25FrameReceiver implements KISSDataListener {
    private byte[] received_destination_address = new byte[6];
    private byte[] received_source_address = new byte[6];
    private byte received_destination_ssid;
    private byte received_source_ssid;
    private byte received_control_field;
    private byte received_pid_field;
    private ArrayList<Byte> receivedInfoField = new ArrayList<Byte>();

    private enum AX25State {SEARCH_DESTINATION_ADDRESS, SEARCH_SOURCE_ADDRESS, SEARCH_CONTROL, SEARCH_PID, SEARCH_INFO};
    private AX25State state = AX25State.SEARCH_DESTINATION_ADDRESS;

    private void frameComplete() {
        byte[] received_info_field = new byte[receivedInfoField.size()];
        for(int i = 0; i < received_info_field.length; i++)
            received_info_field[i] = receivedInfoField.get(i);

        System.out.println("AX25 frame from tnc: ");
        System.out.println("destination address: " + new String(received_destination_address));
        System.out.println("destination ssid: " + Integer.toHexString(received_destination_ssid & 0x000000FF));
        System.out.println("source address: " + new String(received_source_address));
        System.out.println("source ssid: " + Integer.toHexString(received_source_ssid & 0x000000FF));
        System.out.println("control field: " + Integer.toHexString(received_control_field & 0x000000FF));
        System.out.println("pid field: " + Integer.toHexString(received_pid_field & 0x000000FF));
        System.out.println("information field: " + new String(received_info_field));
    }

    public void onDataReceived(byte[] bytes) {

        for(int i = 0; i < bytes.length; i++) {
            switch (state) {
                case SEARCH_DESTINATION_ADDRESS:
                    if((bytes[i] & 0x01) == 0x01)
                        return;

                    if(i < 6)
                        received_destination_address[i] = (byte)((bytes[i] >> 1) & 0x7F) ;
                    else if(i == 6) {
                        received_destination_ssid = bytes[i];
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

                        received_source_ssid = bytes[i];
                        state = AX25State.SEARCH_CONTROL;
                    }
                    break;

                case SEARCH_CONTROL:
                    received_control_field = bytes[i];
                    state = AX25State.SEARCH_PID;
                    break;

                case SEARCH_PID:
                    if(bytes[i] != AX25Protocol.PID_NO_LAYER_3_PROTOCOL)
                        return;

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

}
