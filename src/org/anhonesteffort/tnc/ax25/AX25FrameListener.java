package org.anhonesteffort.tnc.ax25;

public interface AX25FrameListener {

    // U and S frames not yet supported.
    public void onAX25FrameReceived(byte[] destination_address, byte destination_ssid, byte[] source_address, byte source_ssid, byte control_field, byte[] info_field);

    public void onAX25FrameReceived(byte[] destination_address, byte destination_ssid, byte[] source_address, byte source_ssid, byte control_field, byte pid_field, byte[] info_field);

}
