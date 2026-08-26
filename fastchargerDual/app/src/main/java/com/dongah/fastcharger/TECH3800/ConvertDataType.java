package com.dongah.fastcharger.TECH3800;

import androidx.annotation.NonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConvertDataType {

    private static final Logger logger = LoggerFactory.getLogger(ConvertDataType.class);

    /**
     * 000000012345
     *
     * @param inputString source
     * @param length      length
     * @param delimiter   0
     * @return 000000012345
     */
    public String padLeftChar(@NonNull String inputString, int length, String delimiter) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append(delimiter);
        }
        sb.append(inputString);
        return sb.toString();
    }

    /**
     * @param inputString source data
     * @param length      length
     * @param delimiter   0
     * @return 1234560000000
     */
    public String padRightChar(@NonNull String inputString, int length, String delimiter) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(inputString);
        while (sb.length() < length) {
            sb.append(delimiter);
        }
        return sb.toString();
    }

    /**
     * @param inputString source data
     * @param length      length
     * @param delimiter   0
     * @return 000000123456
     */
    public char[] padRightChar(@NonNull String inputString, int length, char delimiter) {
        char[] result = new char[length];
        int currentLength = inputString.length();
        ConvertDataType convertDataType = new ConvertDataType();
        if (length != currentLength) {
            System.arraycopy(inputString.toCharArray(), 0, result, 0, currentLength);
        }

        for (int i = 0; i < (length - currentLength); i++) {
            result[currentLength + i] = delimiter;
        }
        return result;
    }


    /**
     * @param value1 high value
     * @param value2 low value
     * @return short
     */
    public short ByteArrayToShort(byte value1, byte value2) {
        short newValue = 0;
        newValue |= (short) ((((short) value1) << 8) & 0xff00);
        newValue |= (short) ((((short) value2)) & 0xff);
        return newValue;
    }

    /**
     * char array -> byte array
     *
     * @param value char array
     * @return byte array
     */
    public byte[] CharArrayToByteArray(char[] value) {
        if (value == null) return null;
        byte[] byteArray = new byte[value.length];
        for (int i = 0; i < value.length; i++) {
            byteArray[i] = (byte) (value[i]);
        }
        return byteArray;
    }

    public byte[] ShortToByteArray(short value) {
        byte[] byteArray = new byte[2];
        byteArray[0] = (byte) ((value >> 8) & 0xff);
        byteArray[1] = (byte) (value & 0xff);
        return byteArray;
    }

    public byte[] ShortToByteArrayLittle(short value) {
        byte[] byteArray = new byte[2];
        byteArray[0] = (byte) (value & 0xff);
        byteArray[1] = (byte) ((value >> 8) & 0xff);
        return byteArray;
    }


    /**
     * byte array -> char array
     *
     * @param value byte array
     * @return char array
     */
    public char[] ByteArrayToCharArray(byte[] value) {
        if (value == null) return null;
        char[] chars = new char[value.length];
        for (int i = 0; i < value.length; i++) {
            chars[i] = (char) value[i];
        }
        return chars;
    }


    public String byteArrayToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b & 0xff));
        }
        return sb.toString();
    }


}
