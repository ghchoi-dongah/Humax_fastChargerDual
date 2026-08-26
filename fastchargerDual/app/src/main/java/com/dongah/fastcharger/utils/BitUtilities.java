package com.dongah.fastcharger.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitUtilities {

    private static final Logger logger = LoggerFactory.getLogger(BitUtilities.class);

    public static int setBit(int value, int i) {
        return value |= 1 << i;
    }

    public static int clearBit(int value, int i) {
        return value &= ~(1 << i);
    }

    public static long setBit(long value, int i) {
        return value |= 1L << i;
    }

    public static long clearBit(long value, int i) {
        return value &= ~(1L << i);
    }

    public static short setBit(short src, int bitNum, boolean value) {
        if (value) return (short) setBit(src, bitNum);
        else return (short) clearBit(src, bitNum);
    }

    public static boolean getBitBoolean(int value, int i) {
        return (value & (1 << i)) != 0;
    }

    public static int makeInt(byte b3, byte b2, byte b1, byte b0) {
        return (((b3 & 0xff) << 24) |
                ((b2 & 0xff) << 16) |
                ((b1 & 0xff) << 8) |
                ((b0 & 0xff)));
    }

    public static int makeInt(byte b2, byte b1, byte b0) {
        return (((b2 & 0xff) << 16) |
                ((b1 & 0xff) << 8) |
                ((b0 & 0xff)));
    }

    public static int makeInt(byte b1, byte b0) {
        return (((b1 & 0xff) << 8) | ((b0 & 0xff)));
    }

    /**
     * hex array -> String
     */
    public static String byteArrayToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b & 0xff));
        }
        return sb.toString();
    }

    public static byte[] ShortToByteArray(short value) {
        byte[] byteArray = new byte[2];
        try {
            byteArray[0] = (byte) ((value >> 8) & 0xff);
            byteArray[1] = (byte) (value & 0xff);
        } catch (Exception e) {
            logger.error("ShortToByteArray :  {}", e.getMessage());
        }
        return byteArray;
    }

    public static short ByteArrayToShort(byte[] value) {
        short newValue = 0;
        try {
            newValue |= (short) (((value[0]) << 8) & 0xff00);
            newValue |= (short) (((value[1])) & 0xff);
        } catch (Exception e) {
            logger.error("ByteArrayToShort :  {}", e.getMessage());
        }
        return newValue;
    }

    public static short ByteArrayToShort(byte value1, byte value2) {
        short newValue = 0;
        try {
            newValue |= (short) (((value1) << 8) & 0xff00);
            newValue |= (short) (((value2)) & 0xff);
        } catch (Exception e) {
            logger.error("ByteArrayToShort_2 :  {}", e.getMessage());
        }
        return newValue;
    }
}
