package com.onon.ddns.patterns;


import java.util.ArrayList;
import java.util.List;

/**
 * 更新域名解析记录主题
 *
 * @Author onion
 * @Date 2025/12/28 20:00
 **/
public class UpdateDomainRecordSubject {

    private List<UpdateDomainRecordObserver> updateDomainRecordObservers = new ArrayList<>();

    /**
     * 添加更新域名解析记录观察者
     *
     * @param observer 域名解析记录观察者
     */
    public void add(UpdateDomainRecordObserver observer) {
        updateDomainRecordObservers.add(observer);
    }

    /**
     * 删除更新域名解析记录观察者
     *
     * @param observer 域名解析记录观察者
     */
    public void detach(UpdateDomainRecordObserver observer) {
        updateDomainRecordObservers.remove(observer);
    }

    public void notifyAllObservers() {
        updateDomainRecordObservers.forEach(UpdateDomainRecordObserver::executionUpdateDomainRecord);
    }

}
