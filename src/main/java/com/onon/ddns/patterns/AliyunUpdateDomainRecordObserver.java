package com.onon.ddns.patterns;


import com.onon.ddns.service.impl.AliyunDomainNameService;

/**
 * 阿里云更新域名解析记录观察者
 *
 * @Author onion
 * @Date 2025/12/28 20:07
 **/

public class AliyunUpdateDomainRecordObserver implements UpdateDomainRecordObserver {

    private final AliyunDomainNameService aliyunDomainNameService;

    public AliyunUpdateDomainRecordObserver() {
        this.aliyunDomainNameService = new AliyunDomainNameService();
    }


    /**
     * 执行更新域名解析记录
     */
    @Override
    public void executionUpdateDomainRecord() {
        aliyunDomainNameService.executionUpdateDomainRecord();
    }
}
