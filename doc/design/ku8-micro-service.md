# Ku8微服务以及Ku8应用

标签（空格分隔）： ku8微服务 Ku8应用

---

1. **ku8微服务(Micro Service)**
----------------------------

等价与一个kubernetes的Service，但多了副本数的属性，每一个ku8微服务最终会实例化为一个kubernetes Service与对应的RC。ku8微服务本质上是独立的，彼此之间没有关系。ku8微服务分为公共的，以及属于某个私有的Application的两种，公共的微服务，可以被私有的Application引用（仅仅是依赖引用，并不是被包含在Application内）。Ku8微服务通过一个“定义阶段”，在界面上录入必要的属性，然后可以发布到某个ku8分区上部署，这个部署过程就是在对应的分区上创建相关的kubernetes service与RC，并且与之关联起来。

Ku8微服务的状态， 在org.ku8eye.Constants ku8微服务状态常量里定义

 - 待发布
 - 发布中
 - 正常运行
 - 服务异常

**Ku8微服务在修改过程中，新的定义数据（jsonSpec）与旧的定义数据（prevJsonSpec）同时存在，ku8eye会判断具体是哪部分属性变化而需要重新发布，**
Ku8微服务的主界面建议如下方式展现：
![ImageLoadFailed](../../res/micro-service-list.PNG)
 
 点击“+”号，则可以创建一个微服务，可以从“模板”中选择或者直接进入微服务的构建界面：
 ![ImageLoadFailed](../../res/micro-service-def.PNG)
 
 微服务的创建，直接在ku8_service里生成一个实例，projectid为NULL，具体的定义信息存在于jsonSpec属性中。微服务创建以后，可以“发布”到某个资源分区（Ku8ResPartion）上运行，发布过程流程大致如下：
 
-  用户选择待发布的资源分区（仅对于新服务来说，对于已有的服务，则只能再之前分区上更新，就跳过这一界面）  
-  查询该分区已发布的微服务，如果有重名的服务名并且并不是自身（可能重新发布），则提示该微服务名称已经被xxxx占用并报错
-  用户点击，首先保存数据，包括ku8_service里的资源分区信息、修改状态为KU8_MICRO_SERVICE_PUBLISHIING_STATUS，然后先调用后台k8s的API服务接口，在指定的分区的命名空间内，依次完成k8s service与k8s RC对象的创建或更新操作，调用成功以后，更新ku8_service的状态为KU8_MICRO_SERVICE_RUNNING_STATUS，并且把prevJsonSpec的内容设置为当前的内容， 同时新增或者修改Ku8RcInst表（状态为KU8_MICRO_SERVICE_RUNNING_STATUS）。如果这个过程中，如果有失败步骤，则设置ku8_service与Ku8RcInst表的状态为 KU8_MICRO_SERVICE_FAILED_STATUS，并且ku8_service的note字段记录具体错误原因。

2. **ku8微服务模板**
---------------
微服务模板是一个用来快速实例化某个微服务的样板数据模型，常见的一些中间件都可以做成微服务模板，比如MySQL，Redis，Zookeeper，Memcache，MongoDB，FastDFS等。创建微服务的时候，可以直接选择某个微服务模板，修改必要的参数，然后发布到某个分区。微服务模板对应表ku8_service_template


3. **ku8应用（Application)**
---------------
Ku8应用是包括一组私有的K8微服务实例的一个整体单元，一个ku8应用可能会声明引用某些公共的Ku8微服务，与其自身包括的微服务不同，引用的微服务仅仅是确保这些微服务与它在同一个分区内存在，而不是去产生这样一个新的微服务。
     与Ku8微服务类似，一个ku8应用也有一个“定义节点”，即我们的Build阶段，在这个阶段中，用户通过界面定义一个或多个ku8微服务（也可以引用某个Ku8微服务模板），完成构建以后，选择某个ku8分区，发布每个微服务，在发布之前，需要确保它所引用的ku8微服务在此分区中存在。

一个Ku8应用包括如下的状态：

 - 待发布
 - 发布中
 - 正常运行
 - 部分服务异常
 - 服务全部异常

Ku8应用的运行状态同K8微服务，发布后的更新逻辑，同Ku8微服务，即判断哪些微服务是新增的，哪些是删除的，对于不同的变动，做不同的处理。
展示“发布详情”页面，提示用户当前所要执行的操作的内容，比如要创建或更新 k8s service、k8s RC  等资源，考虑到发布以及更新的复杂性，这里对比的参照信息来自k8s API 资源查询的结果，即当前ku8_service表里的微服务信息与k8s API 资源查询的结果进行对比并列出动作详情，比如创建或更新 k8s service 以及k8s RC，对于更新，则需要给出具体的更新细节，如更新了端口信息，镜像信息，实例数量信息等信息到页面上。







