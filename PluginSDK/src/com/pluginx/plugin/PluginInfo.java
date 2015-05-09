package com.pluginx.plugin;

import com.pluginx.plugin.tools.PluginManifest;

/**
 * 描述一个插件的信息
 * @author michaelzuo
 *
 */
public class PluginInfo implements IPluginAdapter {
	
	/**
	 * 插件包的描述清单
	 */
	private PluginManifest mPluginManifest;
	
	/**
	 * 插件apk解压后的存放路径
	 */
	private String mOptimizedDirectory;
	
	/**
	 * 插件动态库的路径 可以为null
	 */
	private String mLibraryPath;
	
	public PluginInfo(PluginManifest manifest) {
		mPluginManifest = manifest;
	}

	/**
	 * 得到插件MD5值
	 * @return
	 */
	public String getMD5() {
		return mPluginManifest.getMD5();
	}

	/**
	 * 得到插件下载和更新地址
	 * @return
	 */
	public String getUrl() {
		return mPluginManifest.getUrl();
	}

	/**
	 * 得到插件版本
	 * @return
	 */
	public String getVersion() {
		return mPluginManifest.getVersion();
	}

	@Override
	public String getClassName() {
		return mPluginManifest.getClassName();
	}

	@Override
	public String getDexPath() {
		return mPluginManifest.getApkFilePath();
	}

	@Override
	public String getOptimizedDirectory() {
		return mOptimizedDirectory;
	}

	@Override
	public String getLibraryPath() {
		return mLibraryPath;
	}
	
	/**
	 * 设置apk解压后的存放目录
	 * @param optimizedDirectory
	 */
	public void setOptimizedDirectory(String optimizedDirectory) {
		mOptimizedDirectory = optimizedDirectory;
	}
	
	/**
	 * 设置动态库路径
	 * @param libraryPath
	 */
	public void setLibraryPath(String libraryPath) {
		mLibraryPath = libraryPath;
	}
}
