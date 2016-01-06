package org.ku8eye.service.image.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class FileUtil {

	public static String readFile(String Path) throws IOException {
		BufferedReader reader = null;
		String laststr = "";
		try {
			FileInputStream fileInputStream = new FileInputStream(Path);
			InputStreamReader inputStreamReader = new InputStreamReader(
					fileInputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				laststr += tempString + "\r\n";

			}
			reader.close();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return laststr;
	}

	public static void CopyFile(String fileFrom, String path, String fileName)
			throws IOException {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			File file = new File(fileFrom);
			if (!file.exists()) {
				System.out.println("warning:no logo");
				return;
			}
			inputStream = new FileInputStream(file);
			File fileto = new File(path + File.separator + fileName);
			if (fileto.exists()) {
				System.out.println("logo icon exist");
				return;
			}
			new File(path).mkdirs();
			outputStream = new FileOutputStream(fileto);

			int bytesWritten = 0;
			int byteCount = 0;
			byte[] bytes = new byte[Integer.parseInt(file.length() + "")];

			while ((byteCount = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, bytesWritten, byteCount);
				bytesWritten += byteCount;
			}
		} finally {
			if (outputStream != null) {
				inputStream.close();
			}
			inputStream.close();
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

}