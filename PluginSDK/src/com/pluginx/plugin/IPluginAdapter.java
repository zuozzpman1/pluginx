package com.pluginx.plugin;

/**
 * 插件适配器
 * @author michaelzuo
 *
 */
public interface IPluginAdapter {

	/**
	 * 得到插件主体的类名
	 * @return
	 */
	public String getClassName();
	
	/**
	 * 得到Dex 文件路径
	 * @return
	 */
	public String getDexPath();
	
	/**
	 * 解压Dex文件的 可写入的目录
	 * @return
	 * 
	 */
	public String getOptimizedDirectory();
	
	/**
	 * 得到动态库存放路径
	 * @return
	 */
	public String getLibraryPath();
}
