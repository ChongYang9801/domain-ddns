package com.onon.ddns.patterns;


import com.onon.ddns.service.DomainNameClientService;
import com.onon.ddns.service.impl.AliyunClientServiceImpl;

/**
 * 域名服务商客户端工厂
 *
 * @Author onion
 * @Date 2025/12/28 09:47
 **/
public class DomainNameServiceClientFactory extends DomainNameServiceClientAbstractFactory {

    /**
     * 获取域名服务商客户端
     *
     * @return 域名服务商客户端
     */
    @Override
    public DomainNameClientService<?> getDomainNameServiceClient() {
        return new AliyunClientServiceImpl();
    }
}
