package com.dongah.fastcharger.controlboard;

import com.dongah.fastcharger.utils.CRC16;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;

import android_serialport_api.SerialPort;

public class ControlBoard implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ControlBoard.class);


    public static final int HEAD_SIZE = 3;
    public static final int CRC_SIZE = 2;
    public static final int RX_SIZE = 46 * 2; //control board word type(x2)
    public static final int RX_DATA_SIZE = HEAD_SIZE + CRC_SIZE + RX_SIZE;

    /**
     * serial
     */
    SerialPort serialPort;
    private InputStream inputStream;
    private OutputStream outputStream;

    /**
     * buffer
     */
    private RxData[] rxData;
    private TxData[] txData;
    private int noDataCount;
    public boolean disconnected = true;
    private boolean bEndFlag = false;

    boolean isOpen = false;
    int maxCh;
    String comPort;
    int tCount = 0;
    int curCh = 0;
    boolean chkTx = false;
    int availableCount;
    Thread receiveThread;

    /**
     * 통신 설계
     */
    private final byte[] sendCh = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06};
    short[] rxBuffer200 = new short[10];
    byte[] receiveData = new byte[128];
    byte[] realReceiveData = new byte[RX_DATA_SIZE];
    /**
     * controlBoard Listener register
     */
    ControlBoardListener controlBoardListener;

    public void setDspControlListener(ControlBoardListener controlBoardListener) {
        this.controlBoardListener = controlBoardListener;
    }

    public void setControlBoardListenerStop() {
        controlBoardListener = null;
    }

    /**
     * Getter & Setter
     */
    public RxData getRxData(int ch) {
        return rxData[ch];
    }

    public TxData getTxData(int ch) {
        return txData[ch];
    }

    public boolean isDisconnected() {
        return disconnected;
    }

    /**
     * ControlBoard constructor
     *
     * @param maxCh   max channel
     * @param comPort use port name
     */
    public ControlBoard(int maxCh, String comPort) {
        try {
            this.maxCh = maxCh;
            this.comPort = comPort;

            serialPort = new SerialPort(new File(comPort), 38400, 0);
            //multi channel
            rxData = new RxData[maxCh];
            txData = new TxData[maxCh];
            for (int i = 0; i < maxCh; i++) {
                rxData[i] = new RxData();
                txData[i] = new TxData();
                txData[i].setInit();        //초기화
            }
            isOpen = true;
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();

            receiveThread = new Thread(this);
            receiveThread.start();

        } catch (Exception e) {
            logger.error("ControlBoard construct error : {}", e.getMessage());
        }
    }


    /**
     * crc check
     *
     * @param chkData source data
     * @return result = true
     */
    private boolean CheckResponse(byte[] chkData) {
        byte[] crc;
        crc = CRC16.setCrc16ModBus(chkData, 0, chkData.length - 2);
        return Objects.equals(crc[0], chkData[chkData.length - 1]) &&
                Objects.equals(crc[1], chkData[chkData.length - 2]);
    }

    /**
     * receive data validation (checksum)
     *
     * @param srcData receive data
     */
    private void responseReceive(byte[] srcData) {
        if (isOpen) {
            try {
                System.arraycopy(srcData, 0, realReceiveData, 0, RX_DATA_SIZE);
                if (Objects.equals(realReceiveData[1], (byte) 0x04) && CheckResponse(realReceiveData)) {
                    byte currentCh = realReceiveData[0];
                    short dataLength = realReceiveData[2];
                    short[] values = new short[dataLength / 2];
                    short highBit, lowBit;
                    for (int i = 0; i < dataLength / 2; i++) {
                        highBit = realReceiveData[3 + (i * 2)];
                        values[i] = (short) ((highBit << 8) & 0xff00);
                        lowBit = (short) (realReceiveData[4 + (i * 2)] & 0xff);
                        values[i] = (short) (values[i] | lowBit);
                    }
//                    rxData[currentCh-1].Decode(values);
                    rxData[curCh].Decode(values);
                    if (controlBoardListener != null)
                        controlBoardListener.onControlBoardReceive(rxData);
                    Arrays.fill(realReceiveData, (byte) 0x00);
                }
            } catch (Exception e) {
                logger.error("responseReceive error : {}", e.getMessage());
            }
        }
    }

    /**
     * txData request
     *
     * @param deviceId device id
     * @param command  0x04
     * @param start    start address
     * @param register request count
     * @return success : true  fail : false
     */
    private boolean requestSend(byte deviceId, byte command, short start, short register) {
        boolean result = false;
        try {
            if (isOpen) {
                byte[] bytes = new byte[8];
                byte[] crc;
                bytes[0] = deviceId;
                bytes[1] = command;
                bytes[2] = (byte) ((start >> 8) & 0xff);
                bytes[3] = (byte) (start & 0xff);
                bytes[4] = (byte) ((register >> 8) * 0xff);
                bytes[5] = (byte) (register & 0xff);
                crc = CRC16.setCrc16ModBus(bytes, 0, bytes.length - 2);
                bytes[6] = crc[1];
                bytes[7] = crc[0];
                outputStream.write(bytes);
            }
            result = true;
        } catch (Exception e) {
            logger.error("requestSend error : {}", e.getMessage());
        }
        return result;
    }

    /**
     * txData request
     *
     * @param deviceId device id
     * @param command  0x04 command
     * @param start    start address
     * @param register request count
     * @param values   send buffer
     * @return success : true  fail : false
     */
    private boolean requestSend(byte deviceId, byte command, short start, short register, short[] values) {
        boolean result = false;
        try {
            if (isOpen) {
                // header (2byte) + start address (2byte) + data length(2byte) + data + crc(2byte)
                byte[] bytes = new byte[HEAD_SIZE + 2 + 2 + (register * 2) + CRC_SIZE];
                byte[] crc;
                bytes[0] = deviceId;
                bytes[1] = command;
                bytes[2] = (byte) ((start >> 8) & 0xff);
                bytes[3] = (byte) (start & 0xff);
                bytes[4] = (byte) ((register >> 8) & 0xff);
                bytes[5] = (byte) (register & 0xff);
                bytes[6] = (byte) (register * 2);
                for (int i = 0; i < register; i++) {
                    bytes[7 + (2 * i)] = (byte) ((values[i] >> 8) & 0xff);
                    bytes[8 + (2 * i)] = (byte) (values[i] & 0xff);
                }
                crc = CRC16.setCrc16ModBus(bytes, 0, bytes.length - 2);
                bytes[bytes.length - 2] = crc[1];
                bytes[bytes.length - 1] = crc[0];
                outputStream.write(bytes);
            }
            result = true;
        } catch (Exception e) {
            logger.error("requestBufferSend error : {}", e.getMessage());
        }
        return result;
    }

    @Override
    public void run() {
        while (!bEndFlag && !Thread.currentThread().isInterrupted()) {
            try {

                if (tCount++ > 1200) tCount = 0;
                if ((tCount % 2) == 0) {
                    curCh = (curCh + 1) % maxCh;
                    chkTx = requestSend(sendCh[0], (byte) 0x04, curCh == 0 ? (short) 400 : (short) 446, (short) 46);
                } else {
                    rxBuffer200 = txData[curCh].Encode();
                    chkTx = requestSend(sendCh[0], (byte) 0x10, curCh == 0 ? (short) 200 : (short) 210, (short) 10, rxBuffer200);
                    // tx data listener
                    if (controlBoardListener != null)
                        controlBoardListener.onControlBoardSend(txData);
                }
                Thread.sleep(150);
                try {
                    Arrays.fill(receiveData, (byte) 0x00);
                    availableCount = inputStream.available();
                    if (availableCount >= HEAD_SIZE) {
                        int readCount = inputStream.read(receiveData, 0, availableCount);
                    } else {
                        Thread.sleep(10);
                        if (noDataCount < 6000) noDataCount++;
                        if (noDataCount > 50) {
                            disconnected = true;
                        }
                        continue;
                    }
                    switch (receiveData[1]) {
                        case (byte) 0x10:
                            noDataCount = 0;
                            disconnected = false;
                            break;
                        case (byte) 0x04:
                            noDataCount = 0;
                            disconnected = false;
                            responseReceive(receiveData);
                            break;
                        default:
                            if (noDataCount < 6500) noDataCount++;
                            if (noDataCount > 20) disconnected = true;
                            break;
                    }
                    Thread.sleep(150);
                } catch (Exception e) {
                    logger.error("receive error : {}", e.getMessage());
                }
            } catch (Exception e) {
                logger.error(" thread receive error : {} ", e.getMessage());
            }

        }
    }
}
