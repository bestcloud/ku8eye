package org.ku8eye.service.image;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.ku8eye.bean.Ku8PackageImportProcess;
import org.ku8eye.service.image.util.CommonUtil;
import org.ku8eye.service.image.util.DBOperator;
import org.ku8eye.util.SystemUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * import image info to registry and db
 * 
 * @author yaoy
 *
 */
public class ImageTool {

	private static Logger log = Logger.getLogger(ImageTool.class);
	static final ObjectMapper om = new ObjectMapper();

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			log.info("usage example: -f /var/image/ku8-images/ku8-images.tar.gz\r\n");
			return;
		}

		Options options = new Options();
		Option opt = new Option("h", "help", false, "Print help");
		opt.setRequired(false);
		options.addOption(opt);
		opt = new Option("f", "file", true, "gz file");
		opt.setRequired(true);
		options.addOption(opt);
		opt = new Option("c", "clusterid", true, "clusterid");
		opt.setRequired(false);
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

		String filePath = commandLine.getOptionValue("f");// 解压后的文件夹名字
		String clusterId = commandLine.getOptionValue("c");
		clusterId = CommonUtil.isBlank(clusterId) ? "1" : clusterId;

		String[] dbArray = DBOperator.getDBInfo(Integer.parseInt(clusterId));

		if (dbArray[0].isEmpty()) {
			String err = "err:no registry info in table ku8s_srv_endpoint,exit";
			log.error(err);
			return;
		}
		int registryId = Integer.parseInt(dbArray[0]);
		String registryUrl = dbArray[1];

		Properties props = SystemUtil.getSpringAppProperties();
		String externalRes = props.getProperty("ku8.externalRes");
		String prex = "file:";
		externalRes = externalRes.substring(prex.length());

		List<DockerImageImportBean> dockerImageImportBeanList = ImageRegistry
				.unZipAndParseKu8ImagePackage(filePath, externalRes,
						registryId, registryUrl, Integer.parseInt(clusterId));

		DBOperator.exitImage(dockerImageImportBeanList);
		for (DockerImageImportBean dockerImageImportBean : dockerImageImportBeanList) {
			dockerImageImportBean.setExecute("1");
		}
		if (dockerImageImportBeanList.size() > 0) {
			Ku8PackageImportProcess ku8PackageImportProcess = new Ku8PackageImportProcess(
					new ImageRegistry(), dockerImageImportBeanList);
			ku8PackageImportProcess.start();
			while (!ku8PackageImportProcess.isFinished()) {
				Thread.sleep(1000);
			}

			Collection<DockerImageImportBean> dockerImageImportBeanC = ku8PackageImportProcess
					.getDockerImages();
			log.info("deal report:");
			for (DockerImageImportBean ockerImageImportBean : dockerImageImportBeanC) {
				log.info("  " + ockerImageImportBean.getImage().getImageUrl()
						+ File.separator
						+ ockerImageImportBean.getImage().getImageName() + ":"
						+ ockerImageImportBean.getImage().getVersion() + "--"
						+ ockerImageImportBean.getProcessHint());
			}

			if (ku8PackageImportProcess.isSuccess()) {
				log.info("deal success!");
			} else {
				log.info("deal fail!");
			}
		} else {
			log.error("json file erro, check your json file");
		}

		log.info("import images finish");
	}
}
