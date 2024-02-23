package com.mario.lib.base.util.security;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Created by HappyMario on 1/12/2018.
 */

public class CryptoUtil {
    private static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * URL Encoder
     *
     * @param str
     * @return
     */
    public static String urlEncoder(String str) {
        String result = null;
        try {
            result = URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String mapToQueryString(Map<String, String> map) {
        StringBuilder string = new StringBuilder();

        if (map.size() > 0) {
            string.append("?");
        }

        for (Map.Entry<String, String> entry : map.entrySet()) {
            string.append(entry.getKey());
            string.append("=");
            string.append(entry.getValue());
            string.append("&");
        }

        return string.toString();
    }

    public static String rpad(String strSrc, int length, String strPad) {
        byte[] bytes = strSrc.getBytes();
        int len = bytes.length;
        int nTemp = length - len;
        for (int i = 0; i < nTemp; i++) {
            strSrc += strPad;
        }
        return strSrc;
    }

    private final static String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

    private static byte[] DecodeBase58(String input, int base, int len) {
        byte[] output = new byte[len];
        for (int i = 0; i < input.length(); i++) {
            char t = input.charAt(i);

            int p = ALPHABET.indexOf(t);
            if (p == -1) return null;
            for (int j = len - 1; j > 0; j--, p /= 256) {
                p += base * (output[j] & 0xFF);
                output[j] = (byte) (p % 256);
            }
            if (p != 0) return null;
        }

        return output;
    }

    public static String hash256(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());
        return bytesToHex(md.digest());
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte byt : bytes)
            result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }

    /**
     * MyBase64 인코딩
     */
    public static String getBase64encode(String content) {
        return Base64.encodeToString(content.getBytes(), 0);
    }

    /**
     * MyBase64 디코딩
     */
    public static String getBase64decode(String content) {
        return new String(Base64.decode(content, 0));
    }

    /**
     * getURLEncode
     */
    public static String getURLEncode(String content) {
        try {
//          return URLEncoder.encode(content, "utf-8");   // UTF-8
            return URLEncoder.encode(content, "euc-kr");  // EUC-KR
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * getURLDecode
     */
    public static String getURLDecode(String content) {
        try {
//          return URLDecoder.decode(content, "utf-8");   // UTF-8
            return URLDecoder.decode(content, "euc-kr");  // EUC-KR
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    //zzdating20190827enc
    private static byte[] realKey = {'z', 'z', 'd', 'a', 't', 'i', 'n', 'g', '2', '0', '1', '9', '0', '8', '2', '7', 'e', 'n', 'c'};


    public static String encrypt(String SourceData) {

        if (SourceData == null || SourceData.length() < 1)
            return "";

        try {


            int count = 0;
            int length = SourceData.getBytes().length;
            byte[] EncryptData = new byte[length];
            int keylen = realKey.length;

            for (int i = 0; i < length; i++) {
                if (count == keylen) {
                    count = 0;
                }
                EncryptData[i] = (byte) (SourceData.getBytes()[i] ^ realKey[count]);
                count++;
            }

            String out = MyBase64.encode(EncryptData);
//        Log.d("enc:", enc + "\n");
            //Log.d("enc:", out);
            return out;

        } catch (Exception e) {

        }
        return "";
    }

    public static String decrypt(String encString) {
        if (encString == null || encString.length() < 1)
            return "";


        byte[] EncryptedData;
        try {
            String sss = encString.trim();
            EncryptedData = MyBase64.decode(sss.getBytes("UTF-8"));

            byte[] Decrypt = new byte[EncryptedData.length];

            int count = 0;
            int length = EncryptedData.length;
            int keylen = realKey.length;

            for (int i = 0; i < length; i++) {
                if (count == keylen) {
                    count = 0;
                }

                Decrypt[i] = (byte) (EncryptedData[i] ^ realKey[count]);
                count++;
            }

            String out = new String(Decrypt, "UTF-8");
            //Log.d("dec:", out);
            return out;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Base64DecoderException e) {
            e.printStackTrace();
        }
        return "";

    }
}
