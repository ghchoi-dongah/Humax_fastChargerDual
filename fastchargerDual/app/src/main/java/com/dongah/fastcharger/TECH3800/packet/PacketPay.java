package com.dongah.fastcharger.TECH3800.packet;

import com.dongah.fastcharger.TECH3800.ConvertDataType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketPay {

    private static final Logger logger = LoggerFactory.getLogger(PacketPay.class);

    ConvertDataType convertDataType;

    // header data
    byte STX;
    char[] terminalID;
    char[] dateTime;
    char jobCode;
    byte responseCode;
    short dataLength;
    //body
    char tradeCode;
    char[] prePayment;
    char[] surTax;
    char[] tip;
    char[] installment;
    char sign;

    public PacketPay() {
        convertDataType = new ConvertDataType();

        //initialize
        STX = (byte) 0x02;
        terminalID = new char[16];
        dateTime = new char[14];
        jobCode = (char) 0x00;
        responseCode = (byte) 0x00;
        dataLength = 0;
        tradeCode = (char) 0x31;        //승인
        prePayment = new char[10];
        surTax = new char[8];
        tip = new char[8];
        installment = new char[2];      //할부 개월
        sign = (char) 0x31;             // 비서명
        //
    }

    public byte[] onPayDataSet() {
        try {
            byte[] result = new byte[67];
            result[0] = getSTX();
            System.arraycopy(convertDataType.CharArrayToByteArray(getTerminalID()), 0, result, 1, 16);
            System.arraycopy(convertDataType.CharArrayToByteArray(getDateTime()), 0, result, 17, 14);
            result[31] = (byte) getJobCode();
            result[32] = (byte) 0x00;
            //body data length
            System.arraycopy(convertDataType.ShortToByteArrayLittle(getDataLength()), 0, result, 33, 2);
            // data body
            result[35] = (byte) getTradeCode();
            System.arraycopy(convertDataType.CharArrayToByteArray(getPrePayment()), 0, result, 36, 10);
            System.arraycopy(convertDataType.CharArrayToByteArray(getSurTax()), 0, result, 46, 8);
            System.arraycopy(convertDataType.CharArrayToByteArray(getTip()), 0, result, 54, 8);
            System.arraycopy(convertDataType.CharArrayToByteArray(getInstallment()), 0, result, 62, 2);
            result[64] = (byte) getSign();
            //tail
            result[65] = (byte) 0x03;
            byte xor = 0x00;
            for (int i = 0; i < 66; i++) {
                xor ^= result[i];
            }
            result[66] = xor;
            return result;
        } catch (Exception e) {
            logger.error(" onPayDataSet error : {} ", e.getMessage());
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

    public char getTradeCode() {
        return tradeCode;
    }

    public void setTradeCode(char tradeCode) {
        this.tradeCode = tradeCode;
    }

    public char[] getPrePayment() {
        return prePayment;
    }

    public void setPrePayment(char[] prePayment) {
        this.prePayment = prePayment;
    }

    public char[] getSurTax() {
        return surTax;
    }

    public void setSurTax(char[] surTax) {
        this.surTax = surTax;
    }

    public char[] getTip() {
        return tip;
    }

    public void setTip(char[] tip) {
        this.tip = tip;
    }

    public char[] getInstallment() {
        return installment;
    }

    public void setInstallment(char[] installment) {
        this.installment = installment;
    }

    public char getSign() {
        return sign;
    }

    public void setSign(char sign) {
        this.sign = sign;
    }
}
