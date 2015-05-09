package com.pluginx.plugin.loader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.pluginx.plugin.tools.PluginBuilder;

import android.content.Context;

/**
 * 插件安装器
 * 
 * 负责插件的安装
 * 
 * @author michaelzuo
 *
 */
public class PluginInstaller {
	
	/**
	 * 插件在assets中存放的目录名称
	 */
	private static final String PLUGINS_ASSETS_DIRECTORY = "plugins";
	
	/**
	 * 插件解压后的存放目录 包含了
	 */
	private static final String PLUGINS_INSTALLED_DIRECTORY = "plugins_installed";

	/**
	 * 插件安装文件存放目录             
	 */
	private static final String PLUGINS_INSTALLER_DIRECTORY = "plugins_installer";
	
	/**
	 * 清空插件目录
	 * @param hostContext
	 */
	public static void clearPlugins(Context hostContext) {
		deleteFile(getInstallerDirFile(hostContext));
		deleteFile(getInstalledDirFile(hostContext));
	}
	
	/**
	 * 递归删除文件夹下所有文件
	 * @param dir
	 */
	private static void deleteFile(File file) {
		if (file.isDirectory()) {
			File[] childs = file.listFiles();
			for (File child : childs) {
				deleteFile(child);
			}
			
			file.delete();
		} else {
			file.delete();
		}
	}
	
	/**
	 * 安装assets下的所有插件
	 * 
	 * 目前的安装至需要进行一次拷贝就可以了
	 * 
	 * @param hostContext
	 * @throws IOException
	 * @return 插件被解压后存放的目录
	 */
	public static File installAllFromAssets(Context hostContext) throws IOException {
		File installerDir = getInstallerDirFile(hostContext);
		String[] pluginsInstalled = installerDir.list();
		
		// 先将assets下的所有插件 拷贝到 PLUGINS_INSTALLER_DIRECTORY目录
		// 拷贝原则 为 本地存在就不拷贝了
		String[] pluginsInAssets = hostContext.getAssets().list(PLUGINS_ASSETS_DIRECTORY);
		
		// 获得需要安装的插件
		ArrayList<String> needInstallPlugins = getNeedInstallPlugins(pluginsInstalled, pluginsInAssets);
		
		// 安装插件
		for (String pluginName : needInstallPlugins) {
			installOnePlugin(hostContext, pluginName, installerDir);
		}
		
		return getInstalledDirFile(hostContext);
	}
	
	/**
	 * 获取Assets中有,但是 没有被安装过的插件列表
	 * @param pluginsInstalled
	 * @param pluginsInAssets
	 * @return
	 */
	private static ArrayList<String> getNeedInstallPlugins(String[] pluginsInstalled, String[] pluginsInAssets) {
		ArrayList<String> needInstallPlugins = new ArrayList<String>();
		
		for (String pluginInAssets : pluginsInAssets) {
			String[] pluginNameAndVersionInAssets = PluginBuilder.splitPluginNameAndVersion(pluginInAssets);
			
			String pluginNameInAssets = pluginNameAndVersionInAssets[0];
			String pluginVersionInAssets = pluginNameAndVersionInAssets[1];
			
			// 从安装列表中查找
			String pluginInstalled = getPluginInInstalled(pluginsInstalled, pluginNameInAssets);

			boolean needInstall = false;
			// 如果没安装过 需要安装
			if (pluginInstalled == null) {
				needInstall = true;
			} else {
				String[] pluginNameAndVersionInstalled = PluginBuilder.splitPluginNameAndVersion(pluginInstalled);
				
				// 比较插件版本 如果assets里边的插件版本高于 已经安装的版本 需要替换
				needInstall = needReplace(pluginNameAndVersionInstalled[1], pluginVersionInAssets);
			}
			
			// 如果不需要安装 直接返回
			if (!needInstall) {
				continue;
			}
			
			needInstallPlugins.add(pluginInAssets);
		}
		
		return needInstallPlugins;
	}
	
	/**
	 * 根据插件名称 查找是否已经被安装了
	 * @param pluginsInstalled
	 * @param pluginName
	 * @return 已经安装的插件完整名称,包括插件名和版本信息, 如果没安装返回null
	 */
	private static String getPluginInInstalled(String[] pluginsInstalled, String pluginName) {
		for (String pluginInstalled : pluginsInstalled) {
			String[] pluginNameAndVersionInstalled = PluginBuilder.splitPluginNameAndVersion(pluginInstalled);
			String pluginNameInstalled = pluginNameAndVersionInstalled[0];
			
			if (pluginNameInstalled.equals(pluginName)) {
				return pluginInstalled;
			}
		}
		
		return null;
	}
	
	/**
	 * 检查插件是否需要覆盖安装
	 * @param version1
	 * @param version2
	 * @return 是否需要覆盖安装
	 */
	private static boolean needReplace(String version1, String version2) {
		// TODO
		return true;
	}
	
	/**
	 * 安装一个插件
	 * @param hostContext 
	 * @param pluginName 插件名称
	 * @param installerDir 安装目标目录
	 */
	private static void installOnePlugin(Context hostContext, String pluginFileName, File installerDir) {
		String[] splitPluginNameAndVersion = PluginBuilder.splitPluginNameAndVersion(pluginFileName);
		String pluginName = splitPluginNameAndVersion[0];
		String version = splitPluginNameAndVersion[1];
		
		// TODO 检查最低的host版本是否满足要求 如果不满足 则不安装
		String minHostVersion = PluginBuilder.getMinHostVersion(version);
		
		// 先拷贝到 PLUGINS_INSTALLER_DIRECTORY 
		if (!installerDir.exists()) {
			installerDir.mkdirs();
		}
		
		File pluginInstallerFile = new File(installerDir, pluginFileName);
		boolean suc = copy2installerDir(hostContext, pluginFileName, pluginInstallerFile);
	
		// 再解压到 PUGLINS_INSTALLED_DIRECTORY
		if (suc) {
			String unzippedDir = getInstalledDirFile(hostContext).getAbsolutePath();
			PluginBuilder.unzipOnePlugin(pluginInstallerFile.getAbsolutePath(), unzippedDir);
		}
	}
	
	/**
	 * 得到插件安装文件路径
	 * @param hostContext
	 * @return
	 */
	private static File getInstallerDirFile(Context hostContext) {
		return hostContext.getDir(PLUGINS_INSTALLER_DIRECTORY, Context.MODE_PRIVATE);
	}
	
	/**
	 * 得到插件解压后存放的文件夹
	 * @param hostContext
	 * @return
	 */
	private static File getInstalledDirFile(Context hostContext) {
		return hostContext.getDir(PLUGINS_INSTALLED_DIRECTORY, Context.MODE_PRIVATE);
	}
	
	/**
	 * 从assets中把插件zip包拷贝到安装器目录
	 * @param hostContext
	 * @param pluginFileName
	 * @param pluginInstallerFile
	 * @return
	 */
	private static boolean copy2installerDir(Context hostContext, String pluginFileName, File pluginInstallerFile) {
		InputStream in = null;
		FileOutputStream fos = null;
		try {
			in = hostContext.getAssets().open(PLUGINS_ASSETS_DIRECTORY + File.separator + pluginFileName);
			
			// 先拷贝到 PLUGINS_INSTALLER_DIRECTORY 
			fos = new FileOutputStream(pluginInstallerFile);
			
			int readLen = 0;
			byte[] buffer = new byte[20480];
			while ((readLen = in.read(buffer)) > 0) {
				fos.write(buffer, 0, readLen);
			}
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
