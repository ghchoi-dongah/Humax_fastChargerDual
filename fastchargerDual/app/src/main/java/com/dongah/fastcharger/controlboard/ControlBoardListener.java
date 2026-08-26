package com.dongah.fastcharger.controlboard;

public interface ControlBoardListener {
    void onControlBoardReceive(RxData[] rxData);

    void onControlBoardSend(TxData[] txData);
}
