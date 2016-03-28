package io.ku8.docker.registry;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.jose4j.base64url.internal.apache.commons.codec.binary.Base64;
import org.jose4j.jwk.EllipticCurveJsonWebKey;
import org.jose4j.jwk.JsonWebKey.OutputControlLevel;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.keys.EllipticCurves;

public class Util {
	public static void writeFile(InputStream instream, File outFile) throws Exception {
		File parent=outFile.getParentFile();
		if(parent!=null&&!parent.exists())
		{
			parent.mkdirs();
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(outFile);
			byte[] b = new byte[1024];
			int readed = -1;
			while ((readed = instream.read(b)) != -1) {
				fos.write(b, 0, readed);
			}

		} finally {
			if (fos != null)
				fos.close();
		}

	}

	public static KeyPair generateECKeyPair() throws Exception {
		ECParameterSpec ecSpec = EllipticCurves.P256;
		KeyPairGenerator g = KeyPairGenerator.getInstance("EC");
		g.initialize(ecSpec, new SecureRandom());
		KeyPair pair = g.generateKeyPair();
		return pair;
	}

	public static String signMenifest(String menifestContent) throws Exception {
		KeyPair keyPair=Util.generateECKeyPair();
		List<JWSSignature> signatures = new ArrayList<JWSSignature>(1);
        String bodyEndPatten="schemaVersion";
		int lastPos = menifestContent.lastIndexOf(bodyEndPatten)+bodyEndPatten.length()+"\" : 1".length();
		String payloadPre=menifestContent.substring(0, lastPos) ;
		String payload = payloadPre+ "\n}";
		ECPublicKey pubKey = (ECPublicKey) keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();
		EllipticCurveJsonWebKey webKey = new EllipticCurveJsonWebKey(pubKey);
		// webKey.setKeyId("5HJ3:D3XS:5TAG:7MO3:ZDEU:EYX2:5M4T:5NYF:DJ3K:VUEO:WIOE:KDIH");
		// Create a new JsonWebSignature
		JsonWebSignature jws = new JsonWebSignature() {
			public String getAlgorithmHeaderValue() {
				return "ES256";
			}

		};

		// Set the payload, or signed content, on the JWS object
		jws.setPayload(payload);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		String encodedHeaderJson = "{\"formatLength\":" + lastPos + ",\"formatTail\":\"Cn0\",\"time\":\""
				+ df.format(new Date()) + "\"}";

		jws.getHeaders().setFullHeaderAsJsonString(encodedHeaderJson);
		
		jws.setKey(privateKey);
		jws.sign();
		// String jwsCompactSerialization = jws.getCompactSerialization();
		JWSSignature signature = new JWSSignature();
		signatures.add(signature);
		signature.setHeader(new JWKPubKey(webKey.toParams(OutputControlLevel.INCLUDE_SYMMETRIC)));
		signature.setProtectedHeader(Base64.encodeBase64URLSafeString(encodedHeaderJson.getBytes("UTF-8")));
		signature.setSignature(jws.getEncodedSignature());

		
		String signatureJson=new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(signature);
//		System.out.println(signatureJson);
		String signedTotalJson = payloadPre+",\n \"signatures\": [\n"+signatureJson+"\n]\n}";
		return signedTotalJson;

	}

	public static byte[] readFile(File fileName) throws IOException {
		FileInputStream in = null;
		try {

			byte[] tempbytes = new byte[1024];
			int byteread = 0;
			ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();
			in = new FileInputStream(fileName);
			while ((byteread = in.read(tempbytes)) != -1) {
				arrayStream.write(tempbytes, 0, byteread);

			}
			return arrayStream.toByteArray();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return null;
	}
}
