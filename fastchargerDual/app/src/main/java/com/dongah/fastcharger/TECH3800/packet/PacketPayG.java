package com.dongah.fastcharger.TECH3800.packet;

import android.util.Log;

import com.dongah.fastcharger.TECH3800.ConvertDataType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class PacketPayG {
    private static final Logger logger = LoggerFactory.getLogger(PacketPayG.class);

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
    char[] approvalAmount;
    char[] surTax;
    char[] tip;
    char[] installment;
    char[] buyerName;
    char[] telNumber;
    char[] emailAddress;
    char[] buyerContact;
    char[] productName;
    char[] address;
    char[] recipientMessage;

    public PacketPayG() {
        convertDataType = new ConvertDataType();
        //initialize
        STX = (byte) 0x00;
        terminalID = new char[16];
        dateTime = new char[14];
        jobCode = (char) 0x20;
        responseCode = (byte) 0x20;
        dataLength = 0;
        // body
        tradeCode = (char) 0x31;
        approvalAmount = new char[10];
        surTax = new char[8];
        tip = new char[8];
        installment = new char[2];
        buyerName = new char[30];
        telNumber = new char[20];
        emailAddress = new char[40];
        buyerContact = new char[20];
        productName = new char[50];
        address = new char[100];
        recipientMessage = new char[50];
    }


    public byte[] onPayGDataSet() {
        try {
            byte[] result = new byte[376];
            result[0] = getSTX();
            System.arraycopy(convertDataType.CharArrayToByteArray(getTerminalID()), 0, result, 1, 16);
            System.arraycopy(convertDataType.CharArrayToByteArray(getDateTime()), 0, result, 17, 14);
            result[31] = (byte) getJobCode();
            result[32] = (byte) 0x00;
            //body data length
            System.arraycopy(convertDataType.ShortToByteArrayLittle(getDataLength()), 0, result, 33, 2);
            // data body
            result[35] = (byte) 0x31;
            System.arraycopy(convertDataType.CharArrayToByteArray(getApprovalAmount()), 0, result, 36, 10);
            System.arraycopy(convertDataType.CharArrayToByteArray(getSurTax()), 0, result, 46, 8);
            System.arraycopy(convertDataType.CharArrayToByteArray(getTip()), 0, result, 54, 8);
            System.arraycopy(convertDataType.CharArrayToByteArray(getInstallment()), 0, result, 62, 2);
            Arrays.fill(buyerName, ' ');
            System.arraycopy(convertDataType.CharArrayToByteArray(getBuyerName()), 0, result, 64, 30);
            Arrays.fill(telNumber, ' ');
            System.arraycopy(convertDataType.CharArrayToByteArray(getTelNumber()), 0, result, 94, 20);
            Arrays.fill(emailAddress, ' ');
            System.arraycopy(convertDataType.CharArrayToByteArray(getEmailAddress()), 0, result, 114, 40);
            Arrays.fill(buyerContact, ' ');
            System.arraycopy(convertDataType.CharArrayToByteArray(getBuyerContact()), 0, result, 154, 20);
            Arrays.fill(productName, ' ');
            System.arraycopy(convertDataType.CharArrayToByteArray(getProductName()), 0, result, 174, 50);
            Arrays.fill(address, ' ');
            System.arraycopy(convertDataType.CharArrayToByteArray(getAddress()), 0, result, 224, 100);
            Arrays.fill(recipientMessage, ' ');
            System.arraycopy(convertDataType.CharArrayToByteArray(getRecipientMessage()), 0, result, 324, 50);
            //tail
            result[374] = (byte) 0x03;
            byte xor = 0x00;
            for (int i = 0; i < 375; i++) {
                xor ^= result[i];
            }
            result[375] = xor;
            Log.d("DONAGH", convertDataType.byteArrayToString(result));
            return result;
        } catch (Exception e) {
            logger.error("onPayGDataSet error : {} ", e.getMessage());
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

    public char[] getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(char[] buyerName) {
        this.buyerName = buyerName;
    }

    public char[] getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(char[] telNumber) {
        this.telNumber = telNumber;
    }

    public char[] getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(char[] emailAddress) {
        this.emailAddress = emailAddress;
    }

    public char[] getBuyerContact() {
        return buyerContact;
    }

    public void setBuyerContact(char[] buyerContact) {
        this.buyerContact = buyerContact;
    }

    public char[] getProductName() {
        return productName;
    }

    public void setProductName(char[] productName) {
        this.productName = productName;
    }

    public char[] getAddress() {
        return address;
    }

    public void setAddress(char[] address) {
        this.address = address;
    }

    public char[] getRecipientMessage() {
        return recipientMessage;
    }

    public void setRecipientMessage(char[] recipientMessage) {
        this.recipientMessage = recipientMessage;
    }
}
