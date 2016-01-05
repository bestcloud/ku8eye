package org.ku8eye;

public class Constants {
	// user type constant
	public static final int USERTYPE_ADMIN = 1;
	public static final int USERTYPE_TANENT = 0;
	public static final String k8sparam_cluster_docker0_ip_srange = "cluster-docker0-ip-range";
	public static final byte K8S_TYPE_API_SERVICE = 1;
	public static final byte K8S_TYPE_ETCD_SERVICE = 2;
	public static final byte K8S_TYPE_REGISTRY_SERVICE = 3;
	public static final byte K8S_SERICE_STATUS_OK = 1;
	public static final byte K8S_SERICE_STATUS_ERR = -1;
	public static final byte HOST_USAGED = 1;
	
	public static final byte K8S_AUTO_NOT_INSTALL = 1;
	public static final byte K8S_AUTO_INSTALLED = 2;
	public static final byte K8S_MANUAL_INSTALLED = 3;
	
	//ku8 Application 类型
	public static final byte KU8_APPLICATION_TYPE_APP=0;//普通的APP
	public static final byte KU8_APPLICATION_TYPE_MICRO_SERVICE=1;//公共的组合式微服务类型
	
	//ku8微服务以及Ku8RcInst对象的的Flag标记 
	public static final byte DELETED_FLAG=-1;//删除标记
	public static final byte NO_K8S_SRV_RES_NOT_FOUND_FLAG_=-2;//找不到对应的k8s service资源描述
	public static final byte NO_K8S_RC_RES_NOT_FOUND_FLAG_=-3;//找不到对应的k8s rc资源描述
	public static final byte NO_K8S_RC_RES_SYNED_FLAG_=1;//k8s RES资源同步
	public static final byte NO_K8S_RC_RES_NOT_SYNED_FLAG_=-1;//k8s RES资源不同步
	//ku8微服务状态
	public static final byte KU8_MICRO_SERVICE_INIT_STATUS=0;//未发布
	public static final byte KU8_MICRO_SERVICE_PUBLISHIING_STATUS=1;//正在发布中
	public static final byte KU8_MICRO_SERVICE_RUNNING_STATUS=2;//已发布并且运行正常
	public static final byte KU8_MICRO_SERVICE_FAILED_STATUS=-1;//已发布并且异常
	
	//Ku8 Application状态
	public static final byte KU8_APP_INIT_STATUS=0;//未发布
	public static final byte KU8_APP_PUBLISHIING_STATUS=1;//正在发布中
	public static final byte KU8_APP_RUNNING_STATUS=2;//已发布并且运行正常
	public static final byte KU8_APP_FAILED_STATUS=-1;//已发布并且全部服务异常
	public static final byte KU8_APP_PART_FAILED_STATUS=-2;//已发布并且部分服务异常
	
}
