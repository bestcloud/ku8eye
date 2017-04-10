FROM centos:latest
MAINTAINER ku8eye.bestcloud@github
 
# set timezone
ENV TZ Asia/Shanghai

# set http proxy if needed
# ENV http_proxy="http://<proxy_ip>:<proxy_port>" https_proxy="http://<proxy_ip>:<proxy_port>"

# 1. install ansible (from Internet)
RUN yum clean all && \
    yum -y install epel-release && \
    yum -y install PyYAML python-jinja2 python-httplib2 python-keyczar python-paramiko python-setuptools git python-pip
RUN mkdir /etc/ansible/ && echo -e '[local]\nlocalhost' > /etc/ansible/hosts
RUN pip install ansible

# 2. install sshpass, and generate ssh keys (from Internet)
RUN yum -y install sshpass
RUN ssh-keygen -q -t rsa -N "" -f ~/.ssh/id_rsa
# make ansible not do key checking from ~/.ssh/known_hosts file
ENV ANSIBLE_HOST_KEY_CHECKING false

# 3. install MariaDB (mysql) (from Internet)
COPY MariaDB.repo /etc/yum.repos.d/MariaDB.repo
RUN yum -y install MariaDB-server MariaDB-client

# 4. install supervisor (from Internet)
RUN pip install supervisor

# 5. add JRE1.8
COPY jre1.8.0_65 /root/jre1.8.0_65
ENV JAVA_HOME="/root/jre1.8.0_65" PATH="$PATH:/root/jre1.8.0_65/bin"
RUN chmod +x /root/jre1.8.0_65/bin/*

# 6. install openssh
RUN yum install -y openssh openssh-server
RUN mkdir -p /var/run/sshd && echo "root:root" | chpasswd
RUN /usr/sbin/sshd-keygen
RUN sed -ri 's/UsePAM yes/#UsePAM yes/g' /etc/ssh/sshd_config && sed -ri 's/#UsePAM no/UsePAM no/g' /etc/ssh/sshd_config

# 7. add ku8eye-ansible binary and config files
COPY kubernetes_cluster_setup /root/kubernetes_cluster_setup

# 8. copy shell scripts, SQL scripts, config files (could be updated in the future)
# db init SQL
COPY db_scripts /root/db_scripts
# shell scripts
COPY shell_scripts /root/shell_scripts
RUN chmod +x /root/shell_scripts/*.sh
COPY ku8eye-install-kubernetes.sh /root/ku8eye-install-kubernetes.sh
RUN chmod +x /root/ku8eye-install-kubernetes.sh
# latest jar
COPY ku8eye-web.jar /root/ku8eye-web.jar

# 9. start mariadb, init db data, and start ku8eye-web app
# supervisor config file
COPY supervisord.conf /etc/supervisord.conf
ENTRYPOINT /usr/bin/supervisord


