
package com.pluginx.plugin;

import com.pluginx.plugin.host.HostService;
import com.pluginx.plugin.util.LogUtil;

import java.io.File;
import java.util.HashMap;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.net.Uri;
import android.view.LayoutInflater;

/**
 * 插件公共的上下文代理类 借用Android的机制 来构建一个 context对象 将AssetManager Resources Theme
 * ClassLoader等替换为插件的上下文 这样的context会指向插件的资源, 使用 {@link PluginContext} 作为
 * {@link LayoutInflater#from(Context)}的参数 可以指定从插件中inflate view
 * 
 * @author michaelzuo
 */
public class PluginContext extends ContextWrapper {

    /**
     * 持有一个LayoutInflater 从插件自身的Resources中加载资源
     */
    private LayoutInflater mLayoutInflater;
    
    /**
     * 插件上下文代理
     */
    private IPluginContextDelegate mDelegate;

    /**
     * ContentProviders autohority->PluginContentProvider
     */
    private HashMap<String, PluginContentProvider> mPluginProviders = new HashMap<String, PluginContentProvider>();
    
    public PluginContext(Context base) {
        super(base);
    }

    /**
     * 设置当前使用的插件上下文代理
     * 
     * @param pluginContextDelegate 如果delegate为空 则会使用 ApplicationContext的行为
     */
    public void setDelegate(IPluginContextDelegate pluginContextDelegate) {
        mDelegate = pluginContextDelegate;
    }
    
    /**
     * 清除代理
     * @return
     */
    public IPluginContextDelegate clearDelegate() {
        IPluginContextDelegate d = mDelegate;
        mDelegate = null;
        return d;
    }

    /**
     * 获取当前使用的插件上下文代理
     * 
     * @return
     */
    public IPluginContextDelegate getDelegate() {
        return mDelegate;
    }
    
    @Override
    public AssetManager getAssets() {
        if (mDelegate != null) {
            return mDelegate.getAssetManager();
        }
        return super.getAssets();
    }

    @Override
    public String getPackageResourcePath() {
        if (mDelegate != null) {
            return mDelegate.getPackageResourcePath();
        }
        return super.getPackageResourcePath();
    }

    @Override
    public Resources getResources() {
        LogUtil.i("PluginContext getResources");
        if (mDelegate != null) {
            LogUtil.i("PluginContext getResources by delegate");
            return mDelegate.getResources();
        }
        return super.getResources();
    }

    @Override
    public Theme getTheme() {
        if (mDelegate != null) {
            return mDelegate.getTheme();
        }
        return super.getTheme();
    }

    @Override
    public ClassLoader getClassLoader() {
        if (mDelegate != null) {
            return mDelegate.getClassLoader();
        }
        return super.getClassLoader();
    }
    
    @Override
    public Object getSystemService(String name) {
        // LayoutInflater.from这种方式 默认是从宿主的Resources中加载资源的
        // 这里需要进行一次重定向, 通过cloneInContext把context设置为自身
        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (mLayoutInflater == null) {
                mLayoutInflater = LayoutInflater.from(getBaseContext()).cloneInContext(this);
            }
            return mLayoutInflater;
        }
        
        return super.getSystemService(name);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory) {
        if (mDelegate != null) {
            return openOrCreateDatabase(name, mode, factory, null);
        }
        return super.openOrCreateDatabase(name, mode, factory);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory,
            DatabaseErrorHandler errorHandler) {
        if (mDelegate != null) {
            return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name).getAbsolutePath(),
                    factory, errorHandler);
        }
        return super.openOrCreateDatabase(name, mode, factory, errorHandler);
    };

    @Override
    public File getDatabasePath(String name) {
        if (mDelegate != null) {
            return mDelegate.getDatabasePath(name);
        }
        return super.getDatabasePath(name);
    }
    
    @Override
    public String getPackageName() {
        if (mDelegate != null) {
            return mDelegate.getPackageName();
        } 
        return super.getPackageName();
    }
    
    /**
     * 获取宿主package name
     * @return
     */
    public String getHostPackageName() {
        return super.getPackageName();
    }
    
    @Override
    public android.content.SharedPreferences getSharedPreferences(String name, int mode) {
        if (mDelegate != null) {
            return mDelegate.getSharedPreferences(name, mode);
        }
        return super.getSharedPreferences(name, mode);
    };

    public PluginContentResolver getPluginContentResolver() {
        return new PluginContentResolver(this);
    }

    /**
     * 注册Provider
     * 
     * @param autohority
     * @param provider
     */
    public void registerProvider(String autohority, PluginContentProvider provider) {
        if (!mPluginProviders.containsKey(autohority)) {
            provider.onCreate();
            mPluginProviders.put(autohority, provider);
        }
    }

    /**
     * 根据uri查找provider
     * 
     * @param uri
     * @return
     */
    public PluginContentProvider findProvider(Uri uri) {
        String authority = uri.getAuthority();
        return mPluginProviders.get(authority);
    }
    
    /**
     * 根据authority查找provider
     * 
     * @param uri
     * @return
     */
    public PluginContentProvider findProvider(String authority) {
        return mPluginProviders.get(authority);
    }

    /**
     * 启动指定的{@link PluginService}
     * @param classNameOfPluginService 插件service的名称
     * @param intent 传递消息的intent
     */
    public void startPluginService(String classNameOfPluginService, Intent intent) {
        HostService.startService(getBaseContext(), this, classNameOfPluginService, intent);
    }
    
    /**
     * 停止指定的{@link PluginService}
     * @param classNameOfPluginService
     * @return 是否找到了对应的PluginService并stop
     */
    public boolean stopPluginService(String classNameOfPluginService) {
        boolean isMatched = HostService.stopService(classNameOfPluginService);
        
        // 如果所有PluginService都被停止掉了
        // 停止掉HostService
        if (!HostService.hasPluginService()) {
            Intent intent = new Intent();
            intent.setClass(this, HostService.class);
            stopService(intent);
        }
        
        return isMatched;
    }

    /**
     * 插件上下文监听
     * 
     * @author michaelzuo
     */
    public static interface IPluginContextListener {

        /**
         * 插件上下文创建
         * 
         * @param context
         */
        public void onCreate(PluginContext context);
    }
}
