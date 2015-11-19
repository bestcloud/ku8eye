# ku8eye web 开发环境

当前版本的 **ku8eye web开发环境** 以docker镜像方式提供，下载地址为：
链接：http://pan.baidu.com/s/1gd0iyaf 密码：b4se
文件名为：ku8eye-web-0.1.tar.gz
用gunzip解压缩后，得到文件ku8eye-web-0.1.tar

Docker镜像包括的内容如下：
 - ansible脚本和kubernetes安装文件，用于自动化安装kubernetes
 -   JDK，用于将ku8eye web编译打包好的JAR放入启动运行，做集成测试
 - MySQL 5.7，用于ku8eye web所需的数据库ku8eye，最新的初始化SQL脚本在  https://github.com/bestcloud/ku8eye/blob/master/doc/install/initsql.sql

导入docker镜像：
`# docker load -i ku8eye-web-0.1.tar`

给该镜像打上tag：
`# docker tag 4beaacf8c465 ku8eye-web:0.1`

运行开发环境：
`docker run -tid -p 3306:3306 -p 8080:8080 --name ku8eye-web --privileged ku8eye-web:0.1 /sbin/init`


# 容器内包含的软件
## 1. Ansible安装环境
**Ansible的使用方法详见 [ku8eye-ansible-user-guide](https://github.com/bestcloud/ku8eye/blob/master/doc/ku8eye-ansible-user-guide.md "ku8eye-ansible-user-guide")**
## 2. JDK1.8
**环境变量 JAVA_HOME=/root/jdk1.8.0_65**
## 3. MySQL 5.7.9
**数据库名：ku8eye**
**数据库用户名：ku8eye，密码：123456**
