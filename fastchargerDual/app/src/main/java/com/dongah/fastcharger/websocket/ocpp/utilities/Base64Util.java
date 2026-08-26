package com.dongah.fastcharger.websocket.ocpp.utilities;

import android.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Base64Util {

    private static final Logger logger = LoggerFactory.getLogger(Base64Util.class);

    /**
     * Base64 인코딩
     *
     * @param text input string
     * @return String
     * @throws UnsupportedEncodingException
     */
    public String encode(String text) throws UnsupportedEncodingException {
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    /**
     * Base64 인코딩
     *
     * @param digest input byte
     * @return
     * @throws UnsupportedEncodingException
     */
    public String encode(byte[] digest) throws UnsupportedEncodingException {
        return Base64.encodeToString(digest, Base64.NO_WRAP);
    }

    /**
     * Base64 디코딩
     *
     * @param text input String
     * @return
     * @throws UnsupportedEncodingException
     */
    public String decode(String text) throws UnsupportedEncodingException {
        return new String(Base64.decode(text, Base64.NO_WRAP), StandardCharsets.UTF_8);
    }

    public String getURLEncode(String content) {
        try {
            return URLEncoder.encode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public String getURLDecode(String content) {
        try {
            return URLDecoder.decode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
        return null;
    }
}
