package com.onon.ddns.util;

import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.onon.ddns.patterns.SymmetricCryptoSingleton;
import lombok.RequiredArgsConstructor;

/**
 * 加解密工具类
 *
 * @author onion
 * @description
 * @create 2025/12/24 10:11
 */
@RequiredArgsConstructor
public class CryptoUtil {

    private SymmetricCrypto SYMMETRIC_CRYPTO;


    public static void main(String[] args) {
        String encryptBase64 = encrypt("123456");
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
        return SymmetricCryptoSingleton.getSymmetricCrypto()
                .encryptBase64(text);
    }

    /**
     * 解密
     *
     * @param ciphertext 密文
     * @return 明文
     */
    public static String decrypt(String ciphertext) {
        return SymmetricCryptoSingleton.getSymmetricCrypto()
                .decryptStr(ciphertext);
    }


}
