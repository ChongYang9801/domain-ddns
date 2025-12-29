package com.onon.ddns.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.sdk.service.alidns20150109.AsyncClient;
import com.aliyun.sdk.service.alidns20150109.models.*;
import com.google.gson.Gson;
import com.onon.ddns.config.DdnsConfig;
import com.onon.ddns.config.DomainNameServiceConfig;
import com.onon.ddns.config.DomainsConfig;
import com.onon.ddns.enums.DomainNameServiceEnum;
import com.onon.ddns.patterns.DomainNameServiceClientAbstractFactory;
import com.onon.ddns.patterns.DomainNameServiceClientFactoryProducer;
import com.onon.ddns.service.DomainNameClientService;
import com.onon.ddns.service.DomainNameService;
import com.onon.ddns.util.RealIPUtil;
import com.onon.ddns.util.SpringBeanUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 阿里云域名服务接口
 *
 * @Author onion
 * @Date 2025/12/28 10:26
 **/
@Slf4j
public class AliyunDomainNameService implements DomainNameService {

    /**
     * 更新域名解析记录
     *
     * @param domainsConfig           域名配置
     * @param domainNameServiceConfig 密钥配置
     * @param recordId                解析记录id
     */
    @Override
    public void updateDomainRecord(DomainsConfig domainsConfig, DomainNameServiceConfig domainNameServiceConfig, String recordId) {
        // 获取阿里云域名服务客户端
        Map<String, AsyncClient> stringAsyncClientMap = getAsyncClient();
        if (stringAsyncClientMap.containsKey(domainNameServiceConfig.getAccessKeyId()) && StrUtil.isNotBlank(recordId)) {
            AsyncClient asyncClient = stringAsyncClientMap.get(domainNameServiceConfig.getAccessKeyId());
            // 获取本机ip地址
            List<InetAddress> inetAddressList = RealIPUtil.getRealIP();
            inetAddressList.forEach(inetAddress -> {
                UpdateDomainRecordRequest updateDomainRecordRequest = UpdateDomainRecordRequest.builder()
//                        .lang("zh")
                        .rr(StrUtil.isNotBlank(domainsConfig.getSubDomain()) ? domainsConfig.getSubDomain() : "@")
                        .type(inetAddress instanceof Inet4Address ? "A" : "AAAA")
                        .value(inetAddress.getHostAddress())
                        .recordId(recordId)
                        // 解析生效时间
//                        .TTL(600L)
                        // MX 记录的优先级，取值范围：[1,50]。
//                        .priority(1L)
                        // 解析线路，默认为 default。
//                        .line("default")
                        .build();
                CompletableFuture<UpdateDomainRecordResponse> response = asyncClient.updateDomainRecord(updateDomainRecordRequest);
                try {
                    UpdateDomainRecordResponse recordResponse = response.get();
                    if (recordResponse.getStatusCode().equals(200)) {
                        log.info("修改域名解析记录成功，执行结果：{}", new Gson().toJson(recordResponse));
                    }
                } catch (InterruptedException | ExecutionException e) {
                    log.error("修改域名解析记录失败：{}", e.getMessage());
                }
            });
        }
    }

    /**
     * 执行更新域名解析记录
     */
    @Override
    public void executionUpdateDomainRecord() {
        // 获取域名相关配置
        DdnsConfig ddnsConfig = SpringBeanUtils.getBean(DdnsConfig.class);
        // 创建周期性执行线程池
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1024);
        ddnsConfig.getDomainNameServiceConfig(DomainNameServiceEnum.ALIYUN)
                .forEach(aliyunConfig -> {
                    aliyunConfig.getDomains().forEach(domainsConfig -> {
                        executor.scheduleWithFixedDelay(() -> {
                            try {
                                // 获取原始域名解析记录
                                List<DescribeDomainRecordsResponseBody.Record> domainRecords = describeDomainRecords();
                                List<DescribeDomainRecordsResponseBody.Record> recordList = domainRecords.stream()
                                        // 判断域名是否已经有解析记录
                                        .filter(record -> ((StrUtil.isNotBlank(domainsConfig.getSubDomain()) ? domainsConfig.getSubDomain() : "@") + domainsConfig.getDomain()).equals(record.getRr() + record.getDomainName()))
                                        .collect(Collectors.toList());
                                if (CollUtil.isNotEmpty(recordList)) {
                                    // 删除域名解析记录
                                    recordList.forEach(domainRecord -> deleteDomainRecord(aliyunConfig, domainRecord.getRecordId()));
                                }
                                log.info("开始新增域名：{} 的解析记录", domainsConfig.getSubDomain() + "." + domainsConfig.getDomain());
                                addDomainRecord(domainsConfig, aliyunConfig);
                            } catch (Exception e) {
                                log.error("定时任务执行异常", e);
                            }
                        }, 0, domainsConfig.getInterval() == null ? 10 : domainsConfig.getInterval(), TimeUnit.MINUTES);
                    });
                });
    }

    /**
     * 获取域名解析记录
     *
     * @return 域名解析记录
     */
    @Override
    public List<DescribeDomainRecordsResponseBody.Record> describeDomainRecords() {
        // 获取域名相关配置
        DdnsConfig ddnsConfig = SpringBeanUtils.getBean(DdnsConfig.class);
        List<DescribeDomainRecordsResponseBody.Record> domainRecordsList = new ArrayList<>();
        if (ObjUtil.isNotEmpty(ddnsConfig) && CollUtil.isNotEmpty(ddnsConfig.getDomainNameServiceConfig())) {
            // 过滤主域名
            Set<String> domainNameSet = ddnsConfig.getDomainsConfig(DomainNameServiceEnum.ALIYUN)
                    .stream().map(DomainsConfig::getDomain)
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toSet());
            // 获取阿里云域名服务客户端
            Map<String, AsyncClient> stringAsyncClientMap = getAsyncClient();
            ddnsConfig.getDomainNameServiceConfig(DomainNameServiceEnum.ALIYUN).forEach(aliyunConfig -> {
                if (stringAsyncClientMap.containsKey(aliyunConfig.getAccessKeyId())) {
                    AsyncClient asyncClient = stringAsyncClientMap.get(aliyunConfig.getAccessKeyId());
                    // 获取域名列表
                    List<DomainsConfig> domains = aliyunConfig.getDomains();
                    domainNameSet.forEach(domainName -> {
                        if (StrUtil.isNotBlank(domainName)) {
                            log.info("开始获取域名：{} 的解析记录", domainName);
                            DescribeDomainRecordsRequest describeDomainRecordsRequest = DescribeDomainRecordsRequest.builder()
                                    .lang("zh")
                                    .domainName(domainName)
                                    .pageNumber(1L)
                                    .pageSize(500L)
                                    .build();
                            CompletableFuture<DescribeDomainRecordsResponse> response = asyncClient.describeDomainRecords(describeDomainRecordsRequest);
                            try {
                                DescribeDomainRecordsResponse resp = response.get();
                                if (resp.getStatusCode().equals(200)) {
                                    log.info("获取域名：{} 的解析记录成功，执行结果：{}", domainName, new Gson().toJson(resp));
                                    List<DescribeDomainRecordsResponseBody.Record> recordList = resp.getBody().getDomainRecords().getRecord();
                                    domainRecordsList.addAll(recordList.stream()
                                            // A 对应 IPv4， AAAA 对应 IPv6
                                            .filter(record -> record.getType().equalsIgnoreCase("A")
                                                    || record.getType().equalsIgnoreCase("AAAA")
                                            )
                                            .toList());
                                }
                            } catch (InterruptedException | ExecutionException exception) {
                                log.error("获取域名解析记录失败：{}", exception.getMessage());
                            }
                        }
                    });
                }
            });
        }
        return domainRecordsList;
    }

    /**
     * 添加域名解析记录
     *
     * @param domainsConfig           域名配置
     * @param domainNameServiceConfig 密钥配置
     */
    @Override
    public void addDomainRecord(DomainsConfig domainsConfig, DomainNameServiceConfig domainNameServiceConfig) {
        // 获取阿里云域名服务客户端
        Map<String, AsyncClient> stringAsyncClientMap = getAsyncClient();
        if (stringAsyncClientMap.containsKey(domainNameServiceConfig.getAccessKeyId())) {
            AsyncClient asyncClient = stringAsyncClientMap.get(domainNameServiceConfig.getAccessKeyId());
            // 获取本机ip地址
            List<InetAddress> inetAddressList = RealIPUtil.getRealIP();
            inetAddressList.forEach(inetAddress -> {
                AddDomainRecordRequest addDomainRecordRequest = AddDomainRecordRequest.builder()
                        .lang("zh")
                        .domainName(domainsConfig.getDomain())
                        .rr(StrUtil.isNotBlank(domainsConfig.getSubDomain()) ? domainsConfig.getSubDomain() : "@")
                        .type(inetAddress instanceof Inet4Address ? "A" : "AAAA")
                        .value(inetAddress.getHostAddress())
                        // 解析生效时间
//                        .TTL(600L)
                        // MX 记录的优先级，取值范围：[1,50]。
//                        .priority(1L)
                        // 解析线路，默认为 default。
//                        .line("default")
                        .build();
                CompletableFuture<AddDomainRecordResponse> response = asyncClient.addDomainRecord(addDomainRecordRequest);
                try {
                    AddDomainRecordResponse recordResponse = response.get();
                    if (recordResponse.getStatusCode().equals(200)) {
                        log.info("添加域名解析记录成功，执行结果：{}", new Gson().toJson(recordResponse));
                    }
                } catch (InterruptedException | ExecutionException e) {
                    log.error("添加域名解析记录失败：{}", e.getMessage());
                }
            });
        }
    }

    /**
     * 删除域名解析记录
     *
     * @param domainNameServiceConfig 密钥配置
     * @param recordId                解析记录id
     */
    @Override
    public void deleteDomainRecord(DomainNameServiceConfig domainNameServiceConfig, String recordId) {
        if (StrUtil.isNotBlank(recordId)) {
            // 获取阿里云域名服务客户端
            Map<String, AsyncClient> stringAsyncClientMap = getAsyncClient();
            if (stringAsyncClientMap.containsKey(domainNameServiceConfig.getAccessKeyId())) {
                AsyncClient asyncClient = stringAsyncClientMap.get(domainNameServiceConfig.getAccessKeyId());
                DeleteDomainRecordRequest deleteDomainRecordRequest = DeleteDomainRecordRequest.builder()
                        .recordId(recordId)
                        .build();
                CompletableFuture<DeleteDomainRecordResponse> response = asyncClient.deleteDomainRecord(deleteDomainRecordRequest);
                try {
                    DeleteDomainRecordResponse resp = response.get();
                    if (resp.getStatusCode().equals(200)) {
                        log.info("删除解析记录成功，执行结果：{}", new Gson().toJson(resp));
                    }
                } catch (InterruptedException | ExecutionException e) {
                    log.error("删除域名解析记录失败：{}", e.getMessage());
                }
            }
        }

    }


    /**
     * 获取阿里云域名服务客户端
     *
     * @return 阿里云域名服务客户端
     */
    private Map<String, AsyncClient> getAsyncClient() {
        DomainNameServiceClientAbstractFactory serviceClientAbstractFactory = DomainNameServiceClientFactoryProducer.getDomainNameServiceClientFactory(DomainNameServiceEnum.ALIYUN);
        DomainNameClientService<AsyncClient> domainNameServiceClient = serviceClientAbstractFactory.getDomainNameServiceClient();
        return domainNameServiceClient.initDomainNameServiceClient();
    }

}
