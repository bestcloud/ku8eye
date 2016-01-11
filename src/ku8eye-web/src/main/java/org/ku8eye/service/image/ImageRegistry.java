package org.ku8eye.service.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.ku8eye.domain.DockerImage;
import org.ku8eye.service.image.util.Shell;
import org.ku8eye.util.SystemUtil;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;

public class ImageRegistry {

	private String filePath;
	private String pathName;
	private String saveImagesName;
	private String registryUrl;
	private DockerImage dockerImage;
	static Properties prop = null;

	private DockerClient dockerClient;

	public ImageRegistry(String filePath, ImageShell imageShell)
			throws IOException {
		this.filePath = filePath;
		this.pathName = imageShell.getPath();
		this.saveImagesName = imageShell.getSaveImageName();
		this.registryUrl = imageShell.getRegistryUrl();
		this.dockerImage = imageShell.getImage();
		dockerClient = new DefaultDockerClient(SystemUtil
				.getSpringAppProperties().getProperty("docker.rest.api.uri"));
	}

	/**
	 * Load 一个docker image到本地的docker engine里
	 * @param registryURL
	 * @param absImageFileName
	 * @param dockerImageName
	 * @param tagName
	 */
	public void loadDockerImage(String registryURL,String absImageFileName,String dockerImageName,String tagName)
	{
		
	}
	/**
	 * push本地docker engine里的某个镜像到远端registryURL中
	 * @param registryURL
	 * @param dockerImageName
	 * @param tagName
	 */
	public void pushDockerImage(String registryURL,String dockerImageName,String tagName)
	{
		
	}

	/**
	 * 添加一个镜像到指定的docker registry里
	 * @param registryURL
	 * @param absImageFileName
	 * @param dockerImageName
	 * @param tagName
	 */
	public void addDockerImage(String registryURL,String absImageFileName,String dockerImageName,String tagName)
	{
		this.loadDockerImage(registryURL,absImageFileName,dockerImageName,tagName);
		this.pushDockerImage(registryURL, dockerImageName, tagName);
	}
	/**
	 * load and tag image
	 * 
	 * @throws FileNotFoundException
	 * @throws DockerException
	 * @throws InterruptedException
	 */
	public void load() throws FileNotFoundException, DockerException,
			InterruptedException {
		dockerClient.load(registryUrl + "/" + dockerImage.getImageName() + ":"
				+ dockerImage.getVersion(), new FileInputStream(new File(
				filePath + File.separator + pathName + File.separator
						+ saveImagesName)));
	}

	/**
	 * push image
	 * 
	 * @throws DockerException
	 * @throws InterruptedException
	 */
	public void push() throws DockerException, InterruptedException {
		dockerClient.push(registryUrl + "/" + dockerImage.getImageName() + ":"
				+ dockerImage.getVersion());
	}

	// 执行shell操作docker，暂时不用
	public String loadShell() throws Exception {
		// String result = HttpsUtil.doHttp(address, null, HTTP_METHOD_POST,
		// TIMEOUT);
		String imageID = "";
		Shell.runShell("docker load -i " + pathName + File.separator
				+ saveImagesName);
		List<String> returnList = Shell.runShell("docker images | grep"
				+ dockerImage.getImageName());
		for (String str : returnList) {
			str.replaceAll(" ", ",").replaceAll(",", ",");
			if (str.split(",")[1].equals(dockerImage.getVersion())) {
				imageID = str.split(",")[2];
				break;
			}
		}

		return imageID;
	}

	public boolean tagShell(String imageID) throws Exception {
		// String result = HttpsUtil.doHttp(address, null, HTTP_METHOD_POST,
		// TIMEOUT);
		List<String> returnList = Shell.runShell("docker tag " + imageID + " "
				+ prop.getProperty("docker.registry.url")
				+ dockerImage.getImageName() + dockerImage.getVersion());
		boolean result = true;
		for (String str : returnList) {
			System.out.println(str);
			if (str.indexOf("Error") >= 0) {
				result = false;
			}
		}
		return result;
	}

	public boolean pushShell() throws Exception {
		// String result = HttpsUtil.doHttp(address +
		// dockerImage.getImageName(),
		// null, HTTP_METHOD_PUT, TIMEOUT);
		List<String> returnList = Shell.runShell("docker push "
				+ prop.getProperty("docker.registry.url")
				+ dockerImage.getImageName() + dockerImage.getVersion());
		boolean result = true;
		for (String str : returnList) {
			System.out.println(str);
			if (str.indexOf("Error") >= 0) {
				result = false;
			}
		}
		return result;
	}
}
