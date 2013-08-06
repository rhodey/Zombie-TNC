package org.anhonesteffort.chubsat;

import org.anhonesteffort.chubsat.kiss.KISSDataListener;

public class KISSDataReceiver implements KISSDataListener {

    public void onDataReceived(byte[] data) {
        System.out.println("from tnc: " + StringUtils.getHexString(data));
    }

}
