package com.dongah.fastcharger.websocket.ocpp.utilities;

public abstract class ModelUtil {


    public static boolean isAmong(String needle, String... hatStack) {
        boolean found = false;
        if (hatStack != null) {
            for (String straw : hatStack) {
                if (!(found = isNullOrEqual(straw, needle))) {
                    continue;
                }
                break;
            }
        }
        return found;
    }

    private static boolean isNullOrEqual(Object object1, Object object2) {
        boolean nullOrEqual = false;
        if (object1 == null && object2 == null) {
            nullOrEqual = true;
        } else if (object1 != null && object2 != null) {
            nullOrEqual = object1.equals(object2);
        }
        return nullOrEqual;
    }

    public static boolean validate(String input, int maxLength) {
        return input != null && input.length() <= maxLength;
    }
}
