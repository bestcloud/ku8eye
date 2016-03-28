package org.ku8eye.service.image.registry;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsUtil {

	/**
	 * do https
	 * 
	 * @param address
	 * @param body
	 * @param httpMethod
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	public static String doHttps(String address, String body,
			String httpMethod, int timeout) throws Exception {
		StringBuffer result = new StringBuffer();
		try {
			SSLContext context = SSLContext.getInstance("SSL");
			context.init(null,
					new TrustManager[] { new TrustAnyTrustManager() },
					new SecureRandom());
			URL url = new URL(address);
			HttpsURLConnection connection = (HttpsURLConnection) url
					.openConnection();
			connection = (HttpsURLConnection) url.openConnection();
			connection.setSSLSocketFactory(context.getSocketFactory());
			connection.setHostnameVerifier(new TrustAnyHostnameVerifier());
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
			connection.setRequestMethod(httpMethod);
			connection.setRequestProperty("Content-Type", "text/xml");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			if (body != null) {
				DataOutputStream dos = new DataOutputStream(
						connection.getOutputStream());
				dos.writeBytes(body);
				dos.flush();
				dos.close();
			}
			InputStream inputStream = connection.getInputStream();

			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);
			String strInformation = null;
			while ((strInformation = bufferedReader.readLine()) != null) {
				result.append(strInformation).append("\n");
			}
			inputStream.close();
			bufferedReader.close();
			inputStreamReader.close();
			connection.disconnect();
		} catch (Exception e) {
			throw e;
		}

		return result.toString();
	}

	/**
	 * do https
	 * 
	 * @param address
	 * @param body
	 * @param httpMethod
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	public static String doHttp(String address, String body, String httpMethod,
			int timeout) throws Exception {
		StringBuffer result = new StringBuffer();
		try {
			URL url = new URL(address);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
			connection.setRequestMethod(httpMethod);
			connection.setRequestProperty("Content-Type", "text/xml");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			if (body != null) {
				DataOutputStream dos = new DataOutputStream(
						connection.getOutputStream());
				dos.writeBytes(body);
				dos.flush();
				dos.close();
			}
			InputStream inputStream = connection.getInputStream();

			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);
			String strInformation = null;
			while ((strInformation = bufferedReader.readLine()) != null) {
				result.append(strInformation).append("\n");
			}
			inputStream.close();
			bufferedReader.close();
			inputStreamReader.close();
			connection.disconnect();
		} catch (Exception e) {
			throw e;
		}

		return result.toString();
	}
}

class TrustAnyHostnameVerifier implements HostnameVerifier {
	public boolean verify(String hostname, SSLSession session) {
		return true;
	}
}

class TrustAnyTrustManager implements X509TrustManager {
	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
	}

	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[] {};
	}
}
