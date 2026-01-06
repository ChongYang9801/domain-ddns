package com.onon.ddns.util;

import lombok.RequiredArgsConstructor;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

/**
 * 加解密工具类
 *
 * @author onion
 * @description
 * @create 2025/12/24 10:11
 */
@RequiredArgsConstructor
public class CryptoUtil {


    public static void main(String[] args) {
        String encryptBase64 = encrypt("2Cswha7ha3fW1Bf1jkTIkaZ2G8GTtc");
        System.out.println("密文" + encryptBase64);
        String decrypt = decrypt(encryptBase64);
        System.out.println("明文" + decrypt);
    }

    /**
     * 加密
     *
     * @param text 明文
     * @return 密文
     */
    public static String encrypt(String text) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            // 生成随机 IV
            byte[] iv = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec keySpec = new SecretKeySpec(getKey(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] ciphertext = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            // 拼接 IV + 密文，并转为 Base64 字符串
            byte[] combined = new byte[16 + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, 16);
            System.arraycopy(ciphertext, 0, combined, 16, ciphertext.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解密
     *
     * @param ciphertext 密文
     * @return 明文
     */
    public static String decrypt(String ciphertext) {
        // 从 Base64 字符串还原完整数据
        byte[] combined = java.util.Base64.getDecoder().decode(ciphertext);
        // 提取 IV 和密文
        byte[] iv = Arrays.copyOfRange(combined, 0, 16);
        byte[] ciphertextByte = Arrays.copyOfRange(combined, 16, combined.length);
        // 解密
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(getKey(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decrypted = cipher.doFinal(ciphertextByte);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                 InvalidAlgorithmParameterException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 获取aes加密key
     *
     * @return 密钥
     */
    public static byte[] getKey() {
        // 获取MAC地址
        String hardwareAddress = HardwareAddressUtil.getHardwareAddress();
        // 取前32字节作为密钥
        return calculateSHA256Hash(hardwareAddress).substring(0, 32).getBytes();
    }

    /**
     * 计算SHA-256哈希值
     *
     * @param input 输入字符串
     * @return SHA-256哈希值
     */
    public static String calculateSHA256Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


}
