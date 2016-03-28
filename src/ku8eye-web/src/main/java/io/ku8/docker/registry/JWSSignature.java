package io.ku8.docker.registry;

import org.codehaus.jackson.annotate.JsonProperty;

public class JWSSignature {
private JWKPubKey header;

private String signature;
@JsonProperty("protected")
private String protectedHeader;


public JWKPubKey getHeader() {
	return header;
}
public void setHeader(JWKPubKey header) {
	this.header = header;
}

public String getSignature() {
	return signature;
}
public void setSignature(String signature) {
	this.signature = signature;
}
public String getProtectedHeader() {
	return protectedHeader;
}
public void setProtectedHeader(String protectedHeader) {
	this.protectedHeader = protectedHeader;
}

}
