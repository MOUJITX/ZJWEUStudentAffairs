package com.zjweustudent.server.utils;


import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;


/**
 * AES加解密
 */
public class AesUtils {
    private static Logger log = LoggerFactory.getLogger(AesUtils.class);

    /**
     * 算法名称/加密模式/数据填充方式
     */
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    /**
     * AES加解密默认密钥(16位，也可以用其它长度，那就用 New 方法 )
     */
    //private static final String KEY = "1234567890abcdef";
    private static final String KEY = "zjweu";

    /**
     * AES
     */
    private static final String AES = "AES";


    /**
     * 加密
     * @param content 要加密的字符串
     * @param encryptKey 加密的密钥
     * @return
     * @throws Exception
     */
    public static String encrypt(String content, String encryptKey){
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(AES);
            kgen.init(128);
            Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), AES));
            byte[] b = cipher.doFinal(content.getBytes("utf-8"));
            //采用base64算法进行转码，避免出现中文乱码
            return Base64.encodeBase64String(b);
        }catch (Exception e){
            log.error("encrypt({} , {})加密异常", content, encryptKey, e);
        }

        return null;
    }


    /**
     * 解密
     * @param encryptStr 要解密的字符串
     * @param decryptKey 要解密的密钥
     * @return
     * @throws Exception
     */
    public static  String decrypt(String encryptStr, String decryptKey) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(KEY.getBytes(), AES));

            //采用base64算法进行转码，避免出现中文乱码
            byte[] encryptBytes = Base64.decodeBase64(encryptStr);
            byte[] decryptBytes = cipher.doFinal(encryptBytes);
            return new String(decryptBytes);
        }catch (Exception e){
            log.error("decrypt({} , {})解密异常", encryptStr, decryptKey, e);
        }

        return null;
    }

    /**
     * AES解密方法；数据库里秘钥长度是14，用secureRandom换成了16*8=128的秘钥
     */
    public static  String decryptNew(String decryptStr) {
        String decryptKey = KEY;
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(AES);

            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(decryptKey.getBytes());

            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();

            Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKey.getEncoded(), AES));

            //采用base64算法进行转码，避免出现中文乱码
            byte[] encryptBytes = Base64.decodeBase64(decryptStr);
            byte[] decryptBytes = cipher.doFinal(encryptBytes);
            return new String(decryptBytes);
        }catch (Exception e){
            log.error("decryptNew({} , {})解密异常", decryptStr, decryptKey, e);
            return "0";
        }

        //return null;
    }

    /*加密*/

    public static  String encryptNew(String encryptStr) {
        String encryptKey = KEY;
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(AES);

            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(encryptKey.getBytes());

            kgen.init(128,secureRandom);
            SecretKey secretKey = kgen.generateKey();

            Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey.getEncoded(), AES));
            byte[] b = cipher.doFinal(encryptStr.getBytes("utf-8"));
            //采用base64算法进行转码，避免出现中文乱码
            return Base64.encodeBase64String(b);
        }catch (Exception e){
            log.error("encryptNew({} , {})加密异常", encryptStr, encryptKey, e);
        }

        return null;
    }
}