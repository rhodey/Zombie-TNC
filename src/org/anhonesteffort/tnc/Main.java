package org.anhonesteffort.tnc;

import jssc.SerialPort;
import org.anhonesteffort.tnc.ax25.AX25Frame;
import org.anhonesteffort.tnc.ax25.AX25Operator;
import org.anhonesteffort.tnc.ax25.AX25Port;
import org.anhonesteffort.tnc.ax25.AX25Protocol;
import org.anhonesteffort.tnc.example.SimpleAX25FrameListener;
import org.anhonesteffort.tnc.example.SimpleAX25Operator;
import org.anhonesteffort.tnc.example.SimpleAX25SecondaryStation;
import org.anhonesteffort.tnc.example.SimpleKISSDataListener;
import org.anhonesteffort.tnc.kiss.KISSPort;

public class Main {

    public static void main(String[] args) {
        try {
            // Open a serial port connected to a TNC in KISS mode.
            SerialPort serialPort = new SerialPort("COM9");
            //serialPort.openPort();
            //serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);


            /*
                KISS Examples
             */

            // Creating a KISSPort for High-Level Data Link Control port 0.
            KISSPort kissTNC0 = new KISSPort(0, serialPort);

            // Adding a KSSDataListener to process data received by the KISSPort.
            SimpleKISSDataListener kissDataListener = new SimpleKISSDataListener();
            kissTNC0.addKISSDataListener(kissDataListener);

            // Transmitting arbitrary data through the TNC.
            kissTNC0.transmitData("ChubSat, truth prevails.".getBytes("ASCII"));



            /*
                AX.25 Examples
             */

            // Creating an AX25Port for sending and receiving AX.25 frames through a KISSPort.
            AX25Port ax25Port = new AX25Port(kissTNC0);

            // Adding an AX25FrameListener to process frames received by the AX25Port.
            SimpleAX25FrameListener ax25FrameListener = new SimpleAX25FrameListener();
            ax25Port.addFrameListener(ax25FrameListener);

            /*
              Creating and transmitting Unnumbered Information Frames manually.
                AX25Frames default to UI Frames with source and destination SSIDs set to 0, C-bits set
                to COMMAND, Control Field set to UI Frame FINAL and PID set to NO LAYER 3 PROTOCOL
                implemented (as constructed below).
             */
            AX25Frame uiFrame = new AX25Frame("EARTH".getBytes("ASCII"), 0, "CHBSAT".getBytes("ASCII"), 0);
            uiFrame.setCommandResponseType(AX25Protocol.CommandResponseType.COMMAND);
            uiFrame.setControlField(AX25Protocol.CONTROL_UIFRAME_FINAL);
            uiFrame.setPIDField(AX25Protocol.PID_NO_LAYER_3_PROTOCOL);
            uiFrame.setInfoField("ChubSat, have it your way.".getBytes("ASCII"));
            ax25Port.sendFrame(uiFrame);

            // Creating an AX25Operator to transmit and receive frames on behalf of a call sign.
            SimpleAX25Operator earthOperator = new SimpleAX25Operator(ax25Port, "EARTH".getBytes("ASCII"));
            earthOperator.sendUnnumberedInformation("CHBSAT".getBytes("ASCII"), "ChubSat, like tears in the rain.".getBytes("ASCII"));
            earthOperator.sendUnnumberedInformation("EARTH".getBytes("ASCII"), "Hello Earth, this is Earth.".getBytes("ASCII"));

            // Creating an AX25SecondaryStation to transmit and receive frames on behalf of Operator: EARTH, SSID: 4.
            SimpleAX25SecondaryStation secondaryStation = new SimpleAX25SecondaryStation(4);
            earthOperator.addSecondaryStation(secondaryStation);
            secondaryStation.sendUnnumberedInformation("EARTH".getBytes("ASCII"), "Hello Earth SSID default, this is Earth SSID 4.".getBytes("ASCII"));
            secondaryStation.sendUnnumberedInformation("EARTH".getBytes("ASCII"), 4, "Hello Earth SSID 4, this is Earth SSID 4.".getBytes("ASCII"));

            /*
                Cleaning up
             */

            //serialPort.closePort();
        }
        catch(Exception e) {
            System.out.println(e.toString());
        }
    }
}
