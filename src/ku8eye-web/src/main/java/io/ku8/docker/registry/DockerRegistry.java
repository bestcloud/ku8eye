package io.ku8.docker.registry;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * docker registry 2.0 client
 * 
 * @author wuzhih
 *
 */
public class DockerRegistry {

	private String registryURL;
	private CloseableHttpAsyncClient httpclient;
	Logger logger = LoggerFactory.getLogger(DockerRegistry.class);

	private Future<HttpResponse> asynHttpCall(HttpUriRequest request) {
		if (!httpclient.isRunning()) {
			httpclient.start();
		}
		return httpclient.execute(request, null);
	}

	private HTTPCallResult httpCall(HttpUriRequest request, File writeFile) throws Exception {
		Future<HttpResponse> response = asynHttpCall(request);
		HttpResponse httpResp = response.get(300, TimeUnit.SECONDS);
		HTTPCallResult result = new HTTPCallResult();
		result.setCode(httpResp.getStatusLine().getStatusCode());
		HttpEntity respEntity = httpResp.getEntity();
		if (respEntity != null) {
			String mineType = respEntity.getContentType().getValue();
			if (mineType.startsWith("application/octet-stream") || writeFile != null) {

				Util.writeFile(respEntity.getContent(), writeFile);
				// System.out.println(EntityUtils.toString(respEntity));
			} else {
				result.setContent(EntityUtils.toString(respEntity));
			}
			EntityUtils.consume(respEntity);
		}
		return result;

	}

	public boolean putImageManifest(String imageName, String tagName, File menifestFile) throws Exception {
		final HttpPut request = new HttpPut(registryURL + "/v2/" + imageName + "/manifests/" + tagName);

		HttpEntity fileEntity = EntityBuilder.create().setContentType(ContentType.APPLICATION_JSON)
				.setFile(menifestFile).build();
		request.setEntity(fileEntity);
		Future<HttpResponse> response = asynHttpCall(request);
		HttpResponse httpResp = response.get(300, TimeUnit.SECONDS);
		int retCode = httpResp.getStatusLine().getStatusCode();
		HttpEntity respEntity = httpResp.getEntity();
		if (respEntity != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("put image manifest result " + EntityUtils.toString(respEntity) + " from registry "
						+ this.registryURL);
			}
		}
		return retCode == 201;

	}

	public boolean pullImage(File unzipedImagePath, String imageName, String tag) throws Exception {
		File manifestFile = new File(unzipedImagePath, "manifest.json");
		int retCode = getImageManifest(imageName, tag, manifestFile);
		if (retCode == 200) {
			byte[] manifestData = Util.readFile(manifestFile);
			ObjectMapper objMapper = new ObjectMapper();
			ImageManifest manifest = objMapper.readValue(manifestData, ImageManifest.class);
			for (int i = 0; i < manifest.getHistory().length; i++) {

				V1LayerJson jsonLayer = manifest.getHistory()[i];
				String v1Json = jsonLayer.getV1Compatibility();
				V1JsonObj v1Obj = objMapper.readValue(v1Json, V1JsonObj.class);
				File layerDir = new File(unzipedImagePath, v1Obj.getId());
				Util.writeFile(new ByteArrayInputStream(v1Json.getBytes("utf-8")), new File(layerDir, "json"));
				String layBlobSum = manifest.getFsLayers()[i].getBlobSum();
				logger.info(" pull image layer " + layBlobSum);
				pullLayer(imageName, layBlobSum, new File(layerDir, "layer.tar"));

			}
			return true;
		} else {
			logger.warn("cant' find image " + imageName + " TAG:" + tag + " in registry " + this.registryURL);
			return false;
		}
	}

	public boolean pushImage(File unzipedImagePath, String imageName, String tag, boolean regenManifest)
			throws Exception {
		if (regenManifest) {
			RegistryUtil.genImageManifestFile(unzipedImagePath, imageName, tag);
		}
		File[] layerFiles = unzipedImagePath.listFiles();
		ArrayList<Future<HttpResponse>> layeruploadResponses = new ArrayList<Future<HttpResponse>>(layerFiles.length);
		for (File theFile : layerFiles) {
			if (theFile.getName().contains(".") || theFile.getName().equals("repositories")) {
				continue;
			}
			File layFile = new File(theFile, "layer.tar");
			String sharDigit = SHA256Digit.hash(layFile);
			String layerDigit = "sha256%3A" + sharDigit;
			FsLayer curLayer = new FsLayer();
			curLayer.setBlobSum("sha256:" + sharDigit);
			if (isLayerExists(imageName, layerDigit)) {
				logger.info("layer exists ,skip it :" + layFile.getParent());
				continue;
			}
			String url = postLayerUpload(imageName);
			logger.info("upload layer ,url " + url + " to registry " + this.registryURL);
			Future<HttpResponse> httpResp = uploadLayerFile(url, layerDigit, layFile);
			layeruploadResponses.add(httpResp);
		}

		boolean allFinished = false;
		while (!allFinished) {
			allFinished = true;
			for (Future<HttpResponse> httpResp : layeruploadResponses) {
				if (!httpResp.isDone()) {
					allFinished = false;
					break;
				}
			}
			Thread.sleep(1000);
		}
		logger.info("upload image layers Finished ");
		boolean allSuccess = true;
		for (Future<HttpResponse> httpResp : layeruploadResponses) {
			HttpResponse resp = httpResp.get();
			int errCode = resp.getStatusLine().getStatusCode();
			if (errCode != 201) {
				logger.warn("some layer(s) upload failed ");
				allSuccess = false;
				break;
			}
		}
		if (!allSuccess) {
			return false;
		}
		File meinfestFile = new File(unzipedImagePath, "manifest.json");
		logger.info("put image manifest file ");
		boolean suc = putImageManifest(imageName, tag, meinfestFile);
		logger.info(
				"push image " + imageName + " TAG " + tag + (suc ? " success " : "failed ") + " to " + this.registryURL);
		return suc;
	}

	/**
	 * return 200,means success
	 * 
	 * @param imageName
	 * @param tagName
	 * @return
	 * @throws Exception
	 */
	public int getImageManifest(String imageName, String tagName, File manifestFile) throws Exception {
		final HttpGet request = new HttpGet(registryURL + "/v2/" + imageName + "/manifests/" + tagName);
		HTTPCallResult result = httpCall(request, manifestFile);
		return result.getCode();
	}

	public boolean isLayerExists(String imageName, String layerdigest) throws Exception {
		final HttpHead request = new HttpHead(registryURL + "/v2/" + imageName + "/blobs/" + layerdigest);
		HTTPCallResult result = httpCall(request, null);
		return result.getCode() == 200;
	}

	public String postLayerUpload(String imageName) throws Exception {
		final HttpPost request = new HttpPost(registryURL + "/v2/" + imageName + "/blobs/uploads/");
		Future<HttpResponse> response = asynHttpCall(request);
		HttpResponse httpResp = response.get(300, TimeUnit.SECONDS);
		int retCode = httpResp.getStatusLine().getStatusCode();
		if (202 == retCode) {
			return httpResp.getLastHeader("Location").getValue();
		} else {
			return null;
		}
	}

	public Future<HttpResponse> uploadLayerFile(String uploadURL, String layerDigit, File layerFile)
			throws FileNotFoundException {
		// uploadURL=uploadURL.substring(0,uploadURL.indexOf('?'))+"?digest="+layerDigit;
		uploadURL = uploadURL + "&digest=" + layerDigit;
		if (logger.isDebugEnabled()) {
			logger.debug("upload layer file " + uploadURL + " to registry " + this.registryURL);
		}

		final HttpPut request = new HttpPut(uploadURL);
		HttpEntity fileEntity = EntityBuilder.create().setContentType(ContentType.APPLICATION_OCTET_STREAM).chunked()
				.setFile(layerFile).build();
		request.setEntity(fileEntity);
		return asynHttpCall(request);
	}

	public int pullLayer(String imageName, String layerdigest, File layerFile) throws Exception {
		final HttpGet request = new HttpGet(registryURL + "/v2/" + imageName + "/blobs/" + layerdigest);
		HTTPCallResult result = httpCall(request, layerFile);
		return result.getCode();
	}

	public String getCatalog() throws Exception {
		final HttpGet request = new HttpGet(registryURL + "/v2/_catalog");
		HTTPCallResult result = httpCall(request, null);
		return result.toString();
	}

	public DockerRegistry(String registryURL) {
		this.registryURL = registryURL;
		this.httpclient = HttpAsyncClients.createDefault();
	}

	public void close() {
		try {
			if(this.httpclient!=null&&this.httpclient.isRunning()){
				this.httpclient.close();
			}
		} catch (IOException e) {
			logger.warn("close http client err " + e);
		}
	}
}
