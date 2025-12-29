package com.onon.ddns.config;

import lombok.Data;

/**
 * 域名配置
 *
 * @author onion
 * @description
 * @create 2025/12/24 13:00
 */
@Data
public class DomainsConfig {

    private String domain;

    private String subDomain;

    private Integer interval;

}
