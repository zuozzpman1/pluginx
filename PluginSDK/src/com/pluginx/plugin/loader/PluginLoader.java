package com.pluginx.plugin.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import org.json.JSONException;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.pluginx.plugin.Plugin;
import com.pluginx.plugin.PluginFactory;
import com.pluginx.plugin.PluginInfo;
import com.pluginx.plugin.tools.PluginBuilder;
import com.pluginx.plugin.tools.PluginManifest;
import com.pluginx.plugin.util.MD5;

/**
 * 插件加载器
 * 
 * 通过插件描述文件
 * @author michaelzuo
 *
 */
public class PluginLoader {
	
	/**
	 * 插件解压的目录
	 */
	private String mOptimizedDirectory;
	
	/**
	 * 插件加载动态库的目录
	 */
	private String mLibraryPath;
	
	/**
	 * 向主线程同步消息的Handler
	 */
	private UIHandler mHIHandler;
	
	/**
	 * 创建插件加载器
	 * @param optimizedDirectory 插件解压存放目录 不能为空 需要注意目录权限,最好放到稳定的只有应用具有读写权限的目录
	 * @param libraryPath 可以为空
	 */
	public PluginLoader(String optimizedDirectory, String libraryPath) {
		mOptimizedDirectory = optimizedDirectory;
		mLibraryPath = libraryPath;
		mHIHandler = new UIHandler(Looper.getMainLooper());
	}
	
	/**
	 * 加载插件列表 耗时较长 要在后台线程运行
	 * @param pluginsPath 插件存放目录
	 * @param parentClassLoader 父ClassLoader
	 * @param hostResources 宿主Resources
	 * @return 是否找到了需要加载的插件
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public boolean load(String pluginsPath, ClassLoader parentClassLoader, Resources hostResources, IPluginLoadCallback callback) throws JSONException, IOException {
		// 解析插件信息列表
		File pluginsDir = new File(pluginsPath);
		
		File[] childs = pluginsDir.listFiles();
		if (childs == null) {
			return false;
		}
		
		// 从插件目录下 加载所有插件
		ArrayList<PluginCreateMessage> pluginCreateMsgs = new ArrayList<PluginLoader.PluginCreateMessage>();
		
		for (File child : childs) {
		    PluginCreateMessage pluginCreateMsg = buildPluginCreateMsg(child, parentClassLoader, hostResources);
		    
			if (pluginCreateMsg == null) {
				continue;
			}
			
			pluginCreateMsgs.add(pluginCreateMsg);
		}
		
		// 同步到UIThread加载
		PluginLoadMessage pluginLoadMsg =new PluginLoadMessage();
		pluginLoadMsg.pluginCreateMsgs = pluginCreateMsgs;
		pluginLoadMsg.callback = callback;
		mHIHandler.sendMessage(Message.obtain(mHIHandler, 0, pluginLoadMsg));
		
		return !pluginCreateMsgs.isEmpty();
	}
	
	/**
	 * 创建一个插件创建消息
	 * @param onePluginDir
	 * @param parentClassLoader
	 * @param hostResources
	 * @return 如果插件没有校验通过 则返回null
	 * @throws JSONException
	 * @throws IOException
	 */
	private PluginCreateMessage buildPluginCreateMsg(File onePluginDir, ClassLoader parentClassLoader, Resources hostResources) throws JSONException, IOException {
	    // 从manifest文件中读取插件描述信息
        PluginInfo pluginInfo = readPluginInfo(onePluginDir);
        
        // verify
        boolean isVerified = verify(pluginInfo);
        
        // 只加载校验通过的插件
        if (!isVerified) {
            return null;
        }
        
        PluginCreateMessage pluginCreateMsg = new PluginCreateMessage();
        pluginCreateMsg.pluginInfo = pluginInfo;
        pluginCreateMsg.parentClassLoader = parentClassLoader;
        pluginCreateMsg.hostResources = hostResources;
        
        return pluginCreateMsg;
	}
	
	/**
	 * 从插件路径的manifest文件中读取插件信息
	 * @param onePluginDir 解压后的一个插件的文件夹
	 * @return
	 * @throws JSONException
	 * @throws IOException
	 */
	private PluginInfo readPluginInfo(File onePluginDir) throws JSONException, IOException {
		
		PluginManifest pluginManifest = PluginBuilder.readPluginManifest(onePluginDir);
		
		// 如果失败了直接返回null
		if (pluginManifest == null) {
			return null;
		}
		
		PluginInfo pluginInfo = new PluginInfo(pluginManifest);
		
		pluginInfo.setOptimizedDirectory(mOptimizedDirectory);
		pluginInfo.setLibraryPath(mLibraryPath);
		
		return pluginInfo;
	}
	
	/**
	 * 插件安全性与完整性校验
	 * @param pluginInfo
	 * @return
	 * @throws FileNotFoundException 
	 */
	private boolean verify(PluginInfo pluginInfo) throws FileNotFoundException {
		// md5 verify
		String dexPath = pluginInfo.getDexPath();
		File dexFile = new File(dexPath);
		String md5 = MD5.getFileMD5(dexFile);
		return pluginInfo.getMD5().equals(md5);
	}
	
	 /**
     * 向UI线程中同步消息的Handler
     * @author michaelzuo
     *
     */
    private static class UIHandler extends Handler {

        public UIHandler(Looper looper) {
            super(looper);
        }
        
        @Override
        public void handleMessage(Message msg) {
            PluginLoadMessage pluginLoadMsg = (PluginLoadMessage)msg.obj;
            
            ArrayList<PluginCreateMessage> pluginCreateMsgs = pluginLoadMsg.pluginCreateMsgs;
            ArrayList<Plugin> plugins = new ArrayList<Plugin>();
            
            for (PluginCreateMessage pluginCreateMsg : pluginCreateMsgs) {
                PluginInfo pluginInfo = pluginCreateMsg.pluginInfo;
                ClassLoader parentClassLoader = pluginCreateMsg.parentClassLoader;
                Resources hostResources = pluginCreateMsg.hostResources;
                
                Plugin plugin = null;
                try {
                    plugin = PluginFactory.createPlugin(pluginInfo, parentClassLoader, hostResources);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                
                if (plugin == null) {
                    continue;
                }
                
                plugins.add(plugin);
            }
            
            IPluginLoadCallback callback = pluginLoadMsg.callback;
            callback.onLoad(plugins);
            
            
        }
        
    }
    
    /**
     * 创建Plugin的Message
     * Plugin的类加载和反射实例化 需要在UI线程中进行
     * @author michaelzuo
     *
     */
    private static class PluginCreateMessage {
        PluginInfo pluginInfo;
        ClassLoader parentClassLoader;
        Resources hostResources;
    }
    
    /**
     * 插件加载信息
     * @author michaelzuo
     *
     */
    private static class PluginLoadMessage {
        ArrayList<PluginCreateMessage> pluginCreateMsgs;
        IPluginLoadCallback callback;
    }
    
	
	/**
	 * 插件加载回调
	 * @author michaelzuo
	 *
	 */
	public static interface IPluginLoadCallback {
	    
	    /**
	     * 加载插件完成 保证在UI线程中回调
	     * @param plugins 加载完的插件列表
	     */
	    void onLoad(ArrayList<Plugin> plugins);
	}
}
