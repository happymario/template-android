package com.mario.lib.base.util.security;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by kakoapps on 2016-01-21.
 */
public class SecurityAes {

    public static String encrypt(String input, String key) {
        byte[] crypted = null;

        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(input.getBytes());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return new String(Base64.encodeToString(crypted, 0));
    }


    public static String decrypt(String input, String key) {
        byte[] output = null;
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            output = cipher.doFinal(Base64.decode(input, 0));
        } catch (Exception e) {
            System.out.println(e.toString());
            return "";
        }
        return new String(output);
    }

}
