package org.anhonesteffort.chubsat.kiss;

import jssc.SerialPort;
import jssc.SerialPortEventListener;
import jssc.SerialPortEvent;

public interface KISSDataListener {

    public void onDataReceived(byte[] data);

}
