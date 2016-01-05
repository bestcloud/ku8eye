package org.ku8eye.util;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil
{
	private static final Logger log = Logger.getLogger(JSONUtil.class);

	public static final ObjectMapper mapper = new ObjectMapper();
	
	public static String getJSONString(Object obj)
	{
		try
		{
			return mapper.writeValueAsString(obj);
		}
		catch (JsonProcessingException e)
		{
			log.error("getJSONString error," + e);
			e.printStackTrace();
		}
		
		return null;
	}
}
