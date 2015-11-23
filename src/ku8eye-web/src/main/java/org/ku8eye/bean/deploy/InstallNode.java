package org.ku8eye.bean.deploy;
/**
 * it used in Ku8ClusterTemplate  ,which is  an install node 
 * @author wuzhih
 *
 */
public class InstallNode {
private int hostId;
private String ip;
private String hostName;
//node role ,for example etcd 、master、 node ,docker registry
private String nodeRole;
//node specific params
private String nodeParams;
}
