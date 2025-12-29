package com.onon.ddns.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.ICredentialProvider;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.alidns20150109.AsyncClient;
import com.onon.ddns.config.DdnsConfig;
import com.onon.ddns.enums.DomainNameServiceEnum;
import com.onon.ddns.service.DomainNameClientService;
import com.onon.ddns.util.CryptoUtil;
import com.onon.ddns.util.SpringBeanUtils;
import darabonba.core.client.ClientOverrideConfiguration;

import java.util.HashMap;
import java.util.Map;

public class AliyunClientServiceImpl implements DomainNameClientService<AsyncClient> {

    private static final Map<String, AsyncClient> ASYNC_CLIENT_MAP = new HashMap<>();

    @Override
    public Map<String, AsyncClient> initDomainNameServiceClient() {
        Map<String, ICredentialProvider> credentialProviderMap = initAliyunCredentialProvider();
        if (CollUtil.isNotEmpty(credentialProviderMap)) {
            credentialProviderMap.forEach((accessKeyId, credentialProvider) ->
                    ASYNC_CLIENT_MAP.put(accessKeyId, AsyncClient.builder()
                            .region("public")
                            //.httpClient(httpClient)
                            .credentialsProvider(credentialProvider)
                            //.serviceConfiguration(Configuration.create()) // Service-level configuration
                            // Client-level configuration rewrite, can set Endpoint, Http request parameters, etc.
                            .overrideConfiguration(
                                    ClientOverrideConfiguration.create()
                                            // Endpoint 请参考 https://api.aliyun.com/product/Alidns
                                            .setEndpointOverride("alidns.aliyuncs.com")
                                    //.setConnectTimeout(Duration.ofSeconds(30))
                            )
                            .build()));
        }
        return ASYNC_CLIENT_MAP;
    }


    /**
     * 初始化阿里CredentialProvider
     *
     * @return 阿里CredentialProvider
     */
    public static Map<String, ICredentialProvider> initAliyunCredentialProvider() {
        Map<String, ICredentialProvider> credentialProviderMap = new HashMap<>();
        // 获取配置类
        DdnsConfig ddnsConfig = SpringBeanUtils.getBean(DdnsConfig.class);
        if (ObjUtil.isNotEmpty(ddnsConfig) && CollUtil.isNotEmpty(ddnsConfig.getDomainNameServiceConfig())) {
            ddnsConfig.getDomainNameServiceConfig().forEach(aliyun -> {
                if (StrUtil.isNotBlank(aliyun.getAccessKeyId())
                        && StrUtil.isNotBlank(aliyun.getAccessKeySecret())
                        && CollUtil.isNotEmpty(aliyun.getDomains())
                        && DomainNameServiceEnum.ALIYUN.name().equalsIgnoreCase(aliyun.getDomainNameServiceProviderType())) {
                    if (!ASYNC_CLIENT_MAP.containsKey(aliyun.getAccessKeyId())) {
                        credentialProviderMap.put(aliyun.getAccessKeyId(), StaticCredentialProvider.create(Credential.builder()
                                .accessKeyId(Boolean.TRUE.equals(ddnsConfig.getEncryption()) ? CryptoUtil.decrypt(aliyun.getAccessKeyId()) : aliyun.getAccessKeyId())
                                .accessKeySecret(Boolean.TRUE.equals(ddnsConfig.getEncryption()) ? CryptoUtil.decrypt(aliyun.getAccessKeySecret()) : aliyun.getAccessKeySecret())
                                .build()));
                    }
                }
            });
        }
        return credentialProviderMap;
    }
}
