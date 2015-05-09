package com.pluginx.plugin.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.json.JSONException;

import com.pluginx.plugin.util.MD5;

/**
 * 将APK 编译成 插件包
 * 
 * @author michaelzuo
 * 
 */
public class PluginBuilder {
	
	/**
	 * 后缀名称
	 */
	private static final String SUFFIX = ".plugin";
	
	/**
	 * 插件APK文件名称
	 */
	private static final String PLUGIN_APK_FILE_NAME = "test_one_plugin.apk";

	/**
	 * 输入目录 存放等待编译的APK
	 */
	private static final String INPUT = "E:\\workspace\\map\\TencentMapPluginTools\\input\\";

	/**
	 * 输出目录 输出编译好的插件包
	 */
	private static final String OUTPUT = "E:\\workspace\\map\\TencentMapPluginTools\\output\\";

	private static final String CLASS_NAME = "com.tencent.map.plugin.business.TestActivity";

	private static final String URL = "";

	/**
	 * 前三位 例如5_0_0为插件最低支持的客户端版本
	 * 后两位 例如 0_1为插件自身版本
	 */
	private static final String VERSION = "5_0_0_0_1";

	/**
	 * manifest文件名称
	 */
	private static final String MANIFEST_FILE_NAME = "manifest.mf";

	private static final String TEST_UNZIPPED = "E:\\workspace\\map\\TencentMapPluginTools\\test_unzipped\\";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String filePath = INPUT + PLUGIN_APK_FILE_NAME;
		try {
			String zipFilePath = buildOnePlugin(filePath);
			boolean isSuccessfual = unzipOnePlugin(zipFilePath,
					TEST_UNZIPPED);

			System.out.println("unzipPlugin is successfual:" + isSuccessfual);
			
			if (isSuccessfual) {
				
			}

			File pluginFile = new File(zipFilePath);
			String[] splitPluginNameAndVersion = splitPluginNameAndVersion(pluginFile.getName());
			System.out.println("pluginName:" + splitPluginNameAndVersion[0] + ", version:" + splitPluginNameAndVersion[1]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		String minHostVersion = getMinHostVersion(VERSION);
		String pluginVersion = getPluginVersion(VERSION);
		
		System.out.println("hostVersion:" + minHostVersion + ", pluginVersion:" + pluginVersion);
	}

	/**
	 * 构建一个插件
	 * 
	 * @param onePluginPath
	 *            一个插件文件夹的路径,文件夹中需要包括 APK和manifest两个文件
	 * @return 打包好的插件文件的绝对路径
	 * @throws IOException
	 * @throws JSONException 
	 */
	public static String buildOnePlugin(String onePluginPath)
			throws IOException, JSONException {
		File pluginApk = new File(onePluginPath);
		String md5 = MD5.getFileMD5(pluginApk);

		PluginManifest pluginInfo = new PluginManifest(CLASS_NAME, md5, URL,
				VERSION);
		String jsonStr = pluginInfo.toJsonString();

		System.out.println(jsonStr);

		File manifestFile = new File(OUTPUT + MANIFEST_FILE_NAME);
		FileWriter fw = new FileWriter(manifestFile);
		fw.write(jsonStr);
		fw.close();

		// 将manifest 和 APK文件 ZIP到一起

		File zippedFile = new File(OUTPUT + "tmp.zip");
		FileOutputStream fos = new FileOutputStream(zippedFile);
		ZipOutputStream zos = new ZipOutputStream(fos);

		zipOneFile(zos, manifestFile);
		zipOneFile(zos, pluginApk);

		zos.close();

		manifestFile.delete();

		String zipFilePath = buildPluginName(pluginApk);
		
		// 替换suffix
		zipFilePath = zipFilePath.replace(zipFilePath.substring(zipFilePath.lastIndexOf('.')), SUFFIX);
		
		
		zippedFile.renameTo(new File(zipFilePath));
		return zipFilePath;
	}
	
	/**
	 * 构建插件文件名称
	 * 
	 * 由三部分构成 插件名 + 版本 + 后缀
	 * 
	 * @param pluginApk 插件的apk包文件
	 * @return
	 */
	private static String buildPluginName(File pluginApk) {
		String zipFilePath = OUTPUT + pluginApk.getName();
		zipFilePath = zipFilePath.substring(0, zipFilePath.indexOf('.'));
		
		zipFilePath = zipFilePath + "." + VERSION;
		zipFilePath += SUFFIX;
		
		return zipFilePath;
	}
	
	/**
	 * 拆分插件名称和版本号
	 * @param pluginName
	 * @return
	 */
	public static String[] splitPluginNameAndVersion(String pluginName) {
		String[] strArr = new String[2];
		
		int firstDotIndex = pluginName.indexOf('.');
		strArr[0] = pluginName.substring(0, firstDotIndex);
		
		int secondDotIndex = pluginName.indexOf('.', firstDotIndex + 1);
		
		strArr[1] = pluginName.substring(firstDotIndex + 1, secondDotIndex);
		
		return strArr;
	}
	
	/**
	 * 得到插件 能容忍的 最低宿主版本
	 * @param version
	 * @return
	 */
	public static String getMinHostVersion(String version) {
		// 5_0_0_0_1
		int lastIndexOfHostVersion = 0;
		
		for (int index = 0; index < 3; index++) {
			lastIndexOfHostVersion = version.indexOf('_', lastIndexOfHostVersion + 1);
		}
		
		return version.substring(0, lastIndexOfHostVersion);
	}
	
	/**
	 * 得到插件自身版本
	 * @param version
	 * @return
	 */
	public static String getPluginVersion(String version) {
		// 5_0_0_0_1
		int lastIndexOfHostVersion = 0;
		
		for (int index = 0; index < 3; index++) {
			lastIndexOfHostVersion = version.indexOf('_', lastIndexOfHostVersion + 1);
		}
		
		return version.substring(lastIndexOfHostVersion + 1, version.length());
	}

	/**
	 * 把一个file 加入到 ZipOutputStream 中
	 * 
	 * @param zos
	 * @param src
	 * @throws IOException
	 */
	private static void zipOneFile(ZipOutputStream zos, File src)
			throws IOException {
		zos.putNextEntry(new ZipEntry(src.getName()));

		FileInputStream fis = new FileInputStream(src);
		byte[] buffer = new byte[2048];

		int readLen = 0;
		while ((readLen = fis.read(buffer)) > 0) {
			zos.write(buffer, 0, readLen);
		}

		fis.close();

		zos.closeEntry();
	}

	/**
	 * 解压插件 并且读取PluginManifest对象
	 * 
	 * @param zipFilePath
	 *            一个插件的ZIP文件路径
	 * @param unzippedDir
	 *            插件解压目录
	 * @return 是否成功
	 * @throws IOException
	 */
	public static boolean unzipOnePlugin(String zipFilePath,
			String unzippedDir) {
		File pluginFile = new File(zipFilePath);

		String pluginFileName = pluginFile.getName();
		String pluginFileNameWithoutSuffix = pluginFileName.substring(0, pluginFileName.lastIndexOf('.'));
		String onePluginUnzippedDir = unzippedDir + File.separator + pluginFileNameWithoutSuffix;

		FileInputStream fis = null;
		ZipInputStream zis = null;
		try {
			fis = new FileInputStream(pluginFile);
			zis = new ZipInputStream(fis);
			unZipFile(zis, onePluginUnzippedDir);
			unZipFile(zis, onePluginUnzippedDir);

			zis.close();
			fis.close();
			return true;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (zis != null) {
					zis.close();
				}

				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return false;
	}
	
	/**
	 * 从插件文件夹中读取 插件manifest信息
	 * 
	 * @param onePluginDir
	 * @return
	 */
	public static PluginManifest readPluginManifest(File onePluginDir) {
		File manifestFile = null;
		File apkFile = null;
		File[] childs = onePluginDir.listFiles();
		for (File child : childs) {
			String fileName = child.getName();
			if (fileName.equals(MANIFEST_FILE_NAME)) {
				manifestFile = child;
			}
			
			if (fileName.endsWith(".apk")) {
				apkFile = child;
			}
		}

		// 如果文件缺失 直接返回
		if (manifestFile == null || apkFile == null) {
			return null;
		}
		
		FileReader fr = null;
		BufferedReader br = null;

		try {
			fr = new FileReader(manifestFile);
			br = new BufferedReader(fr);
			String jsonString = br.readLine();
			PluginManifest pluginManifest = PluginManifest
					.fromJsonString(jsonString);

			// 设置APK File Path
			pluginManifest.setApkFilePath(apkFile.getAbsolutePath());
			
			return pluginManifest;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}

				if (fr != null) {
					fr.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 解压一个ZipEntry
	 * 
	 * @param zipEntry
	 * @param zis
	 * @param unzippedPath
	 * @return
	 * @throws IOException
	 */
	private static File unZipFile(ZipInputStream zis, String unzippedPath)
			throws IOException {
		ZipEntry zipEntry = zis.getNextEntry();
		String fileName = zipEntry.getName();

		File unzippedDir = new File(unzippedPath);
		if (!unzippedDir.exists()) {
			unzippedDir.mkdirs();
		}
		
		File file = new File(unzippedDir, fileName);

		try {
			FileOutputStream fos = new FileOutputStream(file);

			byte[] buffer = new byte[20480];
			int readLen = 0;
			while ((readLen = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, readLen);
			}

			fos.close();

			zis.closeEntry();

			return file;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}
}