package org.ku8eye.service.deploy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.ku8eye.Constants;
import org.ku8eye.bean.deploy.InstallNode;
import org.ku8eye.bean.deploy.Ku8ClusterTemplate;

public class Ku8InstallTool {

	private Ku8ClusterDeployService deployService;

	public Ku8InstallTool() {
		deployService = new Ku8ClusterDeployService();
		TemplateUtil tempUtil = new TemplateUtil();
		tempUtil.setHostsFile("/templates/hosts:hosts");
		tempUtil.setScriptRoot("/root/kubernetes_cluster_setup");
		tempUtil.setSshKeyHostsFile("/templates/ssh-hosts:ssh-hosts");
		tempUtil.setTmpFileYML(
				"default-global:/templates/all.yml:group_vars/all.yml,docker-registry:/templates/docker_registry.yml:roles/docker-registry/defaults/main.yml,etcd:/templates/etcd.yml:roles/etcd/defaults/main.yml,kube-master:/templates/kuber_master.yml:roles/kube-master/defaults/main.yml,kube-node:/templates/kuber_node.yml:roles/kube-node/defaults/main.yml");
		deployService.setTmpUtil(tempUtil);
	}

	private Ku8ClusterTemplate getTemp(String[] hosts, String rootPass, String clusterDocker0Ip) throws Exception {
		Ku8ClusterTemplate template = deployService.getAllTemplates().get(0).clone();
		if (hosts.length == 1) {
			InstallNode node = template.getStandardAllIneOneNode();
			node.setIp(hosts[0]);
			node.setRootPassword(rootPass);
			template.addNewNode(node);
		} else {
			// master node
			InstallNode node = template.getStandardMasterWithEtcdNode();
			node.setIp(hosts[0]);
			node.setRootPassword(rootPass);
			template.addNewNode(node);
			// minion nodes
			for (int i = 1; i < hosts.length; i++) {
				node = template.getStandardK8sNode();
				node.setIp(hosts[i]);
				node.setRootPassword(rootPass);
				template.addNewNode(node);
			}
		}
		return template;
	}

	public void waitAnsibleCallFinish(ProcessCaller caller, int timeOutSeconds) {
		long timeOutMillis = System.currentTimeMillis() + timeOutSeconds * 1000;
		List<String> totalOutResults = new LinkedList<String>();
		while (System.currentTimeMillis() < timeOutMillis && !caller.isFinished()) {
			try {
				Thread.sleep(1000);
				ArrayList<String> results = new ArrayList<String>(caller.getOutputs());
				caller.getOutputs().removeAll(results);
				totalOutResults.addAll(results);
				if (!results.isEmpty()) {
					timeOutMillis += 3000;
				}

				for (String line : results) {
					System.out.println(line);
				}

			} catch (InterruptedException e) {

			}
		}
//		if (!caller.isFinished()) {
//			caller.shutdownCaller();
//		}
		 AnsibleCallResult parseResult=AnsibleResultParser.parseResult(totalOutResults);
		 System.out.println("____________________________Report______________________________");
		 parseResult.printInfo();
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("usage example: -n 192.168.1.10 -cluster-docker0-ip 172.0.0.0/16  -rootpass 111111\r\n");
			return;

		}

		Options options = new Options();
		Option opt = new Option("h", "help", false, "Print help");
		opt.setRequired(false);
		options.addOption(opt);
		opt = new Option("n", "nodes", true,
				"first is master node  ,others are minion nodes (can install all on One host ),for example : -hosts 192.168.1.10,192.168.1.12,192.168.1.13  ,means 192.168.1.10 is master node");
		opt.setRequired(true);
		options.addOption(opt);
		opt = new Option("c", "cluster-docker0-ip-range", true, "cluster docker0's ip range,default is 172.16.0.0/16");
		opt.setRequired(false);
		options.addOption(opt);
		opt = new Option("p", "rootpass", true, "root password of all hosts ,must be the same !");
		opt.setRequired(true);
		options.addOption(opt);
		CommandLineParser parser = new DefaultParser();

		HelpFormatter hf = new HelpFormatter();
		hf.setWidth(180);
		CommandLine commandLine = parser.parse(options, args);
		if (commandLine.hasOption('h')) {
			// 打印使用帮助
			hf.printHelp("ku8eye", options, true);
			return;
		}
		String[] hosts = commandLine.getOptionValue("n").split(",");
		String clusterIPrange = commandLine.getOptionValue("c");
		clusterIPrange = (clusterIPrange == null) ? "172.16.0.0/16" : clusterIPrange;
		String rootPasswd = commandLine.getOptionValue("p");
		System.out.println("install on hosts " + Arrays.toString(hosts));
		Ku8InstallTool tool = new Ku8InstallTool();
		Ku8ClusterTemplate template = tool.getTemp(hosts, rootPasswd, clusterIPrange);
		template.getAllGlobParameters().get(Constants.k8sparam_cluster_docker0_ip_srange).setValue(clusterIPrange);
		List<String> errmsgs = tool.deployService.validateTemplate(template);
		if (errmsgs != null && !errmsgs.isEmpty()) {
			System.out.println("there are some error params ,please check !");
			for (String msg : errmsgs) {
				System.out.println(msg);
				return;
			}
		}
		tool.deployService.createInstallScripts(template);
		System.out.println("generate ssh key  ........");
		tool.deployService.deployKeyFiles();
		ProcessCaller caller = tool.deployService.getProcessCaller();
		tool.waitAnsibleCallFinish(caller, 120);
		if (!caller.isNormalExit()) {
			System.out.println(caller.toString());
			System.out.println("bad ansible result ,skip back step ");
			return;
		}
		// 关闭防火墙的测试
		System.out.println("close Firewalld ........");
		tool.deployService.disableFirewalld();
		caller = tool.deployService.getProcessCaller();
		tool.waitAnsibleCallFinish(caller, 120);
		if (!caller.isNormalExit()) {
			System.out.println(caller.toString());
			System.out.println("bad ansible result ,skip back step ");
			return;
		}

		// 安装kubernetes集群
		System.out.println("install kubernetes .......");
		tool.deployService.installK8s();
		caller = tool.deployService.getProcessCaller();
		tool.waitAnsibleCallFinish(caller, 300);
		if (!caller.isNormalExit()) {
			System.out.println(caller.toString());
			System.out.println("bad ansible result ,skip back step ");
			return;
		}

	}

}
