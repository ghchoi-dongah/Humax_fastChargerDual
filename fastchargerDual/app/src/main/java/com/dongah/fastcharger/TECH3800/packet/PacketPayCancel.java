package com.dongah.fastcharger.TECH3800.packet;

import android.util.Log;

import com.dongah.fastcharger.TECH3800.ConvertDataType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketPayCancel {

    private static final Logger logger = LoggerFactory.getLogger(PacketPayCancel.class);

    ConvertDataType convertDataType;

    /**
     * “4”[PG 무카드 취소], “5”[PG 부분 취소]
     */
    public static final char PAY_CANCEL = '4';
    public static final char PAY_CANCEL_G = '5';

    int cancelType;
    // header data
    byte STX;
    char[] terminalID;
    char[] dateTime;
    char jobCode;
    byte responseCode;
    short dataLength;
    //body
    char cancelCode;
    char tradeCode;
    char[] approvalAmount;
    char[] surTax;
    char[] tip;
    char[] installment;
    char sign;
    char[] approvalNumber;
    char[] tradeDate;
    char[] tradeTime;
    char[] addInfoLength;
    char[] addInfo;


    public PacketPayCancel(int cancelType) {
        this.cancelType = cancelType;
        convertDataType = new ConvertDataType();
        //initialize
        STX = (byte) 0x00;
        terminalID = new char[16];
        dateTime = new char[14];
        jobCode = '0';
        responseCode = (byte) 0x00;
        dataLength = 0;
        cancelCode = (char) 0x34;
        tradeCode = (char) 0x32;
        approvalAmount = new char[10];
        surTax = new char[8];
        tip = new char[8];
        installment = new char[2];
        sign = (char) 0x31;             // 비서명
        approvalNumber = new char[12];
        tradeDate = new char[8];
        tradeTime = new char[6];
        addInfoLength = new char[2];
        addInfo = new char[30];

    }

    /**
     * 부가 정보는  없음....(부분 취소 X)
     *
     * @return byte[] 취소 내역
     */
    public byte[] onPayCancelDataSet() {
        try {
            byte[] result = new byte[126];
            byte xor;
            result[0] = getSTX();
            System.arraycopy(convertDataType.CharArrayToByteArray(getTerminalID()), 0, result, 1, 16);
            System.arraycopy(convertDataType.CharArrayToByteArray(getDateTime()), 0, result, 17, 14);
            result[31] = (byte) getJobCode();
            result[32] = (byte) 0x00;
            //body data length
            System.arraycopy(convertDataType.ShortToByteArrayLittle(getDataLength()), 0, result, 33, 2);
            // data body
            result[35] = (byte) getCancelCode();
            result[36] = (byte) getTradeCode();
            System.arraycopy(convertDataType.CharArrayToByteArray(getApprovalAmount()), 0, result, 37, 10);
            System.arraycopy(convertDataType.CharArrayToByteArray(getSurTax()), 0, result, 47, 8);
            System.arraycopy(convertDataType.CharArrayToByteArray(getTip()), 0, result, 55, 8);
            System.arraycopy(convertDataType.CharArrayToByteArray(getInstallment()), 0, result, 63, 2);
            result[65] = (byte) getSign();
            System.arraycopy(convertDataType.CharArrayToByteArray(getApprovalNumber()), 0, result, 66, 12);
            System.arraycopy(convertDataType.CharArrayToByteArray(getTradeDate()), 0, result, 78, 8);
            System.arraycopy(convertDataType.CharArrayToByteArray(getTradeTime()), 0, result, 86, 6);
            System.arraycopy(convertDataType.CharArrayToByteArray(getAddInfoLength()), 0, result, 92, 2);
            System.arraycopy(convertDataType.CharArrayToByteArray(getAddInfo()), 0, result, 94, 30);
            result[124] = (byte) 0x03;
            xor = 0x00;
            for (int i = 0; i < 125; i++) xor ^= result[i];
            result[125] = xor;

            Log.d("DONAGH", "--" + convertDataType.byteArrayToString(result));
            return result;
        } catch (Exception e) {
            logger.error("onPayCancelDataSet error : {}", e.getMessage());
        }
        return null;
    }

    public int getCancelType() {
        return cancelType;
    }

    public void setCancelType(int cancelType) {
        this.cancelType = cancelType;
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

    public char getCancelCode() {
        return cancelCode;
    }

    public void setCancelCode(char cancelCode) {
        this.cancelCode = cancelCode;
    }

    public char getTradeCode() {
        return tradeCode;
    }

    public void setTradeCode(char tradeCode) {
        this.tradeCode = tradeCode;
    }

    public char[] getApprovalAmount() {
        return approvalAmount;
    }

    public void setApprovalAmount(char[] approvalAmount) {
        this.approvalAmount = approvalAmount;
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

    public char[] getApprovalNumber() {
        return approvalNumber;
    }

    public void setApprovalNumber(char[] approvalNumber) {
        this.approvalNumber = approvalNumber;
    }

    public char[] getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(char[] tradeDate) {
        this.tradeDate = tradeDate;
    }

    public char[] getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(char[] tradeTime) {
        this.tradeTime = tradeTime;
    }

    public char[] getAddInfoLength() {
        return addInfoLength;
    }

    public void setAddInfoLength(char[] addInfoLength) {
        this.addInfoLength = addInfoLength;
    }

    public char[] getAddInfo() {
        return addInfo;
    }

    public void setAddInfo(char[] addInfo) {
        this.addInfo = addInfo;
    }

}
