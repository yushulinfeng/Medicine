package org.outing.medicine.tools.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ToDealZip {

	/**
	 * 解压缩zip包
	 * 
	 * @param zipFilePath
	 *            zip文件路径
	 * @param targetPath
	 *            解压缩到的位置（末尾不用加/），如果为null或空字符串则默认解压缩到跟zip包同目录跟zip包同名的文件夹下
	 */
	public static void unzipFile(String zipFilePath, String targetPath)
			throws IOException {// 不太好try,就让它抛出异常吧
		OutputStream os = null;
		InputStream is = null;
		ZipFile zipFile = null;
		zipFile = new ZipFile(zipFilePath);
		String directoryPath = "";
		if (null == targetPath || "".equals(targetPath)) {
			directoryPath = zipFilePath.substring(0,
					zipFilePath.lastIndexOf("."));
		} else {
			directoryPath = targetPath;
		}
		@SuppressWarnings("rawtypes")
		Enumeration entryEnum = zipFile.entries();
		if (null != entryEnum) {
			ZipEntry zipEntry = null;
			while (entryEnum.hasMoreElements()) {
				zipEntry = (ZipEntry) entryEnum.nextElement();
				if (zipEntry.isDirectory()) {
					directoryPath = directoryPath + File.separator
							+ zipEntry.getName();
					System.out.println("文件解压至：" + directoryPath);
					continue;
				}
				if (zipEntry.getSize() > 0) {// 文件
					File targetFile = new File(directoryPath + File.separator
							+ zipEntry.getName());
					if (!targetFile.getParentFile().exists()) {
						targetFile.getParentFile().mkdirs();
					}
					os = new BufferedOutputStream(new FileOutputStream(
							targetFile));
					is = zipFile.getInputStream(zipEntry);
					byte[] buffer = new byte[6];
					int readLen = 0;
					while ((readLen = is.read(buffer, 0, 6)) >= 0) {
						os.write(buffer, 0, readLen);
					}
					os.flush();
					os.close();
					is.close();
				} else {// 空目录
					new File(directoryPath + File.separator
							+ zipEntry.getName()).mkdirs();
				}
			}
		}
		zipFile.close();
	}

	/**
	 * 压缩为zip包
	 * 
	 * @param inputFileName
	 *            要压缩的文件（文件、文件夹均可）
	 * @param outputFileName
	 *            压缩后的文件（要包含路径）
	 */
	public static void zipFile(String inputFileName, String outputFileName)
			throws Exception {
		File file = new File(inputFileName);
		String name = file.getName();
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				outputFileName));
		zipFolder(out, file, name);
		out.close();
	}

	// 以递归形式压缩文件夹（base是压缩包内部文件夹层次）
	private static void zipFolder(ZipOutputStream out, File file, String base)
			throws Exception {
		if (file.isDirectory()) {
			File[] subfile = file.listFiles();
			out.putNextEntry(new ZipEntry(base + "/"));
			base = base.length() == 0 ? "" : base + "/";
			for (int i = 0; i < subfile.length; i++) {
				zipFolder(out, subfile[i], base + subfile[i].getName());// 递归
			}
		} else {
			out.putNextEntry(new ZipEntry(base));
			FileInputStream in = new FileInputStream(file);
			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			in.close();
		}
	}

}
