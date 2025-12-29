package com.onon.ddns.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.onon.ddns.enums.DomainNameServiceEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * ddns配置类
 *
 * @author onion
 * @description
 * @create 2025/12/23 12:49
 */
@Data
@Component
@ConfigurationProperties(prefix = "config")
public class DdnsConfig {

    // 默认不加密
    private Boolean encryption = false;

    // 域名解析服务提供商类型
    private String domainNameServiceProviderType;

    // 域名解析服务提供商配置
    private List<DomainNameServiceConfig> domainNameServiceConfig;

    /**
     * 获取域名解析服务提供商配置
     *
     * @param domainNameServiceType 域名解析服务提供商类型
     * @return 域名解析服务提供商配置
     */
    public List<DomainNameServiceConfig> getDomainNameServiceConfig(DomainNameServiceEnum domainNameServiceType) {
        if (CollUtil.isNotEmpty(domainNameServiceConfig) && domainNameServiceType != null) {
            return domainNameServiceConfig.stream()
                    .filter(config -> config.getDomainNameServiceProviderType().equalsIgnoreCase(DomainNameServiceEnum.ALIYUN.name()))
                    .toList();
        }
        return new ArrayList<>();
    }

    public List<DomainsConfig> getDomainsConfig(DomainNameServiceEnum domainNameServiceType) {
        return getDomainNameServiceConfig(domainNameServiceType)
                .stream().map(DomainNameServiceConfig::getDomains)
                .flatMap(List::stream)
                .filter(domainsConfig -> StrUtil.isNotBlank(domainsConfig.getDomain()))
                // 去重
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparing(config -> config.getSubDomain() + config.getSubDomain()))), ArrayList::new));
    }


}
