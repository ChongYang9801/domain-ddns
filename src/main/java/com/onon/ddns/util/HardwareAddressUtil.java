package com.onon.ddns.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;

/**
 * 获取硬件地址，通常是MAC地址
 *
 * @author onion
 * @description 获取硬件地址，通常是MAC地址
 * @create 2025/12/23 14:12
 */
@Slf4j
public class HardwareAddressUtil {

    private static String MAC_ADDRESS = null;

    /**
     * 获取MAC地址
     *
     * @return MAC地址
     */
    public static String getHardwareAddress() {
        if (StrUtil.isNotBlank(MAC_ADDRESS)) {
            return MAC_ADDRESS;
        }
        // 获取ip地址
        List<InetAddress> addressList = RealIPUtil.getRealIP();
        if (CollUtil.isNotEmpty(addressList)) {
            InetAddress inetAddress = addressList.get(0);
            try {
                NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
                byte[] mac = networkInterface.getHardwareAddress();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                }
                MAC_ADDRESS = sb.toString();
                return sb.toString();
            } catch (SocketException e) {
                log.error("获取mac异常，错误信息：{}", e.getMessage());
            }
        }
        return "";
    }

}
