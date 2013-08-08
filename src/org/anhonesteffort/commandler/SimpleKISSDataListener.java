package org.anhonesteffort.commandler;

import org.anhonesteffort.tnc.kiss.KSSDataListener;

public class SimpleKISSDataListener implements KSSDataListener {

    public void onDataReceived(byte[] data) {
        System.out.println("KISS data received: " + new String(data));
    }

}
