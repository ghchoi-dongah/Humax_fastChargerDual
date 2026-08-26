package com.dongah.fastcharger.websocket.ocpp.utilities;

public class StringSpaceFormat {


    public String padLeftSpace(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append(' ');
        }
        sb.append(inputString);
        return sb.toString();
    }

    public String padLeftChar(String inputString, int length, String delimiter) {
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
     * 1234560000000
     *
     * @param inputString
     * @param length
     * @return
     */
    public String padRightChar(String inputString, int length, String delimiter) {
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

    public String spaceFill(int length, String delimiter) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length) {
            sb.append(delimiter);
        }
        return sb.toString();
    }


}
