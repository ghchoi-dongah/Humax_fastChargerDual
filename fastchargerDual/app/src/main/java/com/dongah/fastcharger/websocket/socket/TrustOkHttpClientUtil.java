package com.dongah.fastcharger.websocket.socket;

import android.annotation.SuppressLint;

import com.dongah.fastcharger.basefunction.GlobalVariables;
import com.dongah.fastcharger.websocket.ocpp.security.HashAlgorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.tls.HandshakeCertificates;

public class TrustOkHttpClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(TrustOkHttpClientUtil.class);


    public OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {


            // Create a trust manager that does not validate certificate chains
            @SuppressLint("CustomX509TrustManager") final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };


            /** 인증서 없이 접속 경우 */
            // Install the all-trusting trust manager
////            final SSLContext sslContext = SSLContext.getInstance("TLS");
//            final SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(null, trustAllCerts, new SecureRandom());
//            // Create an ssl socket factory with our all-trusting manager
//            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);


//            File file = new File(GlobalVariables.getRootPath() + File.separator + "dongahtest.p-e.kr.crt");
            File file = new File(GlobalVariables.getRootPath() + File.separator + "cert.pem");

            HandshakeCertificates certificates = null;

            if (file.exists()) {
                X509Certificate certificatePem = loadCertificateFromFile(file);
                // Create a KeyStore containing our trusted CAs
//                String keyStoreType = KeyStore.getDefaultType();
//                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
//                keyStore.load(null, null);

                KeyStore keyStore = KeyStore.getInstance("JKS");
                try (InputStream keyStoreStream = new FileInputStream("mykeystore.jks")) {
                    keyStore.load(keyStoreStream, "dongahelecomm".toCharArray());
                }


                keyStore.setCertificateEntry("ca", certificatePem);
                // Create a TrustManager that trusts the CAs in our KeyStore            S
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);
                // Create an SSLContext that uses our TrustManager
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());
                certificates = new HandshakeCertificates.Builder()
                        .addTrustedCertificate(certificatePem)
                        .build();

                PublicKey publicKey = certificatePem.getPublicKey();
                byte[] encoded = publicKey.getEncoded();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    byte[] b64key = Base64.getEncoder().encode(encoded);
                }

                GlobalVariables.setHashAlgorithm(HashAlgorithm.valueOf(certificatePem.getSigAlgName().substring(0, 6)));
                GlobalVariables.setSerialNumber(certificatePem.getSerialNumber().toString(16).toUpperCase());
                GlobalVariables.setIssuerNameHash(getIssuerName(certificatePem));
                GlobalVariables.setIssuerKeyHash(calculateSHA1(certificatePem.getPublicKey().getEncoded()));

            }

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
//            builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) tmf.getTrustManagers()[0]);
            if (certificates != null) {
                builder.sslSocketFactory(certificates.sslSocketFactory(), certificates.trustManager());
            }


            builder.hostnameVerifier(new HostnameVerifier() {
                @SuppressLint("BadHostnameVerifier")
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private X509Certificate loadCertificateFromFile(File file) throws CertificateException, IOException {
        FileInputStream inputStream = new FileInputStream(file);
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);
        inputStream.close();
        return certificate;
    }

    public String ByteArrayToHex(String value) {
        byte[] targetValue = value.getBytes();
        StringBuilder sb = new StringBuilder();
        for (final byte b : targetValue)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    private String calculateSHA1(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] hash = sha1.digest(data);

        // Convert to hexadecimal string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0'); // Append leading zero
            }
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }


    public String getIssuerName(X509Certificate cert) throws Exception {
        // Get the issuer's DN in DER format
        byte[] issuerDN = cert.getIssuerX500Principal().getEncoded();

        // Hash the DER-encoded DN using SHA-1
        return calculateSHA1(issuerDN);
    }

}

