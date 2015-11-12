![ImageLoadFailed](./res/ku8eye.png)

A powerful web based Mangement of  Google's Kubernetes
It has the following goals
 - 1. One step to install kubernetes cluster. The fastest way to get up-and-running with Google Kubernetes cluster. complete with intelligent default settings based on your system. 
 - 2. Multi Role & Tenant enabled Management Portal. Through a centralized interface, your operations team can easily tune configurations and resourcing; manage a wide range of user roles for cross-departmental, self-service access; and even manage multiple clusters for multi-tenant environments.
 - 3. Draw up a standard kubernetes's project package format(ku8package). So every one can easy deploy this package with our automated wizards ,further more, we also provide a tool to visualization the creation proecess of kubernetes based project ,include Visual Design kubernetes service��RC��Pod and more Objects 
 - 4. Customizable monitoring and reporting. Get complete visibility into your cluster with many built-in health checks and alerts that you can configure based on what matters most to you. Not only can you monitor all components across all clusters (including Docker and Kubernetes ), you can also easily monitor your business service's performance.  ku8 eye has a customizable dashboard, with the ability to create advanced charts for historical monitoring and custom triggers and thresholds for your environment.
 - 5. Comprehensive troubleshooting ability. The only centralized log management aggregates logs across all cluster nodes, components ,include system logs and user program logs, and makes them searchable for simple troubleshooting, including integrated, custom alerting for the errors you care about. Historical views and metrics let you see exactly what happened when, and allow you to quickly see anomalistic behavior. 
 - 6. Continuous integration and delivery with Docker and kubernetes project. Provide a visual tool to manager project's continuous delivery pipeline,  allows you to auto buid new Docker images, push to private Docker registry, create a new kubernetes testing environment to run test cases and finally rolling update raleted kuberntes services in product environment
 
K8s eye��һ���ȸ�Kubernetes��Webһվʽ����ϵͳ�����������µ�Ŀ�꣺
 - 1.ͼ�λ�һ����װ�����ڵ��Kuberntes��Ⱥ���ǰ�װ����ȸ�Kubernetes��Ⱥ������Լ���ѷ�ʽ����װ���̻�ο���ǰϵͳ�������ṩĬ���Ż��ļ�Ⱥ��װ������ʵ����Ѳ���
 - 2.֧�ֶ��ɫ���⻧��Portal������档ͨ��һ�����л���Portal���棬��Ӫ�Ŷӿ��Ժܷ���ĵ�����Ⱥ�����Լ�����Ⱥ��Դ��ʵ�ֿ粿�ŵĽ�ɫ���û��������⻧����ͨ������������Ժ��������Kuberntes��Ⱥ����ά��������
 - 3.�ƶ�һ��KubernetesӦ�õĳ��򷢲�����׼(ku8package)���ṩһ���򵼹��ߣ�ʹ��ר��ΪKubernetes��Ƶ�Ӧ���ܹ������״ӱ��ػ����з����������ƺ����������У�����һ���ģ����ǻ��ṩ��KubernetesӦ�ÿ��ӻ��Ĺ������ߣ�ʵ��Kubernetes Service��RC��Pod�Լ�������Դ�Ŀ��ӻ������͹�����
 - 4.�ɶ��ƻ��ļ�غ͸澯ϵͳ���ڽ��ܶ�ϵͳ������鹤���������ͷ����쳣�������澯�¼����������Լ�ؼ�Ⱥ�е����нڵ�����������Docker��Kubernetes�������ܹ������׵ļ��ҵ��Ӧ�õ����ܣ������ṩ��һ��ǿ���Dashboard�������������ɸ��ָ��ӵļ��ͼ����չʾ��ʷ��Ϣ�����ҿ��������Զ�����ؼ��ָ��ĸ澯��ֵ��
 - 5.�߱����ۺϵġ�ȫ��Ĺ����Ų�������ƽ̨�ṩΨһ�ġ����л�����־�����ߣ���־ϵͳ�Ӽ�Ⱥ�и����ڵ���ȡ��־�����ۺϷ�������ȡ����־����ϵͳ��־���û�������־�������ṩȫ�ļ��������Է�����Ϸ����������Ų飬��������Ϣ������ظ澯��Ϣ������ʷ��ͼ����صĶ�������������㣬ʲôʱ������ʲô���飬�����ڿ����˽����ʱ����ϵͳ����Ϊ������
 - 6.ʵ��Dockers��kubernetes��Ŀ�ĳ������ɹ��ܡ��ṩһ�����ӻ����������������ɵ��������̣����������µ�Docker����Push����˽�вֿ��С�����һ��Kubernetes���Ի������в����Լ����չ������������������еȸ�����Ҫ���ڡ� 

�ο�����
 - 1.��jenkins��ansible��supervisor����һ��web��������ϵͳ ��http://blog.csdn.net/hengyunabc/article/details/44072065��
 - 2.��ansible������һ��Kuberntes�Զ�����װ�Ŀ�Դ��Ŀkubernetes-ansible  (https://github.com/eparis/kubernetes-ansible)
 - 3.ansible�����ĵ�, (http://www.kisops.com/?p=23)
 - 4.etc.