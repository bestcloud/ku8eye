package org.ku8eye.bean.image;

public class ImageResponse {

	private int status;
	private String message;
	private Object data;

	public ImageResponse() {
	}
	
	public ImageResponse(int status) {
		this(status, null);
	}

	public ImageResponse(int status, String message) {
		this(status, message, null);
	}

	public ImageResponse(int status, String message, Object data) {
		this.setStatus(status);
		this.setMessage(message);
		this.data = data;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
