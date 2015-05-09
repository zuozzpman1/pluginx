
package com.pluginx.plugin;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import android.content.Context;
import android.content.res.Resources;

import com.pluginx.plugin.loader.PluginLoader;
import com.pluginx.plugin.loader.PluginLoader.IPluginLoadCallback;

/**
 * 插件管理器 维护插件列表 负责插件的加载和卸载 在应用启动时必须先进行初始化 {@link #init(PluginContext)}
 * 
 * @author michaelzuo
 */
public class PluginManager {

    /**
     * 插件APK包解压后的目录
     */
    private static final String OPTIMIZED_DIRECTORY = "plugins_optimized";

    /**
     * 插件动态库存放目录
     */
    private static final String LIBRARY_DIRECTORY = "libs";

    /**
     * 插件管理器唯一实例
     */
    private static PluginManager sInstance;

    /**
     * 插件列表
     */
    private ArrayList<Plugin> mPlugins;

    private PluginManager() {
        mPlugins = new ArrayList<Plugin>();
    }

    /**
     * 得到唯一实例
     * 
     * @return
     */
    public static PluginManager getInstance() {
        if (sInstance == null) {
            sInstance = new PluginManager();
        }

        return sInstance;
    }

    /**
     * 销毁
     */
    public void destroy() {
        sInstance = null;
    }

    /**
     * 从输入流中加载所有插件
     * 
     * @param hostContext
     * @param pluginsPath
     * @param callback 保证在主线程中回调
     * @throws IOException
     * @throws JSONException
     */
    public boolean load(final Context hostContext, String pluginsPath, final IPluginLoadCallback callback)
            throws JSONException, IOException {
        ClassLoader parentClassLoader = hostContext.getClassLoader();
        Resources hostResources = hostContext.getResources();

        String optimizedDirectory = hostContext.getDir(OPTIMIZED_DIRECTORY, Context.MODE_PRIVATE)
                .getAbsolutePath();
        String libraryPath = hostContext.getDir(LIBRARY_DIRECTORY, Context.MODE_PRIVATE)
                .getAbsolutePath();

        PluginLoader loader = new PluginLoader(optimizedDirectory, libraryPath);
        boolean hasPlugins = loader.load(pluginsPath, parentClassLoader, hostResources,
                new IPluginLoadCallback() {

                    @Override
                    public void onLoad(ArrayList<Plugin> plugins) {
                        
                        for (Plugin plugin : plugins) {
                            plugin.setContext(new PluginContext(hostContext));
                        }
                        
                        mPlugins = plugins;
                        
                        callback.onLoad(plugins);
                    }
                });

        return hasPlugins;
    }

    /**
     * 内存中直接指定插件
     * 
     * @param plugin
     */
    public void memoryLoad(Plugin plugin) {
        mPlugins.add(plugin);
    }

    /**
     * 插件个数
     * 
     * @return
     */
    public int size() {
        return mPlugins == null ? 0 : mPlugins.size();
    }

    /**
     * 根据索引获取对应插件
     * 
     * @param index
     * @return
     */
    public Plugin getPlugin(int index) {
        if (mPlugins == null || index < 0 || index >= mPlugins.size()) {
            return null;
        }

        return mPlugins.get(index);
    }

    /**
     * 根据{@link PluginInfo#getClassName()}获取到对应的{@link Plugin}
     * 
     * @param classNameOfPlugin
     * @return
     */
    public Plugin getPlugin(String classNameOfPlugin) {
        if (mPlugins == null) {
            return null;
        }

        for (Plugin plugin : mPlugins) {
            String className = plugin.getClass().getName();
            if (className.equals(classNameOfPlugin)) {
                return plugin;
            }
        }

        return null;
    }

    /**
     * 发送消息给插件
     * 
     * @param msg
     */
    public void sendMessage(PluginMessage msg) {
        if (msg == null)
            return;
        
        String pluginClassName = msg.getPluginClassName();
        if (pluginClassName == null)
            return;
        
        Plugin plugin = getPlugin(pluginClassName);
        
        if (plugin != null) {
            plugin.assembleDelegate();
            plugin.handleMessage(msg);
        }
    }

    // TODO 卸载某个插件

    // TODO 更新某个插件
}
