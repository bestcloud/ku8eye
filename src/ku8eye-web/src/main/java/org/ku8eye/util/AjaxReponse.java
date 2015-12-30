package org.ku8eye.util;

public class AjaxReponse
{
	private final int status;
	private final String message;
	
	public AjaxReponse(int status, String message)
	{
		this.status = status;
		this.message = message;
	}

	public int getStatus()
	{
		return status;
	}

	public String getMessage()
	{
		return message;
	}
}
