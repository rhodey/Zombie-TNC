package org.anhonesteffort.tnc.example;

import org.anhonesteffort.tnc.kiss.KSSDataListener;

public class SimpleKISSDataListener implements KSSDataListener {

    @Override
    public void onKISSDataReceived(byte[] data) {
        System.out.println("KISS data received: " + new String(data));
    }

}
