package com.microservice.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;
import java.util.Objects;

/**
 * @author zw
 * @date 2020-12-04
 * <p>
 */
@Slf4j
public class DesUtils {

    //算法密匙
    private static final byte[] DES_KEY = {11, 21, 1, -110, 82, -32, -85, -128, -65, 44, -2};

    /**
     * 数据加密，算法（DES）
     *
     * @param data 要进行加密的数据
     * @return 加密后的数据
     */
    public static String encryptBasedDes(String data, byte[] keyBytes) {
        String encryptedData = null;
        try {
            // DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();
            DESKeySpec deskey = new DESKeySpec(Objects.isNull(keyBytes) ? DES_KEY : keyBytes);
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(deskey);
            // 加密对象
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key, sr);
            // 加密，并把字节数组编码成字符串
            encryptedData = new sun.misc.BASE64Encoder().encode(cipher.doFinal(data.getBytes()));
        } catch (Exception e) {
            log.error("加密错误，错误信息：", e);
        }
        return encryptedData;
    }

    /**
     * 解密
     *
     * @param cryptData
     * @return
     */
    public static String decryptBasedDes(String cryptData, byte[] keyBytes) {
        String decryptedData = null;
        try {
            // DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();
            DESKeySpec deskey = new DESKeySpec(Objects.isNull(keyBytes) ? DES_KEY : keyBytes);
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(deskey);
            // 解密对象
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key, sr);
            // 把字符串解码为字节数组，并解密
            decryptedData = new String(cipher.doFinal(new sun.misc.BASE64Decoder().decodeBuffer(cryptData)));
        } catch (Exception e) {
            log.error("解密错误，错误信息：", e);
            throw new RuntimeException("解密错误，错误信息：", e);
        }
        return decryptedData;
    }

    public static void main(String[] args) throws Exception {
//        String key = new String(DES_KEY, StandardCharsets.UTF_8);
//        String key = "aaaaaaaa";
//        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
//        System.out.println("密钥" + key);
//
//        String str1 = "12014-11-15";
//        // DES数据加密
//        String s1 = encryptBasedDes(str1, keyBytes);
//        System.out.println("加密后" + s1);
//
//        // DES数据解密
//        String s2 = decryptBasedDes(s1, keyBytes);
//        System.err.println("解密后" + s2);


//        String inputStr = "12014-11-15";
//        String key = DESCoder.initKey("abcdefgh");
//        System.err.println("原文:" + inputStr);
//
//        System.err.println("密钥:" + key);
//
//        byte[] inputData = inputStr.getBytes();
//        inputData = DESCoder.encrypt(inputData, key);
//
//        String encryptBASE64 = DESCoder.encryptBASE64(inputData);
//        System.err.println("加密后:" + encryptBASE64);
//
//        byte[] outputData = DESCoder.decrypt(inputData, key);
//        String outputStr = new String(outputData);
//
//        System.err.println("解密后:" + outputStr);

        String inputStr = "张伟的测试";

        DesIVUtils DesIVUtils = new DesIVUtils();
        String enc = DesIVUtils.getEnc(inputStr);
        System.err.println("加密后:" + enc);

        String dec = DesIVUtils.getDec(enc);
        System.err.println("解密后:" + dec);
    }
}
