package com.onon.ddns.util;


import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.onon.ddns.config.DdnsConfig;
import lombok.extern.slf4j.Slf4j;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取真实ip地址
 *
 * @Author onion
 * @Date 2025/12/22 20:54
 **/
@Slf4j
public class RealIPUtil {

    /**
     * 获取本机ip地址
     *
     * @return ip地址列表
     */
    public static List<InetAddress> getRealIP() {
        List<InetAddress> inetAddressList = new ArrayList<>();
        // 获取配置
        DdnsConfig ddnsConfig = SpringBeanUtils.getBean(DdnsConfig.class);
        try {
            // 获取到所有的网卡
            List<NetworkInterface> networkInterfaceList = NetworkInterface.networkInterfaces().toList();
            for (NetworkInterface netInterface : networkInterfaceList) {
                if (StrUtil.isNotBlank(ddnsConfig.getFixedNetworkCardMacAddress())) {
                    // 筛选用户指定的网卡
                    if (ddnsConfig.getFixedNetworkCardMacAddress()
                            .equalsIgnoreCase(HardwareAddressUtil.byteToStr(netInterface.getHardwareAddress()))) {
                        netInterface.inetAddresses().filter(ObjUtil::isNotNull)
                                .forEach(inetAddressList::add);
                    }
                    continue;
                }
                // 去除回环接口127.0.0.1，子接口，未运行的接口
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                }
                // 获取名称中是否包含 Intel Realtek 的网卡
                if (!netInterface.getDisplayName().contains("Intel")
                        && !netInterface.getDisplayName().contains("Realtek")
                        && !netInterface.getDisplayName().contains("Atheros")
                        && !netInterface.getDisplayName().contains("Broadcom")) {
                    continue;
                }
                List<InetAddress> addresses = netInterface.inetAddresses().toList();
                for (InetAddress ip : addresses) {
                    if (ip != null) {
                        inetAddressList.add(ip);
                    }
                }
            }
        } catch (SocketException e) {
            log.error("获取ip异常，错误信息：{}", e.getMessage());
        }
        return filterIPAddresses(inetAddressList);
    }

    /**
     * 过滤ip地址，同种类型ip地址只取第一个
     *
     * @return ip地址列表
     */
    public static List<InetAddress> filterIPAddresses(List<InetAddress> ipAddressList) {
        List<InetAddress> ipAddress = new ArrayList<>();
        // 过滤出第一个ipv4和ipv6地址
        ipAddressList.stream().filter(inetAddress -> inetAddress instanceof Inet4Address)
                .findFirst().ifPresent(ipAddress::add);
        ipAddressList.stream().filter(inetAddress -> {
                    if (inetAddress instanceof Inet6Address inet6Address) {
                        // 忽略 fe80 开头的ipv6地址
                        return !inet6Address.getHostAddress().startsWith("fe80");
                    }
                    return false;
                })
                .findFirst().ifPresent(ipAddress::add);
        return ipAddress;
    }

    public static void main(String[] args) {
        List<InetAddress> realIP = RealIPUtil.getRealIP();
        for (InetAddress inetAddress : realIP) {
            System.out.println(inetAddress.getHostAddress());
        }
    }
}
