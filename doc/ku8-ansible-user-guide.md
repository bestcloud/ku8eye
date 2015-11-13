# Ansible安装Kubernetes集群

**Ansible**是一款基于Python开发的自动化运维工具，本文通过使用 **ansible-playbook** 完成Kubernetes集群的一键安装。

## 1. Ansible 安装环境准备

**1.** 准备一台Linux服务器，并安装docker。

**2.** 将所有ansible脚本和源文件放在安装服务器的/root/ansible/kubernetes_cluster_setup目录中。

**3.** 下载ansible的docker镜像并启动ansible：
``` script
下载 ansible 镜像：
$ docker pull kubeguide/centos7-ansible:2.0

启动 ansible：
$ docker run -tid -v /root/ansible/kubernetes_cluster_setup:/root/ansible/kubernetes_cluster_setup --name ansible2 kubeguide/centos7-ansible:2.0
```
> **注：**该ansible镜像内已经安装好`sshpass`，并已在容器内部生成公钥私钥文件


## 2. Kubernetes集群环境准备
一个Kubernetes集群由etcd服务、master服务和一组node组成。
在无法访问Internet的环境，还需一台服务器作为docker private registry 私库，供Kubernetes使用。
本文以4台服务器为例，第一台安装docker registry，第二台安装etcd和master的服务，最后两台安装node所需服务。
**注：根据实际环境进行修改**
| 服务器IP地址     | 作用             |
| :------------- | :----------------|
| 192.168.1.200  | docker-registry  |
| 192.168.1.201  | etcd             |
| 192.168.1.201  | kube-master      |
| 192.168.1.202  | kube-node        |
| 192.168.1.203  | kube-node        |
> **注：** 请勿将运行ansible的服务器纳入Kubernetes集群内。


## 3. Ansible配置 - hosts文件
在安装服务器的/root/ansible/kubernetes_cluster_setup目录下创建hosts文件，内容为待安装Kubernetes集群各服务器的分组与IP地址。
``` script
[docker-registry]
192.168.1.200

[etcd]
192.168.1.201

[kube-master]
192.168.1.201

[kube-node]
192.168.1.202
192.168.1.203
```

## 4. 集群安装前的准备工作
### 4.1 在安装服务器的/root/ansible/kubernetes_cluster_setup目录下创建`rootpassword`文件，内容为待安装所有服务器root用户的口令，例如：
``` script
password123
```

### 4.2 在pre-setup目录下创建keys.yml文件：
``` script
# Using this module REQUIRES the sshpass package to be installed!
#
# This REQUIRES you have created a ~/.ssh/id_rsa.pub public key
#
# Place the root password for all nodes in ~/rootpassword, run this playbook,
# and it will put your public key on all the nodes.  Then delete rootpassword!
#
# You can also comment out the "vars" section and use --ask-pass on the command
# line.
#
# All ansible modules will fail if the host is not in the ssh known_hosts.
# Normally ansible just asks if the host key is acceptable.  BUT when using
# password instead of public key authentication it will not ask and will instead
# fail.
#
# You can solve this by running a meaningless play to first get the ssh host
# key, then lay down the public key. Something like:
#       ansible-playbook -i inventory ping.yml
# Then answer yes as you check the host keys.
#
# You also could set the environment variable ANSIBLE_HOST_KEY_CHECKING=False
# when running this playbook.  You would have to answer the host key questions
# the next time you run ansible.
# 
---
- hosts: all
  vars:
          ansible_ssh_pass: "{{ lookup('file', '/root/ansible/kubernetes_cluster_setup/rootpassword') }}"
  tasks:
          - name: Push rsa public key to all machines
            authorized_key: user={{ ansible_ssh_user }} key="{{ lookup('file', '~/.ssh/id_rsa.pub') }}"
```

### 4.3 进入容器内部
``` script
$ docker exec -ti ansible2 bash
```

### 4.4 运行`ssh host_ip`到所有主机，将所有主机的fingerprint保存在known_hosts文件中。
``` script
$ ssh 192.168.1.200
The authenticity of host '192.168.1.200 (192.168.1.200)' can't be established.
ECDSA key fingerprint is 4c:de:25:76:ea:bd:78:18:82:bd:4e:29:39:23:06:92.
Are you sure you want to continue connecting (yes/no)? yes
......
$ ssh 192.168.1.201
......
$ ssh 192.168.1.202
......
$ ssh 192.168.1.203
......
```

### 4.5 执行ansible-playbook命令完成复制公钥操作：
``` script
$ ansible-playbook -i hosts pre-setup/keys.yml

PLAY ***************************************************************************

TASK [setup] *******************************************************************
ok: [192.168.1.200]
ok: [192.168.1.201]
ok: [192.168.1.202]
ok: [192.168.1.203]

TASK [Push rsa public key to all machines] *************************************
ok: [192.168.1.200]
ok: [192.168.1.201]
ok: [192.168.1.202]
ok: [192.168.1.203]

PLAY RECAP *********************************************************************
192.168.1.200              : ok=2    changed=0    unreachable=0    failed=0   
192.168.1.201              : ok=2    changed=0    unreachable=0    failed=0   
192.168.1.202              : ok=2    changed=0    unreachable=0    failed=0   
192.168.1.203              : ok=2    changed=0    unreachable=0    failed=0   
```

> **注：**不进入容器，在安装服务器直接使用 docker exec 也可以完成ansible-playbook脚本的执行，注意配置文件需要使用全路径：
> **$ docker exec -ti ansible2 ansible-playbook -i /root/ansible/kubernetes_cluster_setup/hosts /root/ansible/kubernetes_cluster_setup/pre-setup/ping.yml**


### 4.6 执行ansible-playbook命令停止所有机器的防火墙服务：
``` script
$ ansible-playbook -i hosts pre-setup/disablefirewalld.yml

PLAY ***************************************************************************

TASK [setup] *******************************************************************
ok: [192.168.1.200]
ok: [192.168.1.201]
ok: [192.168.1.202]
ok: [192.168.1.203]

TASK [stop firewalld service] **************************************************
changed: [192.168.1.200]
changed: [192.168.1.201]
changed: [192.168.1.202]
changed: [192.168.1.203]

TASK [disable firewalld service] ***********************************************
changed: [192.168.1.200]
changed: [192.168.1.201]
changed: [192.168.1.202]
changed: [192.168.1.203]

PLAY RECAP *********************************************************************
192.168.1.200              : ok=2    changed=0    unreachable=0    failed=0   
192.168.1.201              : ok=3    changed=2    unreachable=0    failed=0   
192.168.1.202              : ok=3    changed=2    unreachable=0    failed=0   
192.168.1.203              : ok=3    changed=2    unreachable=0    failed=0   
```

### 4.7 执行ansible-playbook命令为每台服务器添加docker registry服务器主机名与IP地址的host记录
**`注：本文中安装的docker registry将使用主机名作为私库的地址，如果希望使用IP地址，请修改相应的配置文件`**
``` script
# ansible-playbook -i hosts pre-setup/add_docker_registry_host.yml 

PLAY ***************************************************************************

TASK [setup] *******************************************************************
ok: [192.168.1.200]
ok: [192.168.1.201]
ok: [192.168.1.202]
ok: [192.168.1.203]

TASK [add host entry for docker private registry server] ***********************
changed: [192.168.1.200]
changed: [192.168.1.201]
changed: [192.168.1.202]
changed: [192.168.1.203]

PLAY RECAP *********************************************************************
192.168.1.200              : ok=2    changed=1    unreachable=0    failed=0   
192.168.1.201              : ok=2    changed=1    unreachable=0    failed=0   
192.168.1.202              : ok=2    changed=1    unreachable=0    failed=0   
192.168.1.203              : ok=2    changed=1    unreachable=0    failed=0   
```

## 5. Kubernetes集群安装
### 5.1 创建Role
#### 在安装服务器的/root/ansible/kubernetes_cluster_setup目录下为不同的分组创建role（角色），包括：
* docker-registry
* etcd
* kube-master
* kube-node

### 5.2 在每个role下创建4个子目录：
defaults：存放变量的值，目录下main.yml文件将被ansible默认读取
files：存放需安装（复制）的源文件
tasks：ansible-playbook执行的任务脚本，目录下main.yml文件将被ansible默认读取
templates：需修改参数的配置文件，其中参数由defaults目录中的值进行替换

### 5.3 在安装服务器的/root/ansible/kubernetes_cluster_setup目录中创建group_vars子目录存放全局变量
默认文件为all.yml

### 5.4 在安装服务器的/root/ansible/kubernetes_cluster_setup目录中创建setup.yml文件，内容为ansible-playbook在各host安装role的配置：
``` script
---
- hosts: docker-registry
  roles:
    - docker-registry

- hosts: etcd
  roles:
    - etcd

- hosts: kube-master
  roles:
    - kube-master

- hosts: kube-node
  roles:
    - kube-node
```


### 5.5 准备最新版本的二进制文件
所需修改的二进制文件列表：
```
kubernetes_cluster_setup
│
└─roles
    ├─docker-registry
    │  └─files
    │         docker                      docker主程序
    │         docker-registry.tar         docker registry 镜像文件
    │         kubernetes_pause.tar        Kubernetes pause 镜像文件
    │
    ├─etcd
    │  └─files
    │         etcd                        etcd主程序
    │         etcdctl                     etcdctl命令行工具
    │
    ├─kube-master
    │  └─files
    │         hyperkube                   Kubernetes相关文件
    │         kube-apiserver
    │         kube-controller-manager
    │         kube-scheduler
    │         kubectl
    │
    └─kube-node
        └─files
              docker                      docker主程序
              georce_route_quagga.tar     quaggar docker 镜像文件
              hyperkube                   Kubernetes相关文件
              kube-proxy
              kubectl
              kubelet
```
> 说明：
- etcd 下载地址：https://github.com/coreos/etcd/releases
- Kubernetes下载地址：https://github.com/kubernetes/kubernetes/releases
- docker 下载地址：https://github.com/docker/docker/releases/
- docker registry 镜像下载地址：docker pull registry:2
-- 导出镜像文件：docker save -o docker-registry.tar registry:2
- Kubernetes pause 镜像下载地址：docker pull gcr.io/google_containers/pause
-- 导出镜像文件：docker save -o kubernetes_pause.tar gcr.io/google_containers/pause
- Quagga 镜像文件下载地址：docker pull index.alauda.cn/georce/router
-- 导出镜像文件：docker save -o georce_route_quagga.tar index.alauda.cn/georce/router



### 5.6 修改配置文件
**所需修改的配置文件列表：**
```
kubernetes_cluster_setup
│
├─hosts                           主机列表、各主机个性化参数
│
├─group_vars
│      all.yml                    全局参数
│
└─roles
    ├─docker-registry
    │  └─defaults
    │         main.yml            docker registry相关参数
    │
    ├─etcd
    │  └─defaults
    │         main.yml            etcd相关参数
    │
    ├─kube-master
    │  └─defaults
    │         main.yml            master相关参数
    │
    └─kube-node
        └─defaults
                main.yml          node相关参数
```

#### 1) 全局参数 group_vars\all.yml
- `cluster_domain_name: cluster.local`       kube-dns服务设置的domain名
- `cluster_dns_ip: 20.1.0.100`                kube-dns服务IP地址（需在kube-apiserver的apiserver_service_cluster_ip_range范围内）

- `docker_registry_server_name: yourcompany.com`    docker registry 主机名
- `docker_registry_server_ip: 192.168.1.202`            docker registry 主机IP地址

- `push_pause_image: true`                        是否将 Kubernetes pause 镜像push到 docker registry
- `kubernetes_pause_image_id: 6c4579af347b`               pause镜像ID
- `kubernetes_pause_image_tag: "{{docker_registry_url}}/google_containers/pause"` pause镜像在 docker registry 的URL


#### 2) docker registry 相关参数 roles\docker-registry\defaults\main.yml
- `docker0_ip: 172.17.253.1/24`                                      docker0网桥的IP地址
- `docker_runtime_root_dir: /hadoop1/docker`                         docker运行根目录

- `docker_registry_url: "{{docker_registry_server_name}}:5000"`      docker registry URL
- `docker_registry_root_dir: /hadoop1/docker_registry`               docker registry 运行目录
- `docker_registry_image_id: 774242a00f13`                           docker registry 镜像ID
- `docker_registry_image_tag: registry:2.2.0`                        docker registry 镜像tag

#### 3) etcd相关参数 roles\etcd\defaults\main.yml
- `peer_ip: 192.168.1.201`                    etcd所在主机的IP地址（cluster配置时使用）
- `etcd_data_dir: /var/lib/etcd/etcd_data`    etcd数据存储目录

#### 4) master相关参数 roles\kube-master\defaults\main.yml
-- for kube-apiserver
- `etcd_servers: http://192.168.1.201:4001`            kube-apiserver所需etcd服务的URL
- `apiserver_insecure_port: 1100`                      kube-apiserver监听的非安全端口号
- `apiserver_service_cluster_ip_range: 20.1.0.0/16`    Kubernetes Services可分配IP地址池

-- for kube-controller-manager, kube-scheduler
- `kube_master_url: http://192.168.1.201:1100`         kube-apiserver服务URL
- `kube_node_sync_period: 10s`                         master与node信息同步时间间隔

-- to generate ssh keys on master server
- `ca_crt_CN: ecip.hp.com`                             master ssh key内CA证书中CN参数
- `server_key_CN: 192.168.1.201`                       master ssh key内CN参数

#### 5) node相关参数 roles\kube-node\defaults\main.yml
-- for kubelet, kube-proxy
- `kube_master_url: http://192.168.1.201:1100`        kube-apiserver服务URL

-- quagga router docker image info
- `quagga_router_image_id: f96cfe685533`          quagga router 镜像ID
- `quagga_router_image_tag: index.alauda.cn/georce/router`  quagga router 镜像tag


#### 6) 各node不同的参数，在主机列表`kubernetes_cluster_setup/hosts `文件中进行设置
- [docker-registry]
`192.168.1.202`

- [etcd]
`192.168.1.201`

- [kube-master]
`192.168.1.201`

- [kube-node]
`192.168.1.202 docker0_ip=172.17.1.1/24 docker_runtime_root_dir=/hadoop1/docker kubelet_hostname_override=192.168.1.202 install_quagga_router=false`
`192.168.1.203 docker0_ip=172.17.2.1/24 docker_runtime_root_dir=/hadoop1/docker kubelet_hostname_override=192.168.1.203 install_quagga_router=false`

> [kube-node] 参数说明：
**docker0_ip=172.17.1.1/24**                                       docker0网桥的IP地址，每个node上设置为不同的IP地址
**docker_runtime_root_dir=/hadoop1/docker**             docker运行根目录
**kubelet_hostname_override=192.168.1.202**            kubelet主机名
**install_quagga_router=false**                                     是否安装Quagga路由器（docker容器）


### 5.7 运行 ansible-playbook 完成集群的安装
``` script
$ ansible-playbook -i hosts setup.yml

PLAY ***************************************************************************

TASK [setup] *******************************************************************
ok: [192.168.1.200]

TASK [docker-registry : copy docker to /usr/bin] *******************************
ok: [192.168.1.200]

TASK [docker-registry : copy config file docker to /etc/sysconfig] *************
ok: [192.168.1.200]

TASK [docker-registry : make docker runtime root directory /hadoop1/docker] ****
ok: [192.168.1.200]

TASK [docker-registry : copy docker.socket to /usr/lib/systemd/system] *********
ok: [192.168.1.200]

TASK [docker-registry : copy docker.service to /usr/lib/systemd/system] ********
ok: [192.168.1.200]

TASK [docker-registry : systemctl daemon-reload] *******************************
changed: [192.168.1.200]

TASK [docker-registry : restart docker.socket service] *************************
changed: [192.168.1.200]

TASK [docker-registry : enable docker service] *********************************
ok: [192.168.1.200]

TASK [docker-registry : restart docker service] ********************************
changed: [192.168.1.200]

TASK [docker-registry : check docker.socket service started ok] ****************
changed: [192.168.1.200]

TASK [docker-registry : check docker service started ok] ***********************
changed: [192.168.1.200]

TASK [docker-registry : check if registry image exists] ************************
changed: [192.168.1.200]

TASK [docker-registry : copy registry image file to /tmp] **********************
skipping: [192.168.1.200]

TASK [docker-registry : load registry image] ***********************************
skipping: [192.168.1.200]

TASK [docker-registry : tag registry image] ************************************
skipping: [192.168.1.200]

TASK [docker-registry : check if registry container exists] ********************
changed: [192.168.1.200]

TASK [docker-registry : start registry container] ******************************
changed: [192.168.1.200]

TASK [docker-registry : create running script] *********************************
skipping: [192.168.1.200]

TASK [docker-registry : start docker registry container] ***********************
skipping: [192.168.1.200]

TASK [docker-registry : check docker registry started ok] **********************
ok: [192.168.1.200]

TASK [docker-registry : copy pause image file to /tmp] *************************
ok: [192.168.1.200]

TASK [docker-registry : check if pause image exists] ***************************
changed: [192.168.1.200]

TASK [docker-registry : load pause image] **************************************
skipping: [192.168.1.200]

TASK [docker-registry : tag pause image] ***************************************
skipping: [192.168.1.200]

TASK [docker-registry : push pause image to private registry] ******************
skipping: [192.168.1.200]

PLAY RECAP *********************************************************************
192.168.1.200              : ok=18   changed=9    unreachable=0    failed=0  

PLAY ***************************************************************************

TASK [setup] *******************************************************************
ok: [192.168.1.201]

TASK [etcd : copy etcd to /usr/bin] ********************************************
ok: [192.168.1.201]

TASK [etcd : copy etcdctl to /usr/bin] *****************************************
ok: [192.168.1.201]

TASK [etcd : make dir /etc/etcd] ***********************************************
ok: [192.168.1.201]

TASK [etcd : copy config file etcd.conf to /etc/etcd] **************************
ok: [192.168.1.201]

TASK [etcd : copy etcd.service to /usr/lib/systemd/system] *********************
ok: [192.168.1.201]

TASK [etcd : make dir /var/lib/etcd/ for etcd service] *************************
ok: [192.168.1.201]

TASK [etcd : systemctl daemon-reload] ******************************************
changed: [192.168.1.201]

TASK [etcd : enable etcd service] **********************************************
ok: [192.168.1.201]

TASK [etcd : start etcd service] ***********************************************
changed: [192.168.1.201]

TASK [etcd : test etcd started ok] *********************************************
changed: [192.168.1.201]

PLAY ***************************************************************************

TASK [setup] *******************************************************************
ok: [192.168.1.201]

TASK [kube-master : copy kube-apiserver to /usr/bin] ***************************
ok: [192.168.1.201]

TASK [kube-master : copy kube-controller-manager to /usr/bin] ******************
ok: [192.168.1.201]

TASK [kube-master : copy kube-scheduler to /usr/bin] ***************************
ok: [192.168.1.201]

TASK [kube-master : copy kubectl to /usr/bin] **********************************
ok: [192.168.1.201]

TASK [kube-master : copy hyperkube to /usr/bin] ********************************
ok: [192.168.1.201]

TASK [kube-master : make dir /var/run/kubernetes] ******************************
ok: [192.168.1.201]

TASK [kube-master : create keys - ca.key] **************************************
changed: [192.168.1.201]

TASK [kube-master : create keys - ca.crt] **************************************
changed: [192.168.1.201]

TASK [kube-master : create keys - server.key] **********************************
changed: [192.168.1.201]

TASK [kube-master : create keys - server.csr] **********************************
changed: [192.168.1.201]

TASK [kube-master : create keys - server.crt] **********************************
changed: [192.168.1.201]

TASK [kube-master : make dir /etc/kubernetes] **********************************
ok: [192.168.1.201]

TASK [kube-master : copy config file apiserver to /etc/kubernetes] *************
ok: [192.168.1.201]

TASK [kube-master : copy config file config to /etc/kubernetes] ****************
ok: [192.168.1.201]

TASK [kube-master : copy config file controller-manager to /etc/kubernetes] ****
ok: [192.168.1.201]

TASK [kube-master : copy config file scheduler to /etc/kubernetes] *************
ok: [192.168.1.201]

TASK [kube-master : copy kube-apiserver.service to /usr/lib/systemd/system] ****
ok: [192.168.1.201]

TASK [kube-master : copy kube-controller-manager.service to /usr/lib/systemd/system] ***
ok: [192.168.1.201]

TASK [kube-master : copy kube-scheduler.service to /usr/lib/systemd/system] ****
ok: [192.168.1.201]

TASK [kube-master : systemctl daemon-reload] ***********************************
changed: [192.168.1.201]

TASK [kube-master : make dir /var/log/kubernetes] ******************************
ok: [192.168.1.201]

TASK [kube-master : enable kube-apiserver service] *****************************
ok: [192.168.1.201]

TASK [kube-master : start kube-apiserver service] ******************************
changed: [192.168.1.201]

TASK [kube-master : enable kube-controller-manager service] ********************
ok: [192.168.1.201]

TASK [kube-master : start kube-controller-manager service] *********************
changed: [192.168.1.201]

TASK [kube-master : enable kube-scheduler service] *****************************
ok: [192.168.1.201]

TASK [kube-master : start kube-scheduler service] ******************************
changed: [192.168.1.201]

TASK [kube-master : check kube-apiserver service started ok] *******************
changed: [192.168.1.201]

TASK [kube-master : check kube-controller-manager service started ok] **********
changed: [192.168.1.201]

TASK [kube-master : check kube-scheduler service started ok] *******************
changed: [192.168.1.201]

PLAY ***************************************************************************

TASK [setup] *******************************************************************
ok: [192.168.1.203]
ok: [192.168.1.202]

TASK [kube-node : copy docker to /usr/bin] *************************************
ok: [192.168.1.202]
ok: [192.168.1.203]

TASK [kube-node : copy config file docker to /etc/sysconfig] *******************
ok: [192.168.1.203]
ok: [192.168.1.202]

TASK [kube-node : make docker runtime root directory /hadoop1/docker] **********
ok: [192.168.1.203]
ok: [192.168.1.202]

TASK [kube-node : copy docker.socket to /usr/lib/systemd/system] ***************
ok: [192.168.1.203]
ok: [192.168.1.202]

TASK [kube-node : copy docker.service to /usr/lib/systemd/system] **************
ok: [192.168.1.202]
ok: [192.168.1.203]

TASK [kube-node : systemctl daemon-reload] *************************************
changed: [192.168.1.203]
changed: [192.168.1.202]

TASK [kube-node : start docker.socket service] *********************************
changed: [192.168.1.203]
changed: [192.168.1.202]

TASK [kube-node : enable docker service] ***************************************
ok: [192.168.1.203]
ok: [192.168.1.202]

TASK [kube-node : start docker service] ****************************************
changed: [192.168.1.202]
changed: [192.168.1.203]

TASK [kube-node : check docker.socket service started ok] **********************
changed: [192.168.1.202]
changed: [192.168.1.203]

TASK [kube-node : check docker service started ok] *****************************
changed: [192.168.1.203]
changed: [192.168.1.202]

TASK [kube-node : install_quagga_router - delete all docker containers] ********
skipping: [192.168.1.202]
skipping: [192.168.1.203]

TASK [kube-node : install_quagga_router - delete all docker images] ************
skipping: [192.168.1.202]
skipping: [192.168.1.203]

TASK [kube-node : install_quagga_router - copy Quagga image to /tmp] ***********
skipping: [192.168.1.202]
skipping: [192.168.1.203]

TASK [kube-node : install_quagga_router - load Quagga image] *******************
skipping: [192.168.1.202]
skipping: [192.168.1.203]

TASK [kube-node : install_quagga_router - tag Quagga image] ********************
skipping: [192.168.1.202]
skipping: [192.168.1.203]

TASK [kube-node : install_quagga_router - start Quagga container] **************
skipping: [192.168.1.202]
skipping: [192.168.1.203]

TASK [kube-node : copy kubelet to /usr/bin] ************************************
ok: [192.168.1.203]
ok: [192.168.1.202]

TASK [kube-node : copy kube-proxy to /usr/bin] *********************************
ok: [192.168.1.203]
ok: [192.168.1.202]

TASK [kube-node : copy kubectl to /usr/bin] ************************************
ok: [192.168.1.202]
ok: [192.168.1.203]

TASK [kube-node : copy hyperkube to /usr/bin] **********************************
ok: [192.168.1.203]
ok: [192.168.1.202]

TASK [kube-node : make dir /var/run/kubernetes] ********************************
ok: [192.168.1.202]
ok: [192.168.1.203]

TASK [kube-node : make dir /etc/kubernetes] ************************************
ok: [192.168.1.203]
ok: [192.168.1.202]

TASK [kube-node : copy config file kubelet to /etc/kubernetes] *****************
ok: [192.168.1.203]
ok: [192.168.1.202]

TASK [kube-node : copy config file config to /etc/kubernetes] ******************
ok: [192.168.1.202]
ok: [192.168.1.203]

TASK [kube-node : copy config file proxy to /etc/kubernetes] *******************
ok: [192.168.1.203]
ok: [192.168.1.202]

TASK [kube-node : copy kubelet.service to /usr/lib/systemd/system] *************
ok: [192.168.1.202]
ok: [192.168.1.203]

TASK [kube-node : copy kube-proxy.service to /usr/lib/systemd/system] **********
ok: [192.168.1.203]
ok: [192.168.1.202]

TASK [kube-node : make dir /var/lib/kubelet] ***********************************
ok: [192.168.1.203]
ok: [192.168.1.202]

TASK [kube-node : systemctl daemon-reload] *************************************
changed: [192.168.1.203]
changed: [192.168.1.202]

TASK [kube-node : make dir /var/log/kubernetes] ********************************
ok: [192.168.1.203]
ok: [192.168.1.202]

TASK [kube-node : enable kubelet service] **************************************
ok: [192.168.1.203]
ok: [192.168.1.202]

TASK [kube-node : start kubelet service] ***************************************
changed: [192.168.1.203]
changed: [192.168.1.202]

TASK [kube-node : enable kube-proxy service] ***********************************
ok: [192.168.1.202]
ok: [192.168.1.203]

TASK [kube-node : start kube-proxy service] ************************************
changed: [192.168.1.203]
changed: [192.168.1.202]

TASK [kube-node : check kubelet service started ok] ****************************
changed: [192.168.1.203]
changed: [192.168.1.202]

TASK [kube-node : check kube-proxy service started ok] *************************
changed: [192.168.1.203]
changed: [192.168.1.202]

PLAY RECAP *********************************************************************
192.168.1.201              : ok=42   changed=15   unreachable=0    failed=0   
192.168.1.202              : ok=32   changed=10   unreachable=0    failed=0   
192.168.1.203              : ok=32   changed=10   unreachable=0    failed=0   

```

### 5.8 登录master服务器，验证Kubernetes集群正常启动
``` script
$ kubectl get nodes
NAME            LABELS                                 STATUS
192.168.1.202   kubernetes.io/hostname=192.168.1.202   Ready
192.168.1.203   kubernetes.io/hostname=192.168.1.203   Ready
```
