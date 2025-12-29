package com.onon.ddns.patterns;


import com.onon.ddns.enums.DomainNameServiceEnum;

/**
 * 域名服务商客户端工厂生成器
 *
 * @Author onion
 * @Date 2025/12/28 14:26
 **/
public class DomainNameServiceClientFactoryProducer {

    public static DomainNameServiceClientAbstractFactory getDomainNameServiceClientFactory(DomainNameServiceEnum domainNameServiceEnum) {
        switch (domainNameServiceEnum) {
            case ALIYUN:
                return new DomainNameServiceClientFactory();
            default:
                return null;
        }
    }

}
