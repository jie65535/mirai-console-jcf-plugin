# mirai-console-jcf-plugin
基于Mirai Console的Curseforge插件

# 请注意：本插件需要申请 [Curseforge Api Key](https://console.curseforge.com/) 才可使用！！

## Introduction

允许用户通过`QQ`对[Curseforge](https://www.curseforge.com/)网站进行搜索查询

现在支持搜索`Minecraft`相关内容，可以通过命令搜索模组、整合包、资源包。

支持查看文件列表与其下载地址，单独查看文件的更新日志。

## Usage
指令
- /jcf help # 查看帮助
- /jcf setApiKey # 设置Curseforge API Key

分类搜索命令（可配置）
- 搜索模组: cfmod \<filter\>
- 搜索整合包: cfpack \<filter\>
- 搜索资源包: cfres \<filter\>
- 搜索存档: cfword \<filter\>
- 搜索水桶服插件: cfbukkit \<filter\>
- 搜索附加: cfaddon \<filter\>
- 搜索定制: cfcustom \<filter\>

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
- [ ] 模组更新订阅，更新时通知订阅者
- [ ] 设置代理


## 鸣谢
- [Mirai](https://github.com/mamoe/mirai) 提供机器人平台
- [Mirai Console](https://github.com/mamoe/mirai-console) 开放插件接入
