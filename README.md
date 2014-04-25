FileManager4Android
===================

管理Android中的文件存储

***


##简介
Android中主要分为两种存储路径： 

>·/data/data/[packageName]/ 

>·SD卡 

相应地，本管理模块中也分别提供了，可以通过以下方式获取： 
>·`FileManager.getDataFileManager()`

>·`FileManager.getSDcardFileManager()` 

一般情况下，应用程序会设置默认的存储方式（本模块默认为SD卡），应用在使用本模块时，可以通过调用`FileManager.initAppConfig(Context, StorageType)`， 在应用程序的Application初始化时进行设置。

---


##v1.0实现了基本功能，分为内存储和SD卡两个不同的FileManager

>|- 应用可以通过`FileManager.initConfig(Context, StorageType)`方法初始化优先存储路径设置——详见`StorageType`

>|- 获取想要的FileManager：`FileManager#getAppFileManager()`、`FileManager#getDataFileManager()`、`FileManager#getSDcardFileManager()`
