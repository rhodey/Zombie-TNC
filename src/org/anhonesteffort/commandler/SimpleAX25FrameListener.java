package org.anhonesteffort.commandler;

import org.anhonesteffort.tnc.ax25.AX25FrameListener;

public class SimpleAX25FrameListener implements AX25FrameListener {

    @Override
    public void onAX25FrameReceived(byte[] destination_address, int destination_ssid, byte[] source_address, int source_ssid, byte control_field, byte[] info_field) {
        // U and S frames not yet supported.
    }

    @Override
    public void onAX25FrameReceived(byte[] destination_address, int destination_ssid, byte[] source_address, int source_ssid, byte control_field, byte pid_field, byte[] info_field) {
        System.out.println("AX25 frame received: ");
        System.out.println("destination address: " + new String(destination_address));
        System.out.println("destination ssid: " + destination_ssid);
        System.out.println("source address: " + new String(source_address));
        System.out.println("source ssid: " + source_ssid);
        System.out.println("control field: " + Integer.toHexString(control_field & 0x000000FF));
        System.out.println("pid field: " + Integer.toHexString(pid_field & 0x000000FF));
        System.out.println("information field: " + new String(info_field));
    }

}
