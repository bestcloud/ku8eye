# ku8eye web 开发环境

当前版本的 **ku8eye web开发环境** 以docker镜像方式提供，下载地址为：
链接：http://pan.baidu.com/s/1gd0iyaf 密码：b4se

文件名为：ku8eye-web-0.1.tar.gz
用gunzip解压缩后，得到文件ku8eye-web-0.1.tar

导入docker镜像：
`# docker load -i ku8eye-web-0.1.tar`

给该镜像打上tag：
`# docker tag 4beaacf8c465 ku8eye-web:0.1`

运行开发环境：
`docker run -tid -p 3306:3306 -p 8080:8080 --name ku8eye-web --privileged ku8eye-web:0.1 /sbin/init`


# - 容器内包含的软件
## 1. Ansible安装环境，以及安装Kubernetes所需的全部软件
**Ansible的使用方法详见 [ku8eye-ansible-user-guide](https://github.com/bestcloud/ku8eye/blob/master/doc/ku8eye-ansible-user-guide.md "ku8eye-ansible-user-guide")**
## 2. JDK1.8
**环境变量 JAVA_HOME=/root/jdk1.8.0_65**
## 3. MySQL 5.7.9
**数据库名：ku8eye**
**数据库用户名：ku8eye，密码：123456**


# - ku8eye-web镜像的Dockerfile
## Dockerfile说明
**基础镜像为CentOS官方最新版：centos:latest**

**主要步骤：**
1. 安装ansible，参考 [centos-ansible镜像的Dockerfile](https://hub.docker.com/r/ansible/centos7-ansible/~/dockerfile/)
2. 安装 sshpass，并执行 `ssh-keygen` 生成密钥
3. 复制安装Kubernetes所需的所有文件和ansible配置脚本到目录 `/root/kubernetes_cluster_setup`
4. 复制 JDK1.8 到 `/root/jdk1.8.0_65`，并设置环境变量 JAVA_HOME 和 PATH
5. 复制 MySQL 5.7.9 所需rpm包到 `/root/mysql-5.7.9.el7.x86_64.rpm`，并执行 `rpm` 命令完成安装
6. 由于 MySQL rpm 安装后使用 systemd 系统进行服务管理，该镜像的启动命令将为 `/usr/sbin/init`

**完整的Dockerfile如下：**
```
FROM centos:latest
MAINTAINER ku8eye.bestcloud

WORKDIR /root

# set http proxy if needed
# ENV http_proxy="http://<ip>:<port>" https_proxy="http://<ip>:<port>"

# 1. install ansible
RUN yum clean all && \
    yum -y install epel-release && \
    yum -y install PyYAML python-jinja2 python-httplib2 python-keyczar python-paramiko python-setuptools git python-pip
RUN mkdir /etc/ansible/
RUN echo -e '[local]\nlocalhost' > /etc/ansible/hosts
RUN pip install ansible

# 2. install sshpass, and generate ssh keys
RUN yum -y install sshpass
RUN ssh-keygen -q -t rsa -N "" -f ~/.ssh/id_rsa

# 3. add ku8eye-ansible binary and config files
COPY kubernetes_cluster_setup /root/kubernetes_cluster_setup

# 4. add JDK1.8
COPY jdk-8u65-linux-x64.gz /root
RUN cd /root && tar xzf jdk-8u65-linux-x64.gz
ENV JAVA_HOME="/root/jdk-8u65-linux-x64" PATH="$PATH:/root/jdk1.8.0_65/bin"
RUN rm -rf /root/jdk-8u65-linux-x64.gz

# 5. add MySQL 5.7.9
COPY mysql-5.7.9.el7.x86_64.rpm /root/mysql-5.7.9.el7.x86_64.rpm
RUN rpm -ih /root/mysql-5.7.9.el7.x86_64.rpm/libaio-0.3.109-12.el7.x86_64.rpm
RUN rpm -ih /root/mysql-5.7.9.el7.x86_64.rpm/numactl-libs-2.0.9-4.el7.x86_64.rpm
RUN rpm -ih /root/mysql-5.7.9.el7.x86_64.rpm/net-tools-2.0-0.17.20131004git.el7.x86_64.rpm
RUN rpm -ih /root/mysql-5.7.9.el7.x86_64.rpm/mysql-community-common-5.7.9-1.el7.x86_64.rpm
RUN rpm -ih /root/mysql-5.7.9.el7.x86_64.rpm/mysql-community-libs-5.7.9-1.el7.x86_64.rpm
RUN rpm -ih /root/mysql-5.7.9.el7.x86_64.rpm/mysql-community-client-5.7.9-1.el7.x86_64.rpm
RUN rpm -ih /root/mysql-5.7.9.el7.x86_64.rpm/mysql-community-server-5.7.9-1.el7.x86_64.rpm
RUN rm -rf /root/mysql-5.7.9.el7.x86_64.rpm

# 6. enable systemd to work
CMD ["/usr/sbin/init"]

```

## 创建镜像
`# docker build -t="ku8eye-web:0.1" --rm .`

## 运行容器
`# docker run -tid --privileged -p 3306:3306 -p 8080:8080 --name ku8eye-web ku8eye-web:0.1`
> **注意：需要 --privileged 来运行 /usr/sbin/init**
> **之后，容器内部才能够使用 systemctl 来启动 mysqld 服务**

## 待讨论事项
1. 由于 mysqld 服务的启动依赖systemd系统，在build镜像时由于 /usr/sbin/init 还未启动，systemctl将不能在Dockerfile中执行
    故只能在该镜像build完成，并用 docker run 启动之后，登录到容器内部，才能使用 systemctl 命令来启动 mysqld服务。
    **问题：是否可以不使用systemd系统来管理mysql？**
    
2. mysql服务在第一次启动后，root用户无法登录，遇到如下错误：
    `ERROR 1045 (28000): Access denied for user 'root'@'localhost' (using password: NO)`
    **解决方法：**
    1) 修改mysqld服务的配置文件`/etc/sysconfig/mysql` ，加上参数：`MYSQLD_OPTS="--skip-grant-tables"`，然后重启mysqld服务：
    `# systemctl restart mysqld`
    2) 用root用户登录：
    `# mysql -uroot mysql`
    3) 修改root用户的密码（例如：123456）：
    `mysql> UPDATE USER SET authentication_string=PASSWORD('123456') WHERE USER='root';`
    4) 使权限设置生效：
    `mysql> FLUSH PRIVILEGES;`
    5) 创建数据库ku8eye：
    `mysql> CREATE DATABASE ku8eye DEFAULT CHARSET=utf8;`
    6) 创建数据库用户ku8eye，密码为123456：
    `mysql> CREATE USER 'ku8eye'@'localhost' IDENTIFIED BY '123456';`
    7) 给ku8eye用户授权：
    `mysql> GRANT ALL ON ku8eye.* TO 'ku8eye'@'localhost';`
    8) 将配置文件`/etc/sysconfig/mysql`恢复原状，再次重启mysqld服务。

    > 也可以将SQL语句保存为文件，例如 /root/mysql-init.sql：
    `UPDATE USER SET authentication_string=PASSWORD('123456') WHERE USER='root';
     FLUSH PRIVILEGES;
     CREATE DATABASE ku8eye DEFAULT CHARSET=utf8;
     CREATE USER 'ku8eye'@'localhost' IDENTIFIED BY '123456';
     GRANT ALL ON ku8eye.* TO 'ku8eye'@'localhost';`
    > 然后执行 `# mysql -uroot mysql < /root/mysql-init.sql` 完成操作。

    **问题：是否可以不使用 --skip-grant-tables 来修改密码、创建数据库？**

3. mysql的安装过程是否不太适合在docker容器内安装
    **问题：是否可以安装 mariadb 来代替 mysql？**
