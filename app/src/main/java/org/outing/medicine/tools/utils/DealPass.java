package org.outing.medicine.tools.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码加密解密类
 * 
 * @author Sun Yu Lin
 */
public class DealPass {

	/**
	 * 加密算法：将密码的char与秘钥的MD5的char相加，并转换为base64字符串。
	 * （因为key的细微变化都会使MD5几乎完全不同，所以传输保密性更好，此处不必考虑存储保密性）
	 * （可惜此方法不太适合前端，但一时又找不出更兼容的方法，查了一下好像前端加密更麻烦更复杂一些）
	 * 
	 * @param pass
	 *            要加密的密码，不能有中文，长度应小于32位
	 * @param key
	 *            秘钥（建议使用时间作为秘钥）
	 * @return 加密后的密码，长度128位
	 */
	public static String encodePass(String pass, String key) {
		if (pass.length() > 32)
			return "";
		String md5key = getMD5(key);// 加长秘钥
		String result = "";
		BigInteger big_pass = toNumber(pass);
		BigInteger big_key = toNumber(md5key);
		BigInteger big_result = big_pass.add(big_key);// 相加
		result = big_result.toString();
		result = Base64Coder.encodeString(result);// 编码
		return result;
	}

	/**
	 * 解密算法：将base64字符串解析然后减去秘钥的MD5的char，得到密码的char并转换为密码。
	 * 
	 * @param pass
	 *            已加密的密码
	 * @param key
	 *            加密时用的秘钥
	 * @return 解密后的密码
	 */
	public static String decodePass(String pass, String key) {
		String md5key = getMD5(key);// 加长秘钥
		String result = "";
		String str_result = "";
		try {
			str_result = Base64Coder.decodeString(pass);// 解码
		} catch (Exception e) {
			return "";
		}
		BigInteger big_result = toBigInteger(str_result);
		BigInteger big_key = toNumber(md5key);
		BigInteger big_pass = big_result.subtract(big_key);// 作差
		result = toString(big_pass);
		return result;
	}

	/**
	 * 将String以char形式读取，转换为BigInteger
	 */
	private static BigInteger toNumber(String string) {
		BigInteger result = BigInteger.ZERO;
		BigInteger temp = BigInteger.ZERO;
		BigInteger thousand = BigInteger.TEN.multiply(BigInteger.TEN).multiply(
				BigInteger.TEN);
		for (int i = 0; i < string.length(); i++) {
			temp = BigInteger.valueOf((int) (string.charAt(i)));
			result = result.multiply(thousand).add(temp);
		}
		return result;
	}

	/**
	 * 将BigInteger以char形式读取，转换为String
	 */
	private static String toString(BigInteger number) {
		String result = "";
		int lenth = number.toString().length();
		BigInteger temp = BigInteger.ZERO;
		BigInteger thousand = BigInteger.TEN.multiply(BigInteger.TEN).multiply(
				BigInteger.TEN);
		for (int i = 0; i < lenth; i += 3) {
			temp = number.mod(thousand);
			number = number.divide(thousand);
			result += (char) (Integer.parseInt(temp.toString()));
		}
		result = new StringBuffer(result).reverse().toString();
		return result;
	}

	/**
	 * 将以String保存的数字，转换为BigInteger类型的数字
	 */
	private static BigInteger toBigInteger(String string) {
		BigInteger result = BigInteger.ZERO;
		BigInteger temp = BigInteger.ZERO;
		for (int i = 0; i < string.length(); i++) {
			temp = BigInteger.valueOf(Long.parseLong(string.charAt(i) + ""));
			result = result.multiply(BigInteger.TEN).add(temp);
		}
		return result;
	}

	/**
	 * 获取文本数据的MD5编码
	 * 
	 * @param text
	 *            要编码的文本数据
	 * @return 数据的32位MD5字符串值
	 */
	public static String getMD5(String text) {// 返回32位MD5数组
		String result = "";
		MessageDigest message = null;
		byte[] bytes = null;
		try {
			message = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
		}
		bytes = message.digest(text.getBytes());
		result = new String(toHexString(bytes));
		return result;
	}

	/**
	 * 将byte数组转换为Hex字符串，这其实是HttpClient里面的codec.jar中Hex类中的encodeHex方法
	 * （这里没有必要导入整个包，所以只拿出来这个方法）
	 * 
	 * @param md
	 *            要转换的byte数组
	 * @return 转换后的字符串
	 */
	private static String toHexString(byte[] md) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		int j = md.length;
		char str[] = new char[j * 2];
		for (int i = 0; i < j; i++) {
			byte byte0 = md[i];
			str[2 * i] = hexDigits[byte0 >>> 4 & 0xf];
			str[i * 2 + 1] = hexDigits[byte0 & 0xf];
		}
		return new String(str);
	}

}
