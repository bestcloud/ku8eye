![ImageLoadFailed](./res/ku8eye.png)

A powerful web based Mangement of  Google's Kubernetes
It has the following goals
 - 1. One step to install kubernetes cluster. The fastest way to get up-and-running with Google Kubernetes cluster. complete with intelligent default settings based on your system. 
 - 2. Multi Role & Tenant enabled Management Portal. Through a centralized interface, your operations team can easily tune configurations and resourcing; manage a wide range of user roles for cross-departmental, self-service access; and even manage multiple clusters for multi-tenant environments.
 - 3. Draw up a standard kubernetes's project package format(ku8package). So every one can easy deploy this package with our automated wizards ,further more, we also provide a tool to visualization the creation proecess of kubernetes based project ,include Visual Design kubernetes service、RC、Pod and more Objects 
 - 4. Customizable monitoring and reporting. Get complete visibility into your cluster with many built-in health checks and alerts that you can configure based on what matters most to you. Not only can you monitor all components across all clusters (including Docker and Kubernetes ), you can also easily monitor your business service's performance.  ku8 eye has a customizable dashboard, with the ability to create advanced charts for historical monitoring and custom triggers and thresholds for your environment.
 - 5. Comprehensive troubleshooting ability. The only centralized log management aggregates logs across all cluster nodes, components ,include system logs and user program logs, and makes them searchable for simple troubleshooting, including integrated, custom alerting for the errors you care about. Historical views and metrics let you see exactly what happened when, and allow you to quickly see anomalistic behavior. 
 - 6. Continuous integration and delivery with Docker and kubernetes project. Provide a visual tool to manager project's continuous delivery pipeline,  allows you to auto buid new Docker images, push to private Docker registry, create a new kubernetes testing environment to run test cases and finally rolling update raleted kuberntes services in product environment
 
K8s eye是一个谷歌Kubernetes的Web一站式管理系统，它具有如下的目标：
 - 1.图形化一键安装部署多节点的Kuberntes集群。是安装部署谷歌Kubernetes集群的最快以及最佳方式，安装流程会参考当前系统环境，提供默认优化的集群安装参数，实现最佳部署。
 - 2.支持多角色多租户的Portal管理界面。通过一个集中化的Portal界面，运营团队可以很方便的调整集群配置以及管理集群资源，实现跨部门的角色及用户管理、多租户管理，通过自助服务可以很容易完成Kuberntes集群的运维管理工作。
 - 3.制定一个Kubernetes应用的程序发布包标准(ku8package)并提供一个向导工具，使得专门为Kubernetes设计的应用能够很容易从本地环境中发布到公有云和其他环境中，更进一步的，我们还提供了Kubernetes应用可视化的构建工具，实现Kubernetes Service、RC、Pod以及其他资源的可视化构建和管理功能
 - 4.可定制化的监控和告警系统。内建很多系统健康检查工具用来检测和发现异常并触发告警事件，不仅可以监控集群中的所有节点和组件（包括Docker与Kubernetes），还能够很容易的监控业务应用的性能，我们提供了一个强大的Dashboard，可以用来生成各种复杂的监控图表以展示历史信息，并且可以用来自定义相关监控指标的告警阀值。
 - 5.具备的综合的、全面的故障排查能力。平台提供唯一的、集中化的日志管理工具，日志系统从集群中各个节点拉取日志并做聚合分析，拉取的日志包括系统日志和用户程序日志，并且提供全文检索能力以方便故障分析和问题排查，检索的信息包括相关告警信息，而历史视图和相关的度量数据则告诉你，什么时候发生了什么事情，有助于快速了解相关时间内系统的行为特征。
 - 6.实现Dockers与kubernetes项目的持续集成功能。提供一个可视化工具驱动持续集成的整个流程，包括创建新的Docker镜像、Push镜像到私有仓库中、创建一个Kubernetes测试环境进行测试以及最终滚动升级到生产环境中等各个主要环节。 

参考资料
 - 1.用jenkins，ansible，supervisor打造一个web构建发布系统 （http://blog.csdn.net/hengyunabc/article/details/44072065）
 - 2.用ansible开发的一个Kuberntes自动化安装的开源项目kubernetes-ansible  (https://github.com/eparis/kubernetes-ansible)
 - 3.ansible入门文档, (http://www.kisops.com/?p=23)
 - 4.etc.