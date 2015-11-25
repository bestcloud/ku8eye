# ku8eye ansible多用户密码脚本

##host说明

``` script
# machines list in group

[docker-registry]
192.168.100.52 ansible_ssh_user=root ansible_ssh_pass=123456

[etcd]
192.168.100.52 ansible_ssh_user=root ansible_ssh_pass=123456

[kube-master]
192.168.100.52 ansible_ssh_user=root ansible_ssh_pass=123456

...
```
> 注：ansible_ssh_user指定用户名，ansible_ssh_pass指定用户名的密码

##执行ansible-playbook命令完成复制公钥操作：
``` script
[root@test60 ansible-pass]# ansible-playbook -i hosts keys.yaml 

PLAY [all] ******************************************************************** 

GATHERING FACTS *************************************************************** 
ok: [192.168.100.52]
ok: [192.168.100.53]

TASK: [Push rsa public key to all machines] *********************************** 
ok: [192.168.100.52]
ok: [192.168.100.53]

PLAY RECAP ******************************************************************** 
192.168.100.52             : ok=2    changed=0    unreachable=0    failed=0   
192.168.100.53             : ok=2    changed=0    unreachable=0    failed=0   

```