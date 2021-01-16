package com.microservice.utils;

import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

/**
 * 使用DES加密和解密工具类
 *
 * @author 张伟
 * @date 2020-12-4
 */
public class DesIVUtils {

    private Key key;
    private String DESkey = "12345678";
    private String DESIV = "abcdefgh";

    private AlgorithmParameterSpec iv = null;
    public static final String ALGORITHM = "DES";
    public static final String CIPHER = "DES/CBC/PKCS5Padding";

    public DesIVUtils() {
        try {
            // 设置密钥
            byte[] bytes = DESkey.getBytes(StandardCharsets.UTF_8);
            // 设置密钥参数
            DESKeySpec keySpec = new DESKeySpec(bytes);
            // 设置向量
            byte[] desivBytes = DESIV.getBytes(StandardCharsets.UTF_8);
            iv = new IvParameterSpec(desivBytes);
            // 获得密钥工厂
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            // 得到密钥对象
            key = keyFactory.generateSecret(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 加密String 明文输入密文输出
     *
     * @param inputString 待加密的明文
     * @return 加密后的字符串
     */
    public String getEnc(String inputString) {
        byte[] byteMi = null;
        byte[] byteMing = null;
        String outputString = "";
        try {
            byteMing = inputString.getBytes(StandardCharsets.UTF_8);
            byteMi = this.getEncCode(byteMing);
            byte[] temp = Base64Utils.encode(byteMi);
            outputString = new String(temp);
        } catch (Exception e) {
        } finally {
            byteMing = null;
            byteMi = null;
        }
        return outputString;
    }


    /**
     * 解密String 以密文输入明文输出
     *
     * @param inputString 需要解密的字符串
     * @return 解密后的字符串
     */
    public String getDec(String inputString) {
        byte[] byteMing = null;
        byte[] byteMi = null;
        String strMing = "";
        try {
            byteMi = Base64Utils.decode(inputString.getBytes());
            byteMing = this.getDesCode(byteMi);
            strMing = new String(byteMing, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
        } finally {
            byteMing = null;
            byteMi = null;
        }
        return strMing;
    }


    /**
     * 加密以byte[]明文输入, byte[]密文输出
     *
     * @param bt 待加密的字节码
     * @return 加密后的字节码
     */
    private byte[] getEncCode(byte[] bt) {
        byte[] byteFina = null;
        Cipher cipher;
        try {
            // 得到Cipher实例
            cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byteFina = cipher.doFinal(bt);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cipher = null;
        }
        return byteFina;
    }


    /**
     * 解密以byte[]密文输入, 以byte[]明文输出
     *
     * @param bt 待解密的字节码
     * @return 解密后的字节码
     */
    private byte[] getDesCode(byte[] bt) {
        Cipher cipher;
        byte[] byteFina = null;
        try {
            cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byteFina = cipher.doFinal(bt);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cipher = null;
        }
        return byteFina;
    }
}