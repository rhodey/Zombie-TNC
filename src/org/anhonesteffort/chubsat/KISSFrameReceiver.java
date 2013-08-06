package org.anhonesteffort.chubsat;

import org.anhonesteffort.chubsat.kiss.KISSFrameListener;

public class KISSFrameReceiver implements KISSFrameListener {

    public void onKISSFrameReceived(byte[] frame) {
        System.out.println("from tnc: " + StringUtils.getHexString(frame));
    }

}
