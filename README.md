# mirai-console-jcf-plugin
基于Mirai Console的Curseforge与Modrinth插件

[![Build Plugin](https://github.com/jie65535/mirai-console-jcf-plugin/actions/workflows/build.yml/badge.svg)](https://github.com/jie65535/mirai-console-jcf-plugin/actions/workflows/build.yml)

# 请注意：使用 CurseForge 功能需要申请 [Curseforge Api Key](https://console.curseforge.com/)！Modrinth 功能无需 API Key 即可使用。

## Introduction

允许用户通过`QQ`对[Curseforge](https://www.curseforge.com/)和[Modrinth](https://modrinth.com/)网站进行搜索查询

现在支持搜索`Minecraft`相关内容，可以通过命令搜索模组、整合包、资源包等。

支持查看文件列表与其下载地址，单独查看文件的更新日志。

支持订阅项目更新，有新版本时自动推送通知。

## Usage
指令
- /jcf help # 查看帮助
- /jcf setApiKey # 设置Curseforge API Key
- /jcf setSubsSender \<qq\> # 设置订阅信息推送bot（qq id）
- /jcf setCheckInterval \<seconds\> # 设置更新检查间隔（单位：秒）

### CurseForge 分类搜索命令（可配置）
- 搜索模组: cfmod \<filter\>
- 搜索整合包: cfpack \<filter\>
- 搜索资源包: cfres \<filter\>
- 搜索存档: cfworld \<filter\>
- 搜索水桶服插件: cfbukkit \<filter\>
- 搜索附加: cfaddon \<filter\>
- 搜索定制: cfcustom \<filter\>

### Modrinth 分类搜索命令（可配置）
- 搜索模组: mrmod \<filter\>
- 搜索整合包: mrpack \<filter\>
- 搜索资源包: mrres \<filter\>
- 搜索光影: mrshader \<filter\>
- 搜索插件: mrplugin \<filter\>
- 搜索数据包: mrdata \<filter\>

### 订阅管理命令
**CurseForge 订阅**
- /jcf subStat # 查看 CurseForge 订阅处理状态
- /jcf idleSubs # 使 CurseForge 订阅器闲置
- /jcf runSubs # 使 CurseForge 订阅器恢复运行

**Modrinth 订阅**
- /jcf mrSubStat # 查看 Modrinth 订阅处理状态
- /jcf mrIdleSubs # 使 Modrinth 订阅器闲置
- /jcf mrRunSubs # 使 Modrinth 订阅器恢复运行

## Screenshots

![chat record image](/doc/contact.png)

![show search result image](/doc/show_search_result.png)

![show addon info image](/doc/show_addon_info.png)

![show changelog image](/doc/show_changelog.png)

## TODO List
- [x] **搜索资源**
    - [x] 搜索模组
    - [x] 搜索整合包
    - [x] 搜索资源包
    - [x] ~~搜索存档~~
    - [ ] 根据项目ID搜索
---
- [x] 分页选择
- [ ] 获取介绍
- [x] 获取文件列表
- [x] 获取最新文件
- [x] 获取文件的修改记录
- [x] 获取文件下载地址
- [ ] 获取依赖的项目
---
- [x] 模组更新订阅，更新时通知订阅者
- [x] 集成 [Modrinth](https://modrinth.com/) 平台
    - [x] 搜索模组、整合包、资源包、光影、插件、数据包
    - [x] 查看项目详情与版本列表
    - [x] 订阅项目更新通知
- [ ] 设置代理


## 鸣谢
- [Mirai](https://github.com/mamoe/mirai) 提供机器人平台
- [Mirai Console](https://github.com/mamoe/mirai-console) 开放插件接入
