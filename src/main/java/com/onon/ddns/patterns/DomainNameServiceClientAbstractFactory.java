package com.onon.ddns.patterns;


import com.onon.ddns.service.DomainNameClientService;

/**
 * 域名服务商客户端抽象工厂
 *
 * @Author onion
 * @Date 2025/12/28 09:37
 **/
public abstract class DomainNameServiceClientAbstractFactory {

    /**
     * 获取域名服务商客户端
     *
     * @return 域名服务商客户端
     */
    public abstract DomainNameClientService getDomainNameServiceClient();

}
