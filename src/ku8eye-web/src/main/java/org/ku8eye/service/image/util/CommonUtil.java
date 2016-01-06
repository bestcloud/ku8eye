package org.ku8eye.service.image.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

/**
 * 最基本的常用工具类
 * 
 * */
public class CommonUtil {
	private static Logger _logger = Logger.getLogger(CommonUtil.class);
	static final int ZIPBUFFER = 8192;
	public static final String numberic = "^(-?\\d+)(\\.\\d+)?$";

	/**
	 * 判断传入字符串是否为null或“”
	 * */
	public static boolean isBlank(String str) {
		return null == str || "".equals(str.trim());
	}

	/**
	 * 判断传入byte是否为null或“”
	 * */
	public static boolean isBlankByte(Byte b) {
		return null == b || "".equals(b.toString());
	}

	/**
	 * 判断传入字符串是否为null或“”或“null”
	 * */
	public static boolean isBlankOrnullStr(String str) {
		return str == null || str.trim().equals("")
				|| str.equalsIgnoreCase("null");
	}

	/**
	 * 判断传入字符串是否为null或“null”
	 * */
	public static boolean isNull(String str) {
		return str == null || str.trim().equalsIgnoreCase("null");
	}

	public static String null2Blank(String str) {
		return (null == str) ? "" : str;
	}

	public static boolean isNumberic(String str) {
		if (isBlank(str)) {
			return false;
		}
		return str.matches(numberic);
	}

	public static String readToString(File file) {
		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(filecontent);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(filecontent);
	}

	/**
	 * 查找指定字符串指定位置的正则
	 * 
	 * @param regex
	 *            正则表达式
	 * @param buffer
	 *            匹配的字符串
	 * @param index
	 *            匹配第几个
	 * @return String 匹配到的字符串
	 * */
	public static String findMatchedValue(String regex, String buffer, int index) {
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(buffer);
		boolean b = matcher.find();
		if (b && matcher.groupCount() >= index) {
			return matcher.group(index);
		}
		return null;
	}

	/**
	 * 判断字符串是否满足指定正则格式
	 * 
	 * @param regex
	 *            正则表达式
	 * @param buffer
	 *            匹配的字符串
	 * @return boolean 字符串是否符合正则规则
	 * */
	public static boolean match(String regex, String buffer) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(buffer);
		return matcher.matches();
	}

	/**
	 * ???
	 * */
	public static boolean isMatched(String name, String postfix) {

		if (name != null
				&& name.length() >= postfix.length()
				&& name.substring(name.length() - postfix.length()).equals(
						postfix))
			return true;

		return false;
	}

	/**
	 * 将字符串按照指定的字符分割
	 * 
	 * @param line
	 *            待分割字符串
	 * @param delim
	 *            匹配分割字符串
	 * @return List<String> 返回list类型的结果
	 * */
	public static List<String> split(String line, String delim) {
		List<String> lst = new LinkedList<String>();
		int beginPos = 0;
		while (true) {
			int endPos = line.indexOf(delim, beginPos);
			if (endPos == -1) {
				if (beginPos <= line.length()) {
					String buf = line.substring(beginPos, line.length());
					lst.add(buf);
				} else {
					lst.add("");
				}

				break;
			}

			String buf = line.substring(beginPos, endPos);
			lst.add(buf);
			beginPos = endPos + 1;
		}
		return lst;
	}

	/**
	 * 将字符串按照指定的字符分割，取指定个数结果
	 * 
	 * @param line
	 *            待分割字符串
	 * @param delim
	 *            匹配分割字符串
	 * @param limit
	 *            返回结果个数
	 * @return List<String> 返回list类型的结果
	 * */
	public static List<String> split(String line, String delim, int limit) {
		int count = 0;
		List<String> lst = new LinkedList<String>();
		int beginPos = 0;
		while (true) {
			int endPos = line.indexOf(delim, beginPos);
			if (endPos == -1) {
				if (beginPos <= line.length()) {
					lst.add("");
				}
				break;
			}

			String buf = line.substring(beginPos, endPos);
			lst.add(buf);
			beginPos = endPos + 1;
			count++;
			if (count >= limit)
				break;
		}
		return lst;
	}

	public static String replaceAll(String source, String findWhat,
			String replaceWith) {
		if (isBlank(source) || isBlank(findWhat) || isBlank(replaceWith))
			return source;
		int index = source.indexOf(findWhat);
		while (index >= 0) {
			StringBuilder temp = new StringBuilder(source);
			index = source.indexOf(findWhat);
			temp.delete(index, index + findWhat.length());
			temp.insert(index, replaceWith);

			source = temp.toString();
			index = source.indexOf(findWhat);
		}

		return source;
	}

	public static void createFolder(final String path) {
		_logger.info("create folder :" + path);
		final File p = new File(path);
		if (!p.exists())
			p.mkdirs();
	}

	public static void deleteFolder(final String path) {
		deleteFolder(new File(path));
	}

	public static void deleteFolder(final File path) {
		final File[] dirs = path.listFiles();
		if (dirs != null) {
			for (final File subPath : dirs) {
				if (subPath.isDirectory()) {
					deleteFolder(subPath);
				} else {
					subPath.delete();
				}
			}
		}
		path.delete();
	}

	public static List<File> getSubPath(String path) throws Exception {
		List<File> lstFolder = new LinkedList<File>();
		File f = new File(path);
		for (File p : f.listFiles()) {
			if (p.isDirectory()) {
				lstFolder.add(p);
			}
		}
		return lstFolder;
	}

	/**
	 * 在指定路径创建文件并写入内容
	 * 
	 * @param path
	 *            路径
	 * @param fileName
	 *            文件名字
	 * @param content
	 *            写入文件内容
	 * 
	 * */
	public static void createFileAndWriteContent(String path, String fileName,
			String content) throws IOException {
		FileWriter fw = new FileWriter(new File(path + File.separator
				+ fileName));
		fw.write(new String(content));
		fw.write(new String("\r\n"));
		if (fw != null) {
			fw.close();
			fw = null;
		}
	}

	/**
	 * 在指定路径创建文件并写入内容
	 * 
	 * @param file
	 *            文件全路径
	 * @param content
	 *            写入文件内容
	 * */
	public static void createFileAndWriteContent(String file, String content)
			throws IOException {
		FileWriter fw = new FileWriter(new File(file));
		fw.write(new String(content));
		fw.write(new String("\r\n"));
		if (fw != null) {
			fw.close();
			fw = null;
		}
	}

	/**
	 * 将文件压缩成.zip文件
	 * */
	public static boolean zipFile(File file, String zipFile) {
		if (!file.exists())
			return false;

		BufferedInputStream bis = null;
		ZipOutputStream out = null;
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(new File(
					zipFile));
			CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream,
					new CRC32());
			out = new ZipOutputStream(cos);
			bis = new BufferedInputStream(new FileInputStream(file));
			out.putNextEntry(new ZipEntry(file.getName()));
			int count;
			byte data[] = new byte[ZIPBUFFER];
			while ((count = bis.read(data, 0, ZIPBUFFER)) != -1) {
				out.write(data, 0, count);
			}

		} catch (Exception e) {
			_logger.error(e);
			return false;
		} finally {
			try {
				if (bis != null)
					bis.close();
				if (out != null)
					out.close();
			} catch (Exception e2) {
				_logger.error(e2);

				return false;
			}
		}

		return true;

	}

	public static List<String> getMatchedFile(String path, String postfix)
			throws Exception {

		List<String> lstFile = new LinkedList<String>();
		File f = new File(path);
		for (File p : f.listFiles()) {
			if (p.isFile()) {
				if (postfix != null) {
					if (p.getName().length() >= postfix.length()
							&& p.getName()
									.substring(
											p.getName().length()
													- postfix.length())
									.equals(postfix))
						lstFile.add(p.getPath());
				} else {
					lstFile.add(p.getPath());
				}
			}
		}
		return lstFile;
	}

	public static String getFormattedDigit(int data, int length) {
		NumberFormat idFormat = NumberFormat.getInstance();
		idFormat.setGroupingUsed(false);
		idFormat.setMinimumIntegerDigits(length);
		return idFormat.format(data);
	}

	public static String getFormattedChar(String data, String supplement,
			int resultLength) {
		StringBuffer sb = new StringBuffer();
		data = data.trim();
		int dataLength = data.trim().length();
		while (resultLength > dataLength) {
			sb.append(supplement);
			dataLength++;
		}
		sb.append(data);
		return sb.toString();

	}

	/**
	 * 获取当前主机的名字
	 * */
	public static String getLocalHostName() {
		String hostName = "";
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			hostName = inetAddress.getHostName();
		} catch (Exception e) {
			_logger.error(e);
		}
		return hostName;
	}

	/**
	 * 获取“通用唯一识别码 ”
	 * */
	public static String generateUUDI() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	/**
	 * Format a string using int type
	 * 
	 * @param v
	 * @return
	 */
	public static int string2int(String v) {
		if (v == null || v.trim().equals(""))
			return 0;

		try {
			return Integer.parseInt(v.trim());
		} catch (NumberFormatException e) {
			_logger.warn("input String:" + v, e);
		}
		return 0;
	}

	/**
	 * Format a String using long type
	 * 
	 * @param v
	 * @return
	 */
	public static long string2long(String v) {
		if (v == null || v.trim().equals(""))
			return 0;

		try {
			return Long.parseLong(v.trim());
		} catch (NumberFormatException e) {
			_logger.warn("input String:" + v, e);
		}
		return 0;
	}

	/**
	 * Make a string representation of the exception.
	 * 
	 * @param e
	 *            The exception to stringify
	 * @return A string with exception name and call stack.
	 */
	public static String stringifyException(Throwable e) {
		StringWriter stm = new StringWriter();
		PrintWriter wrt = new PrintWriter(stm);
		e.printStackTrace(wrt);
		wrt.close();
		return stm.toString();
	}

	public static void main(String[] args) {

	}
}
