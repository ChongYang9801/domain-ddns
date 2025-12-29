package com.onon.ddns.task;


import cn.hutool.core.thread.ThreadUtil;
import com.onon.ddns.patterns.AliyunUpdateDomainRecordObserver;
import com.onon.ddns.patterns.UpdateDomainRecordSubject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 定时任务
 *
 * @Author onion
 * @Date 2025/12/22 21:35
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class StartTask implements ApplicationRunner {

    @Override
    public void run(@NotNull ApplicationArguments args) {
        ThreadUtil.execute(() -> {
            UpdateDomainRecordSubject subject = new UpdateDomainRecordSubject();
            subject.add(new AliyunUpdateDomainRecordObserver());
            subject.notifyAllObservers();
        });
    }
}
