
package com.pluginx.plugin;

import java.io.File;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;

/**
 * 插件上下文代理接口 负责提供关于插件资源的对象
 * 
 * @author michaelzuo
 */
public interface IPluginContextDelegate {

    /**
     * 得到AssetManager
     * 
     * @return
     */
    public AssetManager getAssetManager();

    /**
     * 得到Resources
     * 
     * @return
     */
    public Resources getResources();

    /**
     * 得到Theme
     * 
     * @return
     */
    public Theme getTheme();

    /**
     * 得到资源路径
     * 
     * @return
     */
    public String getPackageResourcePath();

    /**
     * 得到类加载器
     * 
     * @return
     */
    public ClassLoader getClassLoader();

    /**
     * 得到数据库文件路径
     * 
     * @param name
     * @return
     */
    public File getDatabasePath(String name);

    /**
     * 得到SharedPreferences对象
     * @param name
     * @param mode
     * @return
     */
    public SharedPreferences getSharedPreferences(String name, int mode);

    /**
     * 获取插件的包名
     * @return
     */
    public String getPackageName();
}
