package com.onon.ddns.patterns;


/**
 * 更新域名解析记录观察者
 *
 * @Author onion
 * @Date 2025/12/28 19:55
 **/
public interface UpdateDomainRecordObserver {

    /**
     * 执行更新域名解析记录
     */
    void executionUpdateDomainRecord();

}
