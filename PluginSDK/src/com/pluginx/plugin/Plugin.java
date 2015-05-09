package com.pluginx.plugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;

/**
 * 插件
 * @author michaelzuo
 *
 */
public abstract class Plugin implements IPluginContextDelegate, IMessageHandler {
	
	/**
	 * Provides access to an application's raw asset files
	 */
	private AssetManager mAssetManager;
	
	/**
	 * The Android resource system keeps track of all non-code assets associated with an application
	 */
	private Resources mResources;
	
	/**
	 * The Android Theme
	 */
	private Theme mTheme;
	
	/**
	 * 插件的类加载器
	 */
	private ClassLoader mClassLoader;
	
	/**
	 * 插件描述
	 */
	private PluginInfo mPluginInfo;

	/**
	 * 插件状态
	 * 
	 * @see State
	 */
	private State mState;
	
	/**
	 * 插件上下文对象
	 */
	private PluginContext mContext;
	
	/**
	 * 使用插件上下文对象创建插件
	 * @param context
	 */
	public Plugin() {
		mState = State.UNLOAD;
	}
	
	/**
	 * 得到插件上下文对象
	 * @return
	 */
	public PluginContext getContext() {
		return mContext;
	}
	
	/**
	 * 设置{@link PluginContext}
	 * @param baseContext
	 */
	public void setContext(PluginContext pluginContext) {
	    mContext = pluginContext;
	}
	
	/**
	 * 为context装配delegate为自身
	 */
	public void assembleDelegate() {
	    if (mContext != null) {
	        mContext.setDelegate(this);
	    }
	}
	
	/**
	 * 加载一个插件
	 * @param pluginInfo 插件信息
	 * @param classLoader 插件自身的类加载器
	 * @param hostResources 宿主的Resources对象
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws NoSuchMethodException 
	 */
	public void onLoad(PluginInfo pluginInfo, ClassLoader classLoader, Resources hostResources) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		mPluginInfo = pluginInfo;
		mClassLoader = classLoader;
		mAssetManager = createAssetManager(mPluginInfo.getDexPath());
		mResources = createResources(mAssetManager, hostResources);
		mTheme = createTheme(mResources);
	}
	
	/**
	 * 得到插件显示的标题
	 * @return
	 */
	public String getTitle() {
		if (mResources == null) {
			return null;
		}
		
		return getTitle(mResources);
	}
	
	/**
	 * 得到插件显示的icon 耗时较长 要在异步线程中加载
	 * @return
	 */
	public Bitmap loadIcon() {
		if (mResources == null) {
			return null;
		}
		
		return getIcon(mResources);
	}
	
	/**
	 * 创建AssetManager
	 * @param dexPath
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 */
	private AssetManager createAssetManager(String dexPath) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		AssetManager am = AssetManager.class.newInstance();
		Method m = AssetManager.class.getDeclaredMethod("addAssetPath",
				String.class);
		m.setAccessible(true);
		m.invoke(am, dexPath);
		return am;
	}
	
	/**
	 * 创建Resources
	 * @param am
	 * @param hostResources 宿主的Resources
	 * @return
	 */
	private Resources createResources(AssetManager am, Resources hostResources) {
		DisplayMetrics dm = hostResources.getDisplayMetrics();
		Configuration configuration = hostResources.getConfiguration();
		return new Resources(am, dm, configuration);
	}
	
	/**
	 * 创建Theme
	 * @param res
	 * @return
	 */
	private Theme createTheme(Resources res) {
		Theme theme = res.newTheme();
		int resId = 0;
		try {
			Class<?> claxx = Class.forName("com.android.internal.R$style");
			resId = claxx.getDeclaredField("Theme").getInt(null);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 设置默认theme
		theme.applyStyle(resId, true);
		
		return theme;
	}
	
	/**
	 * unLoad插件
	 */
	public void unLoad() {
		mAssetManager = null;
		mResources = null;
		mTheme = null;
		mPluginInfo = null;
	}
	
	/**
	 * 得到PluginInfo
	 * @return
	 */
	public PluginInfo getPluginInfo() {
		return mPluginInfo;
	}
	
	/**
	 * 得到AssetManager
	 * @return
	 */
	@Override
	public AssetManager getAssetManager() {
		return mAssetManager;
	}
	
	/**
	 * 得到Resources
	 * @return
	 */
	@Override
	public Resources getResources() {
		return mResources;
	}
	
	/**
	 * 得到Theme
	 * @return
	 */
	@Override
	public Theme getTheme() {
		return mTheme;
	}
	
	@Override
	public String getPackageName() {
	    if (mPluginInfo == null) {
	        return null;
	    }
	    
	    String className = mPluginInfo.getClassName();
	    String packageName = className.substring(0, className.lastIndexOf("."));
	    return packageName;
	}
	
	/**
	 * 得到资源路径
	 * @return
	 */
	@Override
	public String getPackageResourcePath() {
		return mPluginInfo == null ? null : mPluginInfo.getDexPath();
	}
	
	/**
	 * 得到类加载器
	 * @return
	 */
	@Override
	public ClassLoader getClassLoader() {
		return mClassLoader;
	}
	
    /**
     * 得到数据库文件路径
     */
    @Override
    public File getDatabasePath(String name) {
        String pluginDir = mContext.getDir(mPluginInfo.getClassName(), Context.MODE_PRIVATE).getAbsolutePath();
        File file = new File(pluginDir, name);
        return file;
    }
    
    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        String className = mPluginInfo == null ? getClass().getName() : mPluginInfo.getClassName();
        String fixName = className + "_" + name;
        IPluginContextDelegate deleate = mContext.getDelegate();
        mContext.setDelegate(null);
        SharedPreferences prefs = mContext.getSharedPreferences(fixName, mode);
        mContext.setDelegate(deleate);
        return prefs;
    }
	
	/**
	 * 得到插件状态
	 * @return
	 */
	public State getState() {
		return mState;
	}

    /**
	 * 得到插件显示名称
	 * @param resources
	 * @return
	 */
	protected abstract String getTitle(Resources resources);
	
	/**
	 * 得到插件的Icon
	 * @param resources
	 * @return
	 */
	protected abstract Bitmap getIcon(Resources resources);
	
	/**
	 * 插件的状态枚举定义
	 * @author michaelzuo
	 *
	 */
	public static enum State {
		
		/**
		 * 禁用状态
		 */
		DISABLE,
		
		/**
		 * 未加载状态
		 */
		UNLOAD,
		
		/**
		 * 加载中
		 */
		LOADING,
		
		/**
		 * 使用中
		 */
		IN_USE,
		
		/**
		 * 更新中
		 */
		UPDATING
	}
	
	/**
	 * 插件类型
	 * @author michaelzuo
	 *
	 */
	public static enum Type {
		
		/**
		 * 未初始化
		 */
		UNINITIALIZED,
		
		/**
		 * 有界面交互的插件
		 */
		ACTIVITY,
		
		/**
		 * 没有界面交互,只有后台服务能力的插件
		 */
		SERVICE
	}
}
