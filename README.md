## 一个基于Spring Boot 4 的动态ddns小玩意

### 一个crud仔闲暇之时搞的小玩意，结合设计模式，可以很方便集成其他服务商的域名解析服务
### 支持IPV4/IPV6,会自动读取本地IP
### 根据配置可以支持多账户、多个域名、支持自定义时间进行解析
### 至于为什么使用Spring Boot,不直接原生开发？一方面是这个用起来比较顺手，国内大多数Java开发者估计也是经常使用Spring Boot，要是改起来也很快。



## 配置说明

```yaml
config:
  # accessKeyId 和 accessKeySecret 是否加密
  # 此处我是在公司内部使用，将域名解析到局域网地址，毕竟是公司电脑，可能存在配置被其他人看到，因此选择加密，如果没有加密需求，可以选择忽略
  # 默认为 false，直接填写明文的accessKeyId和accessKeySecret即可。
  # 另外，加密是基于网卡MAC地址，一旦选择加密，就只能在同一台机器上进行解密
  # 具体加解密方式参见：com.onon.ddns.util.CryptoUtil
  encryption: true
  DomainNameServiceConfig:
    # 支持多账号，并且一组密钥对可以管理多个域名
    - accessKeyId: OzQ8QAmfV1Vx0VLRfqx+tIWo+jb
      accessKeySecret: /XROc3a3Ffesp0O05iFVnvfYOcZiM
      # 域名解析服务提供商类型
      domainNameServiceProviderType: aliyun
      # 多个域名配置
      domains:
        - domain: example1.com
          subDomain: dev
          # 域名解析的更新间隔时间，单位：分。非必填，默认为10分钟
          interval: 120
```

## 项目说明

目前已经完成阿里云动态域名解析，如果你只使用阿里云进行解析，那么可以直接使用，配置对应的密钥即可。

如需扩展，可以根据查看`StartTask`类来自行扩展

# 最后，代码水平有限，设计模式用的也不多，肯定存在诸多不适之处，望各位大佬指正