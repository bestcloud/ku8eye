package org.ku8eye.util;

import java.io.IOException;
import java.util.List;

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
	
	public static <T> T toObject(String json,Class<T> c)
	{
		try
		{
			return mapper.readValue(json, c);
		}
		catch (JsonProcessingException e)
		{
			log.error("json to object error," + e);
			e.printStackTrace();
		}
		catch (IOException e)
		{
			log.error("read io error," + e);
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T> List<T> toObjectList(String json, Class<T> c)
	{
		try
		{
			return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, c));
		}
		catch (JsonProcessingException e)
		{
			log.error("json to object error," + e);
			e.printStackTrace();
		}
		catch (IOException e)
		{
			log.error("read io error," + e);
			e.printStackTrace();
		}
		return null;
	}
}
