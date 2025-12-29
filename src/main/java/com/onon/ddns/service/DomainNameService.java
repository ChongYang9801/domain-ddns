package com.onon.ddns.service;


import com.onon.ddns.config.DomainNameServiceConfig;
import com.onon.ddns.config.DomainsConfig;

import java.util.List;

/**
 * 域名服务接口
 *
 * @Author onion
 * @Date 2025/12/28 09:53
 **/
public interface DomainNameService {

    /**
     * 更新域名解析记录
     *
     * @param domainsConfig           域名配置
     * @param domainNameServiceConfig 密钥配置
     * @param recordId                解析记录id
     */
    void updateDomainRecord(DomainsConfig domainsConfig, DomainNameServiceConfig domainNameServiceConfig, String recordId);

    /**
     * 执行更新域名解析记录
     */
    void executionUpdateDomainRecord();

    /**
     * 获取域名解析记录
     *
     * @return 域名解析记录
     */
    List<?> describeDomainRecords();


    /**
     * 添加域名解析记录
     *
     * @param domainsConfig           域名配置
     * @param domainNameServiceConfig 密钥配置
     */
    void addDomainRecord(DomainsConfig domainsConfig, DomainNameServiceConfig domainNameServiceConfig);


    /**
     * 删除域名解析记录
     *
     * @param domainNameServiceConfig 密钥配置
     * @param recordId                解析记录id
     */
    void deleteDomainRecord(DomainNameServiceConfig domainNameServiceConfig, String recordId);

}
