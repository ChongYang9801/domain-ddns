package com.onon.ddns.config;

import lombok.Data;

import java.util.List;

/**
 * 域名服务相关配置
 *
 * @author onion
 * @description
 * @create 2025/12/24 10:40
 */
@Data
public class DomainNameServiceConfig {

    private String domainNameServiceProviderType;

    private String accessKeyId;

    private String accessKeySecret;

    private List<DomainsConfig> domains;


}
