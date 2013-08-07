package org.anhonesteffort.chubsat;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import org.anhonesteffort.chubsat.ax25.AX25FrameReceiver;
import org.anhonesteffort.chubsat.ax25.AX25Protocol;
import org.anhonesteffort.chubsat.ax25.AX25UIFrame;
import org.anhonesteffort.chubsat.kiss.KISSPort;
import org.anhonesteffort.chubsat.kiss.KISSProtocol;

import java.nio.charset.Charset;

public class Main {

    public static void main(String[] args) {
        try {
            // Open and configure a serial port connected to a TNC in KISS mode.
            SerialPort tncPort = new SerialPort("COM9");
            //tncPort.openPort();
            //tncPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            // Create a new KISSPort using the configured serial port and add a KISSDataListener.
            KISSPort tnc = new KISSPort(tncPort);
            KISSDataReceiver dataReceiver = new KISSDataReceiver();
            AX25FrameReceiver frameReceiver = new AX25FrameReceiver();
            tnc.addKISSDataListener(dataReceiver);
            tnc.addKISSDataListener(frameReceiver);

            // Transmitting arbitrary data.
            tnc.sendData(0, "ChubSat, truth prevails.".getBytes("US-ASCII"));

            // Transmitting AX.25 UI frames.
            AX25UIFrame frame = new AX25UIFrame("WVTC".getBytes("US-ASCII"), "ALLYOU".getBytes("US-ASCII"));
            frame.setInfoField("ChubSat, truth prevails.".getBytes(Charset.forName("US-ASCII")));
            tnc.sendFrame(0, frame);

            frame.setInfoField("ChubSat, have it your way.".getBytes(Charset.forName("US-ASCII")));
            tnc.sendFrame(0, frame);

            // Clean up.
            //tncPort.closePort();
        }
        catch(Exception e) {
            System.out.println(e.toString());
        }
    }
}
