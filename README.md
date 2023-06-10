app 壳工程
uilayer 界面层：基础业务
- basebiz：基础业务
- bizextend 扩展业务、高级业务的动态feature（插件）
domainlayer 领域层
datalayer 数据层
foundationlayer 基础设施层

# 架构问题

1. biz_base模块可以去掉？业务基础库，这个与领域层的模块如何隔离与区分？如何放置什么代码都往basebiz放，与foundation层、领域层模块分不清；开发迷茫
    代码应该放在哪？
2. 领域层、ui层直接对foundationn层进行依赖，如果基础层稍微改动，那么就是牵一发而动前身？基础设施一定要稳定，特别是公开接口。
3. 如果让领域层只依赖基础设施层的api，那么一些自定义view、uikit这些咋搞呢？还是行不通啊？基础lib还得被上面正常依赖。总不能为自定义view、uikit、工具类等
    都去定义一套接口吧？非常不现实。
