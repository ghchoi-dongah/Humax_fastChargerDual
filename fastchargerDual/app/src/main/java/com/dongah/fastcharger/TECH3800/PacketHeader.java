package com.dongah.fastcharger.TECH3800;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketHeader {

    private static final Logger logger = LoggerFactory.getLogger(PacketHeader.class);


    // 1. Packet의 구조는 기본적으로 Header + Data + Tail로 이루어진다
    // 2. Header 및 Tail의 고정길이이며 Data는 가변길이로 이루어진다.
    // 3. Header 구성[35BYTE]


    ConvertDataType convertDataType;


    byte STX;
    char[] terminalID;
    char[] dateTime;
    char jobCode;
    byte responseCode;
    short dataLength;


    public PacketHeader() {
        convertDataType = new ConvertDataType();

        //initialize
        STX = (byte) 0x00;
        terminalID = new char[16];
        dateTime = new char[14];
        jobCode = (char) 0x00;
        responseCode = (byte) 0x00;
        dataLength = 0;
    }

    public byte[] onHeaderDataSet() {
        try {
            byte[] result = new byte[37];
            result[0] = (byte) 0x02;
            System.arraycopy(convertDataType.CharArrayToByteArray(getTerminalID()), 0, result, 1, 16);
            System.arraycopy(convertDataType.CharArrayToByteArray(getDateTime()), 0, result, 17, 14);
            result[31] = (byte) getJobCode();
            result[32] = (byte) 0x00;
            System.arraycopy(convertDataType.ShortToByteArray(getDataLength()), 0, result, 33, 2);
            result[35] = (byte) 0x03;

            byte xor = 0x00;
            for (int i = 0; i < 36; i++) xor ^= result[i];
            result[36] = xor;

            return result;
        } catch (Exception e) {
            logger.error("PacketHeader error : {}", e.getMessage());
        }
        return null;
    }

    public byte getSTX() {
        return STX;
    }

    public void setSTX(byte STX) {
        this.STX = STX;
    }

    public char[] getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(char[] terminalID) {
        this.terminalID = terminalID;
    }

    public char[] getDateTime() {
        return dateTime;
    }

    public void setDateTime(char[] dateTime) {
        this.dateTime = dateTime;
    }

    public char getJobCode() {
        return jobCode;
    }

    public void setJobCode(char jobCode) {
        this.jobCode = jobCode;
    }

    public byte getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(byte responseCode) {
        this.responseCode = responseCode;
    }

    public short getDataLength() {
        return dataLength;
    }

    public void setDataLength(short dataLength) {
        this.dataLength = dataLength;
    }
}
