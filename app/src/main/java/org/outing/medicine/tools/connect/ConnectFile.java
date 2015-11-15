package org.outing.medicine.tools.connect;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * 文件处理，使之可以通过json传输
 * 
 * @author Sun Yu Lin
 */
public class ConnectFile {

	/**
	 * TODO:将以Base64方式编码的字符串解码为byte数组
	 * 
	 * @param encodeString
	 *            待解码的字符串
	 * @return 解码后的byte数组，解码失败返回null
	 */
	public static byte[] decodeFile(String encodeString) {
		byte[] filebyte = null;
		try {
			filebyte = Base64Coder.decode(encodeString);
		} catch (Exception e) {
			filebyte = null;
		}
		return filebyte;
	}

	/**
	 * TODO:将文件以Base64方式编码为字符串
	 * 
	 * @param filepath
	 *            文件的绝对路径
	 * @return 编码后的字符串，编码失败返回null
	 * */
	public static String encodeFile(String filepath) {
		String result = "";
		try {
			FileInputStream fis = new FileInputStream(filepath);
			byte[] filebyte = new byte[fis.available()];
			fis.read(filebyte);
			fis.close();
			result = new String(Base64Coder.encode(filebyte));
		} catch (IOException e) {
			result = null;
		}
		return result;
	}

}