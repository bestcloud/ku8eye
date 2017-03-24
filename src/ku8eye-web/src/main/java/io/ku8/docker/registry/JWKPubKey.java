package io.ku8.docker.registry;

import java.util.Map;

public class JWKPubKey {
private Map<String,Object> jwk;
private String alg="ES256";

public JWKPubKey() {
	super();
}

public JWKPubKey(Map<String, Object> jwk) {
	super();
	this.jwk = jwk;
}

public Map<String, Object> getJwk() {
	return jwk;
}

public void setJwk(Map<String, Object> jwk) {
	this.jwk = jwk;
}

public String getAlg() {
	return alg;
}

public void setAlg(String alg) {
	this.alg = alg;
}

}
