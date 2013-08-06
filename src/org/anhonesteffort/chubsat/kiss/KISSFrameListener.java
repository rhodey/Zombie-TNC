package org.anhonesteffort.chubsat.kiss;

import jssc.SerialPort;
import jssc.SerialPortEventListener;
import jssc.SerialPortEvent;

public interface KISSFrameListener {

    public void onKISSFrameReceived(byte[] frame);

}
