<p align="center">
	<img alt="logo" src="https://sa-token.dev33.cn/doc/logo.png" width="150" height="150">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Sa-Token v1.31.0</h1>
<h4 align="center">一个轻量级 Java 权限认证框架，让鉴权变得简单、优雅！</h4>
<p align="center">
	<a href="https://gitee.com/dromara/sa-token/stargazers"><img src="https://gitee.com/dromara/sa-token/badge/star.svg?theme=gvp"></a>
	<a href="https://gitee.com/dromara/sa-token/members"><img src="https://gitee.com/dromara/sa-token/badge/fork.svg?theme=gvp"></a>
	<a href="https://github.com/dromara/sa-token/stargazers"><img src="https://img.shields.io/github/stars/dromara/sa-token?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/dromara/sa-token/network/members"><img src="https://img.shields.io/github/forks/dromara/sa-token?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/dromara/sa-token/watchers"><img src="https://img.shields.io/github/watchers/dromara/sa-token?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/dromara/sa-token/issues"><img src="https://img.shields.io/github/issues/dromara/sa-token.svg?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/dromara/sa-token/blob/master/LICENSE"><img src="https://img.shields.io/github/license/dromara/sa-token.svg?style=flat-square"></a>
</p>

---

## 前言：
- [在线文档：http://sa-token.dev33.cn/](http://sa-token.dev33.cn/)

- 注：学习测试请拉取 master 分支，dev 为正在开发的分支，有很多特性并不稳定。

- 开源不易，点个 star 鼓励一下吧！


## Sa-Token 介绍

**Sa-Token** 是一个轻量级 Java 权限认证框架，主要解决：**登录认证**、**权限认证**、**单点登录**、**OAuth2.0**、**分布式Session会话**、**微服务网关鉴权**
等一系列权限相关问题。


- **登录认证** —— 单端登录、多端登录、同端互斥登录、七天内免登录
- **权限认证** —— 权限认证、角色认证、会话二级认证
- **Session会话** —— 全端共享Session、单端独享Session、自定义Session 
- **踢人下线** —— 根据账号id踢人下线、根据Token值踢人下线
- **账号封禁** —— 指定天数封禁、永久封禁、设定解封时间 
- **持久层扩展** —— 可集成Redis、Memcached等专业缓存中间件，重启数据不丢失
- **分布式会话** —— 提供jwt集成、共享数据中心两种分布式会话方案
- **微服务网关鉴权** —— 适配Gateway、ShenYu、Zuul等常见网关的路由拦截认证
- **单点登录** —— 内置三种单点登录模式：无论是否跨域、是否共享Redis，都可以搞定
- **OAuth2.0认证** —— 轻松搭建 OAuth2.0 服务，支持openid模式 
- **二级认证** —— 在已登录的基础上再次认证，保证安全性 
- **Basic认证** —— 一行代码接入 Http Basic 认证 
- **独立Redis** —— 将权限缓存与业务缓存分离 
- **临时Token验证** —— 解决短时间的Token授权问题
- **模拟他人账号** —— 实时操作任意用户状态数据
- **临时身份切换** —— 将会话身份临时切换为其它账号
- **前后台分离** —— APP、小程序等不支持Cookie的终端
- **同端互斥登录** —— 像QQ一样手机电脑同时在线，但是两个手机上互斥登录
- **多账号认证体系** —— 比如一个商城项目的user表和admin表分开鉴权
- **花式token生成** —— 内置六种Token风格，还可：自定义Token生成策略、自定义Token前缀
- **注解式鉴权** —— 优雅的将鉴权与业务代码分离
- **路由拦截式鉴权** —— 根据路由拦截鉴权，可适配restful模式
- **自动续签** —— 提供两种Token过期策略，灵活搭配使用，还可自动续签
- **会话治理** —— 提供方便灵活的会话查询接口
- **记住我模式** —— 适配[记住我]模式，重启浏览器免验证
- **密码加密** —— 提供密码加密模块，可快速MD5、SHA1、SHA256、AES、RSA加密 
- **全局侦听器** —— 在用户登陆、注销、被踢下线等关键性操作时进行一些AOP操作
- **开箱即用** —— 提供SpringMVC、WebFlux等常见web框架starter集成包，真正的开箱即用
- **更多功能正在集成中...** —— 如有您有好想法或者建议，欢迎加群交流


## Sa-Token-SSO 单点登录
Sa-Token-SSO 由简入难划分为三种模式，解决不同架构下的 SSO 接入问题：

| 系统架构						| 采用模式	| 简介						|  文档链接	|
| :--------						| :--------	| :--------					| :--------	|
| 前端同域 + 后端同 Redis		| 模式一		| 共享Cookie同步会话			| [文档](http://sa-token.dev33.cn/doc/index.html#/sso/sso-type1)、[示例](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-sso1-client)	|
| 前端不同域 + 后端同 Redis		| 模式二		| URL重定向传播会话 			| [文档](http://sa-token.dev33.cn/doc/index.html#/sso/sso-type2)、[示例](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-sso2-client)	|
| 前端不同域 + 后端 不同Redis	| 模式三		| Http请求获取会话			| [文档](http://sa-token.dev33.cn/doc/index.html#/sso/sso-type3)、[示例](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-sso3-client)	|


1. 前端同域：就是指多个系统可以部署在同一个主域名之下，比如：`c1.domain.com`、`c2.domain.com`、`c3.domain.com`
2. 后端同Redis：就是指多个系统可以连接同一个Redis。PS：这里并不需要把所有项目的数据都放在同一个Redis中，Sa-Token提供了 **`[权限缓存与业务缓存分离]`** 的解决方案，详情戳：[Alone独立Redis插件](http://sa-token.dev33.cn/doc/index.html#/plugin/alone-redis)
3. 如果既无法做到前端同域，也无法做到后端同Redis，那么只能走模式三，Http请求获取会话（Sa-Token对SSO提供了完整的封装，你只需要按照示例从文档上复制几段代码便可以轻松集成）

## Sa-Token-OAuth2.0 授权登录
Sa-OAuth2 模块分为四种授权模式，解决不同场景下的授权需求 

| 授权模式						| 简介						|
| :--------						| :--------					|
| 授权码（Authorization Code）	| OAuth2.0 标准授权步骤，Server 端向 Client 端下放 Code 码，Client 端再用 Code 码换取授权 Token			|
| 隐藏式（Implicit）				| 无法使用授权码模式时的备用选择，Server 端使用 URL 重定向方式直接将 Token 下放到 Client 端页面 			|
| 密码式（Password）				| Client直接拿着用户的账号密码换取授权 Token			|
| 客户端凭证（Client Credentials）| Server 端针对 Client 级别的 Token，代表应用自身的资源授权		|

详细参考文档：[http://sa-token.dev33.cn/doc/index.html#/oauth2/readme](http://sa-token.dev33.cn/doc/index.html#/oauth2/readme)



## Sa-Token 功能结构图
![sa-token-js](https://color-test.oss-cn-qingdao.aliyuncs.com/sa-token/x/sa-token-js4.png)

![sa-token-rz](https://color-test.oss-cn-qingdao.aliyuncs.com/sa-token/x/sa-token-rz2.png)


## 使用Sa-Token的开源项目 

- [[ Sa-Plus ]](https://gitee.com/click33/sa-plus)：一个基于 SpringBoot 架构的快速开发框架，内置代码生成器。

- [[ RuoYi-Vue-Plus ]](https://gitee.com/JavaLionLi/RuoYi-Vue-Plus)：重写RuoYi-Vue所有功能 集成 Sa-Token+Mybatis-Plus+Jackson+Xxl-Job+knife4j+Hutool+OSS 定期同步

- [[ RuoYi-Cloud-Plus ]](https://gitee.com/JavaLionLi/RuoYi-Cloud-Plus)：重写RuoYi-Cloud所有功能 整合 SpringCloudAlibaba Dubbo3.0 Sa-Token Mybatis-Plus MQ OSS ES Xxl-Job Docker 全方位升级 定期同步

- [[ EasyAdmin ]](https://gitee.com/lakernote/easy-admin)：一个基于SpringBoot2 + Sa-Token + Mybatis-Plus + Snakerflow + Layui 的后台管理系统，灵活多变可前后端分离，也可单体，内置代码生成器、权限管理、工作流引擎等

- [[ YC-Framework ]](http://framework.youcongtech.com/)：致力于打造一款优秀的分布式微服务解决方案。

- [[ Pig-Satoken ]](https://gitee.com/wchenyang/cloud-satoken)：重写 Pig 授权方式为 Sa-Token，其他代码不变。

更对开源案例可参考：[Awesome-Sa-Token](https://gitee.com/sa-token/awesome-sa-token)


## 友情链接
- [[ OkHttps ]](https://gitee.com/ejlchina-zhxu/okhttps)：轻量级 http 通信框架，API无比优雅，支持 WebSocket、Stomp 协议

- [[ Bean Searcher ]](https://github.com/ejlchina/bean-searcher)：专注高级查询的只读 ORM，使一行代码实现复杂列表检索！

- [[ Snowy ]](https://xiaonuo.vip/index#pricing)：小诺快速开发平台，同时拥有 Layui、Vue、SpringCloud 三个版本。

- [[ Jpom ]](https://gitee.com/dromara/Jpom)：简而轻的低侵入式在线构建、自动部署、日常运维、项目监控软件。

- [[ TLog ]](https://gitee.com/dromara/TLog)：一个轻量级的分布式日志标记追踪神器。

- [[ hippo4j ]](https://gitee.com/agentart/hippo4j)：强大的动态线程池框架，附带监控报警功能。


## 知识星球
<img src="https://oss.dev33.cn/sa-token/dromara-xingqiu--sa-token.jpg" width="300px" />


## 交流群
QQ交流群：496757342 [点击加入](https://jq.qq.com/?_wv=1027&k=ceibbMFr)

微信交流群：

![微信群](https://dev33-test.oss-cn-beijing.aliyuncs.com/sa-token/i-wx-qr.png ':size=230')

(扫码添加微信，备注：sa-token，邀您加入群聊)

<br>

