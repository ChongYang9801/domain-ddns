package com.onon.ddns.patterns;


import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.onon.ddns.util.HardwareAddressUtil;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 创建对称加密
 *
 * @Author onion
 * @Date 2025/12/28 10:15
 **/
public class SymmetricCryptoSingleton {

    private static final AtomicReference<SymmetricCrypto> SYMMETRIC_CRYPTO = new AtomicReference<>();

    public static final SymmetricCrypto getSymmetricCrypto() {
        SymmetricCrypto symmetricCrypto = SYMMETRIC_CRYPTO.get();
        if (symmetricCrypto != null) {
            return symmetricCrypto;
        }
        SYMMETRIC_CRYPTO.set(new SymmetricCrypto(SymmetricAlgorithm.AES, getKey()));
        return SYMMETRIC_CRYPTO.get();
    }

    public static byte[] getKey() {
        // 获取MAC地址
        String hardwareAddress = HardwareAddressUtil.getHardwareAddress();
        // 计算sha256
        String sha256 = DigestUtil.sha256Hex(hardwareAddress);
        // 取前32字节作为密钥
        return SecureUtil.sha256(sha256).substring(0, 32).getBytes();
    }


}
