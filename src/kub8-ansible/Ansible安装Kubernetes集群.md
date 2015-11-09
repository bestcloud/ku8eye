# Ansible安装Kubernetes集群

**Ansible**是一款基于Python开发的自动化运维工具，本文通过使用 **ansible-playbook** 完成Kubernetes集群的一键安装。

## ansible 安装环境准备

1. 准备一台Linux服务器，并安装docker。

2. 将所有ansible脚本和源文件放在安装服务器的/root/ansible/kubernetes_cluster_setup目录中。

3. 下载ansible的docker镜像并启动ansible：
``` script
下载 ansible 镜像：
$ docker pull kubeguide/centos7-ansible:2.0

启动 ansible：
$ docker run -tid -v /root/ansible/kubernetes_cluster_setup:/root/ansible/kubernetes_cluster_setup --name ansible2 kubeguide/centos7-ansible:2.0
```
> **注：**该ansible镜像内已经安装好`sshpass`，并已在容器内部生成公钥私钥文件


## Kubernetes集群环境准备
一个Kubernetes集群由etcd服务、master服务和一组node组成。
本文以3台服务器为例，其中第一台安装etcd和master的服务，后两台安装node所需服务。
**注：根据实际环境进行修改**
| 服务器IP地址     | 作用             |
| :------------- | :----------------|
| 192.168.1.201  | etcd             |
| 192.168.1.201  | kube-master      |
| 192.168.1.202  | kube-node        |
| 192.168.1.203  | kube-node        |



## ansible配置 - hosts文件
在安装服务器的/root/ansible/kubernetes_cluster_setup目录下创建hosts文件，内容为待安装Kubernetes集群各服务器的分组与IP地址。
``` script
[etcd]
192.168.1.201

[kube-master]
192.168.1.201

[kube-node]
192.168.1.202
192.168.1.203
```

## 集群安装前的准备工作
1. 在安装服务器的/root/ansible/kubernetes_cluster_setup目录下创建`rootpassword`文件，内容为待安装所有服务器root用户的口令，例如：
``` script
password123
```

2. 在pre-setup目录下创建keys.yml文件：
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

3. 进入容器内部
``` script
$ docker exec -ti ansible2 bash
```

4. 执行ssh到所有主机，以将所有主机的fingerprint保存在known_hosts文件中。
``` script
$ ssh 192.168.1.201
The authenticity of host '192.168.1.201 (192.168.1.201)' can't be established.
ECDSA key fingerprint is 4c:de:25:76:ea:bd:78:18:82:bd:4e:29:39:23:06:92.
Are you sure you want to continue connecting (yes/no)? yes
......
$ ssh 192.168.1.202
......
$ ssh 192.168.1.203
......
```

5. 执行ansible-playbook命令完成复制公钥操作：
``` script
$ ansible-playbook -i hosts pre-setup/keys.yml

PLAY ***************************************************************************

TASK [setup] *******************************************************************
ok: [192.168.1.201]
ok: [192.168.1.202]
ok: [192.168.1.203]

TASK [Push rsa public key to all machines] *************************************
ok: [192.168.1.201]
ok: [192.168.1.202]
ok: [192.168.1.203]

PLAY RECAP *********************************************************************
192.168.1.201              : ok=2    changed=0    unreachable=0    failed=0   
192.168.1.202              : ok=2    changed=0    unreachable=0    failed=0   
192.168.1.203              : ok=2    changed=0    unreachable=0    failed=0   
```

> **注：**不进入容器，在安装服务器直接使用 docker exec 也可以完成ansible-playbook脚本的执行，注意配置文件需要使用全路径：
> $ **docker exec -ti ansible2 ansible-playbook -i /root/ansible/kubernetes_cluster_setup/hosts /root/ansible/kubernetes_cluster_setup/pre-setup/ping.yml**


6. 执行ansible-playbook命令停止所有机器的防火墙服务：
``` script
$ ansible-playbook -i hosts pre-setup/disablefirewalld.yml

PLAY ***************************************************************************

TASK [setup] *******************************************************************
ok: [192.168.1.201]
ok: [192.168.1.202]
ok: [192.168.1.203]

TASK [stop firewalld service] **************************************************
changed: [192.168.1.201]
changed: [192.168.1.202]
changed: [192.168.1.203]

TASK [disable firewalld service] ***********************************************
changed: [192.168.1.201]
changed: [192.168.1.202]
changed: [192.168.1.203]

PLAY RECAP *********************************************************************
192.168.1.201              : ok=3    changed=2    unreachable=0    failed=0   
192.168.1.202              : ok=3    changed=2    unreachable=0    failed=0   
192.168.1.203              : ok=3    changed=2    unreachable=0    failed=0   
```

## Kubernetes集群安装
1. 在安装服务器的/root/ansible/kubernetes_cluster_setup目录下为不同的分组创建role（角色），包括：
etcd
kube-master
kube-node

2. 在每个role下创建4个子目录：
defaults：存放变量的值，目录下main.yml文件将被ansible默认读取
files：存放需安装（复制）的源文件
tasks：ansible-playbook执行的任务脚本，目录下main.yml文件将被ansible默认读取
templates：需修改参数的配置文件，其中参数由defaults目录中的值进行替换

3. 在安装服务器的/root/ansible/kubernetes_cluster_setup目录中创建group_vars子目录存放全局变量
默认文件为all.yml

4. 在安装服务器的/root/ansible/kubernetes_cluster_setup目录中创建setup.yml文件，内容为ansible-playbook在各host安装role的配置：
``` script
---
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


5. 对配置文件进行相应的修改
特别需要在 /root/ansible/kubernetes_cluster_setup/hosts 文件中，将各node不同的参数进行设置：
``` script
[etcd]
192.168.1.201

[kube-master]
192.168.1.201

# for docker, different docker0's IP on different node
# and kubelet, kube-proxy
[kube-node]
192.168.1.202 docker0_ip=172.17.1.1/24 docker_runtime_root_dir=/hadoop1/docker kubelet_hostname_override=192.168.1.202 install_quagga_router=false
192.168.1.203 docker0_ip=172.17.2.1/24 docker_runtime_root_dir=/hadoop1/docker kubelet_hostname_override=192.168.1.203 install_quagga_router=false
```



6. 启动 ansible-playbook 完成集群的安装
``` script
$ ansible-playbook -i hosts setup.yml

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

7. 登录master服务器，验证Kubernetes集群正常启动
``` script
$ kubectl get nodes
NAME            LABELS                                 STATUS
192.168.1.202   kubernetes.io/hostname=192.168.1.202   Ready
192.168.1.203   kubernetes.io/hostname=192.168.1.203   Ready
```
