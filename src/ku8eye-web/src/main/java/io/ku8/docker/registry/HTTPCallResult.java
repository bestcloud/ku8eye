package io.ku8.docker.registry;

/**
 * 存放HTTP调用返回结果，包括返回码，以及返回的内容（字符串)
 * 
 * @author wuzhih
 *
 */
public class HTTPCallResult {
	private int code;
	private String content;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "HTTPCallResult [code=" + code + ", content=" + content + "]";
	}

}
