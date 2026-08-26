package com.dongah.fastcharger.websocket.socket;
import android.annotation.SuppressLint;
import android.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class TripleDES {


    public static final Logger logger = LoggerFactory.getLogger(TripleDES.class);

    private static final String SECRET_KEY = "hmev24_Ocpp_Key_18003188"; // 반드시 24바이트
    private static final String ALGORITHM = "DESede";
    private static final String TRANSFORMATION = "DESede/ECB/PKCS5Padding";

    public TripleDES() {
    }



    public String encrypt(String plainText) throws Exception {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        // 반드시 DESede로 지정해야 함
        SecretKey key = new SecretKeySpec(keyBytes, ALGORITHM);
        @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(encrypted, Base64.NO_WRAP);

    }



    public String decrypt(String cipherText) throws Exception {
        Key key = generateKey();
        @SuppressLint("GetInstance")
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(Base64.decode(cipherText, Base64.NO_WRAP));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    private Key generateKey() throws Exception {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        DESedeKeySpec keySpec = new DESedeKeySpec(keyBytes);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        return keyFactory.generateSecret(keySpec);
    }

}