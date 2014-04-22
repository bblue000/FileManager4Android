FileManager4Android
===================

管理Android中的文件存储

***

###v1.0实现了基本功能，分为内存储和SD卡两个不同的FileManager

>|- 应用可以通过`FileManager.initConfig(Context, StorageType)`方法初始化优先存储路径设置——详见`StorageType`

>|- 获取想要的FileManager：`FileManager#getAppFileManager()`、`FileManager#getDataFileManager()`、`FileManager#getSDcardFileManager()`
