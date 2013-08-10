package org.anhonesteffort.tnc.ax25;

public interface AX25SecondaryStation {

    public void setOperator(AX25Operator operator);

    public int getSSID();

    public void onAX25FrameReceived(AX25Frame myFrame);

}
