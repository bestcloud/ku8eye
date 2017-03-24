package org.ku8eye.service.image.registry;

import java.util.ArrayList;
import java.util.List;

import org.ku8eye.service.image.registry.entity.Repositories;
import org.ku8eye.service.image.registry.entity.RepositoriesArray;
import org.ku8eye.util.JSONUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistryClient {

	public RegistryClient() {

	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	private String getRegistryUrl(String registryUrl) {
		registryUrl = registryUrl.replace("https", "http");
		registryUrl = registryUrl.endsWith("/") ? registryUrl.substring(0,
				registryUrl.length() - 1) : registryUrl;
		registryUrl = registryUrl.startsWith("http") ? registryUrl : "http://"
				+ registryUrl;
		return registryUrl;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Repositories> getRepositories(String registryUrl)
			throws Exception {
		registryUrl = getRegistryUrl(registryUrl);
		List<Repositories> repositoriesList = new ArrayList<Repositories>();
		String reqUri = registryUrl + "/" + "v2" + "/" + "_catalog";
		String re = HttpsUtil.doHttp(reqUri, null, "GET", 3000);
		RepositoriesArray repositoriesArray = new RepositoriesArray();
		repositoriesArray = JSONUtil.toObject(re, RepositoriesArray.class);
		for (String name : repositoriesArray.getRepositories()) {
			Repositories repositories = new Repositories();
			repositories.setName(name);
			repositoriesList.add(repositories);
		}
		return repositoriesList;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Repositories> getTags(String registryUrl,
			List<Repositories> repositoriesList) throws Exception {
		registryUrl = getRegistryUrl(registryUrl);
		List<Repositories> reRepositoriesList = new ArrayList<Repositories>();
		for (Repositories repositories : repositoriesList) {
			String name = repositories.getName();
			String re = HttpsUtil.doHttp(registryUrl + "/" + "v2" + "/" + name
					+ "/tags/list", null, "GET", 3000);
			Repositories repositori = new Repositories();
			repositori = JSONUtil.toObject(re, Repositories.class);
			reRepositoriesList.add(repositori);
		}
		return reRepositoriesList;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Repositories> getTags(String registryUrl) throws Exception {
		registryUrl = getRegistryUrl(registryUrl);
		List<Repositories> repositoriesList = getRepositories(registryUrl);
		return getTags(registryUrl, repositoriesList);
	}

//	@Transactional(propagation = Propagation.NOT_SUPPORTED)
//	public String deleteImage(String registryUrl, String imageName, String tag)
//			throws Exception {
//		registryUrl = getRegistryUrl(registryUrl);
//		String re = HttpsUtil.doHttp(registryUrl + "/" + "v2" + "/" + imageName
//				+ "/manifests/" + tag, null, "DELETE", 30000);
//		return re;
//	}

	public static void main(String[] args) throws Exception {
		RegistryClient registryClient = new RegistryClient();
		List<Repositories> repositoriesList = registryClient
				.getTags("192.168.100.125:5000");
		System.out.println(repositoriesList.toArray().toString());
	}
}
