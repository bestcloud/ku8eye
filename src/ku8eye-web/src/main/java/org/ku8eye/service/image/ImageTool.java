package org.ku8eye.service.image;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.ku8eye.domain.DockerImage;
import org.ku8eye.service.image.util.CommonUtil;
import org.ku8eye.service.image.util.DBOperator;
import org.ku8eye.service.image.util.FileUtil;
import org.ku8eye.util.SystemUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * import image info to registry and db
 * 
 * @author yaoy
 *
 */
public class ImageTool {

	static final ObjectMapper om = new ObjectMapper();

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("usage example: -p /var/image/ku8-images\r\n");
			return;
		}

		Options options = new Options();
		Option opt = new Option("h", "help", false, "Print help");
		opt.setRequired(false);
		options.addOption(opt);
		opt = new Option("p", "file", true, "json file");
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

		String filePath = commandLine.getOptionValue("p");// 最外层的文件夹家路径
		String name = filePath
				.substring(filePath.lastIndexOf(File.separator) + 1);// 最外层的文件夹名
		String jsonFilePath = filePath + File.separator + name + ".json";// json文件路径（json文件名为其父文件名加后缀）

		int clusterId = Integer.parseInt(commandLine.getOptionValue("c"));
		File file = new File(jsonFilePath);
		if (!file.exists()) {
			System.out.println("json file don't exit!" + jsonFilePath);
			return;
		}

		ImageShellArray imageList = om.readValue(file, ImageShellArray.class);
		ImageShell[] imageShellArray = imageList.getImageShell();
		for (int i = 0; i < imageShellArray.length; i++) {
			ImageShell imageShell = imageShellArray[i];
			String path = imageShell.getPath();
			DockerImage dockerImage = imageShell.getImage();
			dockerImage.setClusterId(clusterId);

			System.out.println("deal:'" + dockerImage.getImageName() + "'");
			if (CommonUtil.isBlank(path)
					|| CommonUtil.isBlank(dockerImage.getTitle())
					|| CommonUtil.isBlank(dockerImage.getImageName())
					|| CommonUtil.isBlank(dockerImage.getVersion())
					|| CommonUtil.isBlankByte(dockerImage.getVersionType())
					|| CommonUtil.isBlank(dockerImage.getCategory())
					|| dockerImage.getClusterId() == null) {
				System.out.println("deal fail:json file miss value");
				continue;
			}
			ImageTool imageTool = new ImageTool();

			Properties props = SystemUtil.getSpringAppProperties();
			String externalRes = props.getProperty("ku8.externalRes");
			String prex = "file:";
			if (!externalRes.startsWith(prex)) {
				throw new java.lang.RuntimeException(
						"invalid externalRes properties " + externalRes);
			}
			externalRes = externalRes.substring(prex.length());
			String uploadedPicturePath = externalRes + File.separator
					+ props.getProperty("ku8.uploadedPicturePath");
			FileUtil.CopyFile(filePath + File.separator + imageShell.getPath()
					+ File.separator + dockerImage.getImageIconUrl(),
					uploadedPicturePath, dockerImage.getImageIconUrl());// 目的路径待定
			String registryUrl = imageTool.getInfo(dockerImage);
			if (!CommonUtil.isBlank(registryUrl)) {
				imageShell.setRegistryUrl(registryUrl);
			}

			if (CommonUtil.isBlank(imageShell.getRegistryUrl())) {
				System.out.println("can't find RegistryUrl,exit");
				return;
			}
			if (imageTool.operateImage(filePath, imageShell)) {
				imageTool.addDB(filePath, imageShell);
			} else {
				System.out.println("deal fail");
			}
		}
	}

	/**
	 * add image info to db
	 * 
	 * @param filePath
	 * @param imageShell
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 */
	private void addDB(String filePath, ImageShell imageShell)
			throws ClassNotFoundException, SQLException, IOException {
		System.out.println("deal DB data");
		DBOperator.executeInsertImage(filePath, imageShell);
	}

	/**
	 * push image to registry
	 * 
	 * @param filePath
	 * @param imageShell
	 * @return
	 * @throws Exception
	 */
	private boolean operateImage(String filePath, ImageShell imageShell)
			throws Exception {
		System.out.println("deal registry");
		ImageRegistry imagesRegistry = new ImageRegistry(filePath, imageShell);
		imagesRegistry.load();
		imagesRegistry.push();
		return true;
	}

	private String getInfo(DockerImage dockerImage)
			throws ClassNotFoundException, SQLException, IOException {
		return DBOperator.getInfo(dockerImage);
	}
}
