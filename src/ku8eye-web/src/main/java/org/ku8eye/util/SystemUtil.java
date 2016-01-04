package org.ku8eye.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SystemUtil {

	public static final Properties getSpringAppProperties() throws IOException {
		InputStream inStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("application.properties");
		java.util.Properties props = new Properties();
		props.load(inStream);
		return props;
	}
}
