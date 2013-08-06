package org.anhonesteffort.chubsat;

import jssc.SerialPort;
import org.anhonesteffort.chubsat.ax25.AX25UIFrame;
import org.anhonesteffort.chubsat.kiss.KISSPort;

public class Main {

    public static void main(String[] args) {
        try {
            // Open and configure a serial port connected to a TNC in KISS mode.
            SerialPort tncPort = new SerialPort("COM9");
            tncPort.openPort();
            tncPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            // Create a new KISSPort using the configured serial port and add a KISSFrameListener.
            KISSPort tnc = new KISSPort(tncPort);
            KISSFrameReceiver frameReceiver = new KISSFrameReceiver();
            tnc.addKISSFrameListener(frameReceiver);

            // Transmitting arbitrary data.
            tnc.sendData(0, "ChubSat, truth prevails.".toCharArray());
            tnc.sendData(0, new byte[]{0, 32, 64});

            // Transmitting AX.25 UI frames.
            AX25UIFrame frame = new AX25UIFrame("WVTC".toCharArray(), "ALLYOU".toCharArray());
            frame.setInfoField("ChubSat, truth prevails.".toCharArray());
            tnc.sendFrame(frame);

            // Clean up.
            tncPort.closePort();
        }
        catch(Exception e) {
            System.out.println(e.toString());
        }
    }
}
