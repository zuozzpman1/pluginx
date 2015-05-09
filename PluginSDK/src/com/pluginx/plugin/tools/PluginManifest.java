package com.pluginx.plugin.tools;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 插件包的描述清单
 * @author michaelzuo
 *
 */
public class PluginManifest {
	
	/**
	 * 类名 key
	 */
	private static final String KEY_CLASS_NAME = "name";
	
	/**
	 * MD5 key
	 */
	private static final String KEY_MD5 = "md5";
	
	/**
	 * URL key
	 */
	private static final String KEY_URL = "url";
	
	/**
	 * Version key
	 */
	private static final String KEY_VERSION = "version";
	
	/**
	 * 插件的className
	 */
	private String mClassName;
	
	/**
	 * 插件md5值 用于校验
	 */
	private String mMD5;

	/**
	 * 下载插件的地址
	 */
	private String mUrl;

	/**
	 * 插件版本
	 */
	private String mVersion;
	
	/**
	 * APK解压后存放的路径
	 */
	private String mApkFilePath;
	
	/**
	 * 创建插件信息
	 * @param className 类名必须唯一
	 * @param md5 插件APK的md5值
	 * @param url 插件更新地址
	 * @param version 版本信息
	 */
	public PluginManifest(String className, String md5, String url, String version) {
		mClassName = className;
		mMD5 = md5;
		mUrl = url;
		mVersion = version;
	}
	
	private PluginManifest() {
	}
	
	/**
	 * 生成JSON String
	 * @return
	 * @throws JSONException 
	 */
	public String toJsonString() throws JSONException {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put(KEY_CLASS_NAME, mClassName);
		jsonObj.put(KEY_MD5, mMD5);
		jsonObj.put(KEY_URL, mUrl);
		jsonObj.put(KEY_VERSION, mVersion);
		return jsonObj.toString();
	}
	
	/**
	 * 从Json String中读取
	 * @param jsonString
	 * @return
	 * @throws JSONException 
	 */
	public static PluginManifest fromJsonString(String jsonString) throws JSONException {
		PluginManifest pluginManifest = new PluginManifest();
		JSONObject json = new JSONObject(jsonString);
		pluginManifest.mClassName = json.getString(KEY_CLASS_NAME);
		pluginManifest.mMD5 = json.getString(KEY_MD5);
		pluginManifest.mUrl = json.getString(KEY_URL);
		pluginManifest.mVersion = json.getString(KEY_VERSION);
		
		return pluginManifest;
	}
	
	/**
	 * 设置APK存放的路径
	 * @param apkFilePath
	 */
	void setApkFilePath(String apkFilePath) {
		mApkFilePath = apkFilePath;
	}
	
	/**
	 * 得到APK解压后存放的路径
	 * @return
	 */
	public String getApkFilePath() {
		return mApkFilePath;
	}
	
	/**
	 * 得到插件类名
	 * @return
	 */
	public String getClassName() {
		return mClassName;
	}
	
	/**
	 * 得到MD5值
	 * @return
	 */
	public String getMD5() {
		return mMD5;
	}
	
	/**
	 * 得到插件下载URL
	 * @return
	 */
	public String getUrl() {
		return mUrl;
	}
	
	/**
	 * 得到插件版本
	 * @return
	 */
	public String getVersion() {
		return mVersion;
	}
}