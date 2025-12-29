package com.onon.ddns.service;


import java.util.Map;

/**
 * 域名服务商客户端
 *
 * @Author onion
 * @Date 2025/12/28 09:12
 **/
public interface DomainNameClientService<T> {

    /**
     * 初始化域名服务商客户端
     *
     * @return 服务商客户端
     */
    Map<String, T> initDomainNameServiceClient();

}
