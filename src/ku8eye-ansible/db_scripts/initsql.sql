/*
Navicat MySQL Data Transfer
 
Source Server         : localhost
Source Server Version : 50613
Source Host           : localhost:3306
Source Database       : ku8eye

Target Server Type    : MYSQL
Target Server Version : 50613
File Encoding         : 65001

Date: 2015-11-24 15:57:56
*/
DROP DATABASE IF EXISTS ku8eye;
create database ku8eye DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
grant all privileges on ku8eye.* to 'ku8eye'@'localhost' identified  by '123456';
grant all privileges on ku8eye.* to 'ku8eye'@'%' identified  by '123456';
use ku8eye;
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `host`
-- ----------------------------
DROP TABLE IF EXISTS `host`;
CREATE TABLE `host` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `ZONE_ID` int(11) DEFAULT NULL COMMENT 'belongs which zone ',
  `HOST_NAME` char(32) NOT NULL COMMENT 'host name ',
  `IP` char(32) NOT NULL COMMENT 'ip addr',
  `ROOT_PASSWD` char(16) DEFAULT NULL COMMENT 'root password ',
  `LOCATION` varchar(128) DEFAULT NULL COMMENT 'host location ,etc room ',
  `NOTE` varchar(256) DEFAULT NULL COMMENT 'note for this record',
  `LAST_UPDATED` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'last updated time',
  `CORES` smallint(6) DEFAULT NULL,
  `MEMORY` mediumint(6) DEFAULT NULL,
  `USAGE_FLAG` tinyint(4) DEFAULT NULL,
  `SSH_LOGIN` tinyint(4) DEFAULT NULL,
  `CLUSTER_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of host
-- ----------------------------
INSERT INTO `host` VALUES ('1', '1', '10.255.242.203', '10.255.242.203', '123456', 'wuhan', null, '2015-11-24 15:20:04', '4', '524288', '0', '0', '0');
INSERT INTO `host` VALUES ('2', '1', '10.255.242.204', '10.255.242.204', '123456', 'wuhan', null, '2015-11-24 15:20:04', '4', '524288', '0', '0', '0');
INSERT INTO `host` VALUES ('3', '1', '10.255.242.205', '10.255.242.205', '123456', 'wuhan', null, '2015-11-24 15:20:04', '4', '524288', '0', '0', '0');
INSERT INTO `host` VALUES ('4', '1', '10.255.242.206', '10.255.242.206', '123456', 'wuhan', null, '2015-11-24 15:20:04', '4', '524288', '0', '0', '0');
INSERT INTO `host` VALUES ('5', '1', '10.255.242.207', '10.255.242.207', '123456', 'wuhan', null, '2015-11-24 15:20:04', '4', '524288', '0', '0', '0');

-- ----------------------------
-- Table structure for `ku8s_srv_endpoint`
-- ----------------------------
DROP TABLE IF EXISTS `ku8s_srv_endpoint`;
CREATE TABLE `ku8s_srv_endpoint` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `NODE_ROLE` tinyint(4) DEFAULT NULL COMMENT 'kubernetes node role(etcd,master ,node,docker registry ) ',
  `SERVICE_TYPE` tinyint(4) DEFAULT NULL COMMENT 'service type ',
  `CLUSTER_ID` int(11) DEFAULT NULL COMMENT 'belong to which cluster ',
  `HOST_ID` int(11) DEFAULT NULL COMMENT ' at witch host ',
  `SERVICE_URL` varchar(64) DEFAULT NULL COMMENT 'service url',
  `SERVICE_STATUS` tinyint(4) DEFAULT NULL COMMENT 'service status (ok ,bad)',
  `NOTE` varchar(256) DEFAULT NULL COMMENT 'note for this record',
  `SSH_PORT` int(5) DEFAULT NULL,
  `SSH_HOST` varchar(20) DEFAULT NULL,
  `SSH_PASS` varchar(50) DEFAULT NULL,
  `SSH_USER` varchar(50) DEFAULT NULL,
  `LAST_UPDATED` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'last updated time',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Records of ku8s_srv_endpoint
-- ----------------------------
INSERT INTO `ku8s_srv_endpoint` VALUES (1, 1, 1, 0, '10.255.242.203','http://10.255.242.203:1180', 1, NULL, NULL,NULL, NULL,NULL, NULL);
INSERT INTO `ku8s_srv_endpoint` VALUES (2, 1, 1, 1, '10.255.242.207', 'http://10.255.242.207:1186', 1, NULL, NULL,NULL, NULL,NULL, NULL);
-- ----------------------------
-- Table structure for `ku8_cluster`
-- ----------------------------
DROP TABLE IF EXISTS `ku8_cluster`;
CREATE TABLE `ku8_cluster` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `TENANT_ID` int(11) DEFAULT NULL COMMENT 'tenant ',
  `ZONE_ID` int(11) DEFAULT NULL COMMENT 'belongs which zone ',
  `NAME` char(32) NOT NULL COMMENT 'cluster name ',
  `LABELS` varchar(64) DEFAULT NULL COMMENT 'splitted labels',
  `K8S_VERSION` char(16) DEFAULT '1.0' COMMENT 'kuernetes version',
  `INSTALL_TYPE` tinyint(4) DEFAULT NULL COMMENT 'custom, all in one, normal ,ha ..',
  `NOTE` varchar(256) DEFAULT NULL COMMENT 'note for this record',
  `LAST_UPDATED` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'last updated time',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ku8_cluster
-- ----------------------------
INSERT INTO `ku8_cluster` VALUES ('0', '1', '1', 'test cluster', 'test', '1.0', '1', null, '2015-11-19 14:13:46');
INSERT INTO `ku8_cluster` VALUES ('1', '1', '1', 'test cluster', 'test', '1.0', '1', null, '2015-11-19 14:13:46');
-- ----------------------------
-- Table structure for `ku8_project`
-- ----------------------------
DROP TABLE IF EXISTS `ku8_project`;
CREATE TABLE `ku8_project` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `TENANT_ID` int(11) DEFAULT NULL COMMENT 'tenant ',
  `OWNER` char(16) DEFAULT NULL COMMENT 'creater :userid',
  `NAME` varchar(128) NOT NULL COMMENT ' project name ',
  `ICON_URL` varchar(128)  NULL COMMENT ' project icon url ',
  `VERSION` char(16) DEFAULT '1.0' COMMENT ' project version',
  `K8S_VERSION` char(16) DEFAULT '1.0' COMMENT 'kubernetes  version',
  `APP_TYPE` tinyint(4) DEFAULT 0  COMMENT 'application type ,0 means normal applicatio ,1 means micro service cluster',
  `ZONE_ID` int(11) DEFAULT NULL COMMENT 'belongs which zone ',
  `CLUSTER_ID` int(11) DEFAULT NULL COMMENT 'belong to which cluster ',
  `RES_PARTION_ID`  int(11) DEFAULT NULL  COMMENT 'deployed in this resource partion ',
  `JSON_SPEC` text COMMENT 'JSON spec content',
  `PREV_JSON_SPEC` text COMMENT 'prievious json spec content',
  `STATUS` tinyint(4) DEFAULT 0 ,
   `NOTE` varchar(256) DEFAULT NULL COMMENT 'note for this record',
  `LAST_UPDATED` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'last updated time',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ku8_project
-- ----------------------------
INSERT INTO `ku8_project` VALUES ('3', null, null, 'demo app', 'blank', '1.0', '1.0', '0', '1', '1', null, null, null, '0', null, '2015-12-31 15:32:12');
INSERT INTO `ku8_project` VALUES ('4', null, null, 'demo2 app', 'blank', '1.0', '1.0', '0', '1', '1', null, null, null, '0', null, '2015-12-31 15:32:25');



-- ----------------------------
-- Table structure for `ku8_service`
-- ----------------------------
DROP TABLE IF EXISTS `ku8_service`;
CREATE TABLE `ku8_service` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
   `PROJECTID` int(11) DEFAULT NULL COMMENT 'project Id ',
  `TENANT_ID` int(11) DEFAULT NULL COMMENT 'tenant ',
  `OWNER` char(16) DEFAULT NULL COMMENT 'creater :userid',
  `NAME` varchar(128) NOT NULL COMMENT ' service name ',
  `REPLICA` tinyint(4) DEFAULT NULL COMMENT 'service replica ',
  `ICON_URL` varchar(128) NOT NULL COMMENT ' service icon url ',
  `VERSION` char(16) DEFAULT '1.0' COMMENT ' project version',
  `K8S_VERSION` char(16) DEFAULT '1.0' COMMENT 'kubernetes  version',
  `ZONE_ID` int(11) DEFAULT NULL COMMENT 'belongs which zone ',
  `CLUSTER_ID` int(11) DEFAULT NULL COMMENT 'belong to which cluster ',
  `RES_PARTION_ID`  int(11) DEFAULT NULL  COMMENT 'deployed in this resource partion ',
  `JSON_SPEC` text COMMENT 'JSON spec content',
  `PREV_JSON_SPEC` text COMMENT 'prievious json spec content',
  `FLAG` tinyint(4) DEFAULT 0 ,
  `STATUS` tinyint(4) DEFAULT 0 ,
   `NOTE` varchar(256) DEFAULT NULL COMMENT 'note for this record',
  `LAST_UPDATED` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'last updated time',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for `ku8_res_partion`
-- ----------------------------
DROP TABLE IF EXISTS `ku8_res_partion`;
CREATE TABLE `ku8_res_partion` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `CLUSTER_ID` int(11) NOT NULL COMMENT 'k8s clusterId  ',
  `NAMESPACE` char(64) NOT NULL COMMENT 'k8s namespace  ',
  `POD_LIMIT` int(11) NOT NULL COMMENT 'pod count limit ',
  `CPU_LIMIT` int(11) NOT NULL COMMENT 'total cpu  limit ',
  `MEMORY_LIMIT` int(11) NOT NULL COMMENT 'total memory  limit ',
  `RC_LIMIT` int(11) NOT NULL COMMENT 'total RC  limit ',
  `SERVICE_LIMIT` int(11) NOT NULL COMMENT 'total service  limit ',
  `PV_LIMIT` int(11) NOT NULL COMMENT 'total persistance Volume  limit ',
  `NOTE` varchar(256) DEFAULT NULL COMMENT 'note for this record',
  `LAST_UPDATED` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'last updated time',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ku8_res_partion
-- ----------------------------
INSERT INTO `ku8_res_partion` VALUES ('2', '1', 'dev env', '20', '10', '1024', '50', '50', '50', null, '2015-11-24 11:21:22');
INSERT INTO `ku8_res_partion` VALUES ('3', '1', 'test env', '10', '5', '1024', '20', '30', '30', null, '2015-11-24 11:21:22');
INSERT INTO `ku8_res_partion` VALUES ('4', '1', 'uat env', '8', '5', '1024', '20', '30', '30', null, '2015-11-24 11:21:22');
INSERT INTO `ku8_res_partion` VALUES ('5', '1', 'default', '100', '20', '1024', '50', '50', '50', null, '2015-11-24 11:21:22');

-- ----------------------------
-- Table structure for `tenant`
-- ----------------------------
DROP TABLE IF EXISTS `tenant`;
CREATE TABLE `tenant` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `NAME` char(32) NOT NULL COMMENT 'tenant unique name   ',
  `ALIAS` varchar(128) NOT NULL COMMENT 'tenant display name ',
  `NOTE` varchar(256) DEFAULT NULL COMMENT 'note for this record',
  `LAST_UPDATED` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'last updated time',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tenant
-- ----------------------------
INSERT INTO `tenant` VALUES ('1', 'hp_cms', 'hp cms team', null, null);
INSERT INTO `tenant` VALUES ('2', 'develop_team', 'develop team', null, null);

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `USER_ID` char(16) NOT NULL COMMENT 'id of user',
  `ALIAS` varchar(64) DEFAULT NULL COMMENT 'user alias',
  `PASSWORD` char(8) NOT NULL,
  `USER_TYPE` tinyint(4) DEFAULT NULL COMMENT 'tenent user or admin user',
  `STATUS` tinyint(4) DEFAULT NULL COMMENT 'account status ,used for control login',
  `TENANT_ID` int(11) DEFAULT NULL COMMENT 'belongs witch tenant',
  `NOTE` varchar(256) DEFAULT NULL COMMENT 'note for this record',
  `LAST_UPDATED` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'last updated time',
  PRIMARY KEY (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('guest', 'guest', '123456', '1', '0', '2', null, '2015-11-19 14:07:25');
INSERT INTO `user` VALUES ('hpcms', 'hp cms', '123456', '1', '0', '1', null, '2015-11-19 14:08:14');
INSERT INTO `user` VALUES ('ku8eye', 'ku8 admin', '123456', '1', '0', null, 'demo init user', '2015-11-19 11:16:21');

-- ----------------------------
-- Table structure for `zone`
-- ----------------------------
DROP TABLE IF EXISTS `zone`;
CREATE TABLE `zone` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `NAME` char(32) NOT NULL COMMENT 'zone name ',
  `NOTE` varchar(256) DEFAULT NULL COMMENT 'note for this record',
  `LAST_UPDATED` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'last updated time',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of zone
-- ----------------------------
INSERT INTO `zone` VALUES ('1', 'beijing', null, '2015-11-19 13:54:43');


-- ----------------------------
-- Table structure for docker_image
-- ----------------------------
DROP TABLE IF EXISTS `docker_image`;

CREATE TABLE `docker_image` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `TITLE` varchar(64) NOT NULL COMMENT 'image display title ',
  `IMAGE_URL` varchar(128) COMMENT 'image url ',
  `IMAGE_NAME` varchar(512) NOT NULL COMMENT 'image name ',
  `VERSION` varchar(512) NOT NULL COMMENT 'image version ',
  `VERSION_TYPE` tinyint(4) DEFAULT '0',
  `PUBLIC_IMAGE` tinyint(4) DEFAULT '0',
  `SIZE` int(11) DEFAULT '0',
  `category` varchar(32) NOT NULL COMMENT 'image category ',
  `CLUSTER_ID` int(11) DEFAULT NULL COMMENT 'belong whitch cluster',
  `REGISTRY_ID` int(11) NOT NULL,
  `IMAGE_ICON_URL` varchar(1024) DEFAULT NULL COMMENT 'image icon url ',
  `STATUS` tinyint(4) DEFAULT '0',
  `BUILD_FILE` text,
  `AUTO_BUILD_COMMAND` varchar(512) DEFAULT NULL,
  `AUTO_BUILD` tinyint(4) DEFAULT '0',
  `NOTE` varchar(256) DEFAULT NULL COMMENT 'note for this record',
  `LAST_UPDATED` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'last updated time',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of docker_image
-- ----------------------------

INSERT INTO `docker_image` VALUES (48, 'Jre 8', '10.255.242.171:5000', 'jre', '8', 1, 0, 346660, 'middleware', 1, 21, 'logo_pic\\java.png', 0, 'FROM centos:locale \r\n\r\nENV JAVA_HOME /var/run/jre\r\nADD jre8 ${JAVA_HOME}\r\n', NULL, 0, 'auto imported', now());
INSERT INTO `docker_image` VALUES (49, 'tomcat 6', '10.255.242.171:5000', 'tomcat', '6', 1, 0, 357577, 'middleware', 1, 21, 'logo_pic\\tomcat.png', 0, 'FROM centos:locale\r\n\r\nENV JAVA_HOME /var/run/jre\r\nADD jre8 ${JAVA_HOME}\r\n\r\nENV TOMCAT_HOME /var/run/tomcat\r\nADD apache-tomcat-6.0.39 ${TOMCAT_HOME}\r\n\r\n\r\n', NULL, 0, 'auto imported', now());

-- ----------------------------
-- Table structure for `ku8_service_template`
-- ----------------------------
DROP TABLE IF EXISTS `ku8_service_template`;
CREATE TABLE `ku8_service_template` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `TENANT_ID` int(11) DEFAULT NULL COMMENT 'tenant ',
  `OWNER` char(16) DEFAULT NULL COMMENT 'creater :userid',
  `NAME` varchar(128) NOT NULL COMMENT ' template name ',
  `ICON_URL` varchar(128) NOT NULL COMMENT ' service icon url ',
  `VERSION` char(16) DEFAULT '1.0' COMMENT ' project version',
  `K8S_VERSION` char(16) DEFAULT '1.0' COMMENT 'kubernetes  version',
  `ZONE_ID` int(11) DEFAULT NULL COMMENT 'belongs which zone ',
  `CLUSTER_ID` int(11) DEFAULT NULL COMMENT 'belong to which cluster ',
  `JSON_SPEC` text COMMENT 'JSON spec content',
  `PREV_JSON_SPEC` text COMMENT 'prievious json spec content',
   `NOTE` varchar(256) DEFAULT NULL COMMENT 'note for this record',
  `LAST_UPDATED` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'last updated time',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
