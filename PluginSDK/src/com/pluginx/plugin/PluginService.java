package com.pluginx.plugin;

import com.pluginx.plugin.util.LogUtil;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;

/**
 * 后台运行的插件 不具有可交互性
 * @author michaelzuo
 *
 */
public abstract class PluginService {
    
    private PluginContext context;
    
    /**
     * 执行PluginService行为能力的代理
     */
    private IPluginServiceDelegate mPluginServiceStopDelegate;
    
    /**
     * PluginService 整数唯一标识
     */
    private int mStartId;
    
	public PluginService() {
    }

	/**
	 * 设置代理接口
	 * @param delegate
	 */
	public void setDelegate(IPluginServiceDelegate delegate) {
	    mPluginServiceStopDelegate = delegate;
	}
	
	public void setContext(PluginContext context) {
        this.context = context;
    }
	
	public PluginContext getContext() {
        return context;
    }
	
	/**
	 * 设置startId
	 * @param startId
	 */
	public void setStartId(int startId) {
	    mStartId = startId;
	}

	/**
	 * like {@link Service#onCreate()}
	 */
	public void onCreate() {
	    LogUtil.i("PluginService onCreate():" + toString());
	}

    /**
     * like {@link Service#onStartCommand(Intent, int, int)}
     */
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i("PluginService onStartCommand():" + toString());
        return Service.START_STICKY;
    }

    /**
     * like {@link Service#onDestroy()}
     */
    public void onDestroy() {
        LogUtil.i("PluginService onDestroy():" + toString());
    }
    
    /**
     * like {@link Service#stopSelf()}
     */
    public final void stopSelf() {
        if (mPluginServiceStopDelegate != null) {
            mPluginServiceStopDelegate.stopSelf(this);
        }
    }
    
    /**
     * like {@link Service#stopSelfResult(int)}
     * @param startId
     * @return Returns true if the startId matches the last start request
     * and the service will be stopped, else false.
     */
    public final boolean stopSelfResult (int startId) {
        // if startId is not matched just return false
        if (startId != mStartId) {
            return false;
        }
        
        stopSelf();
        return true;
    }
    
    /**
     * like {@link Service#startForeground(int id, Notification notification)}
     * @param id The identifier for this notification as per
     * @param notification The Notification to be displayed.
     */
    public final void startForeground(int id, Notification notification) {
        if (mPluginServiceStopDelegate != null) {
            mPluginServiceStopDelegate.startForeground(id, notification);
        }
    }
    
    /**
     * like {@link Service#startForeground(boolean removeNotification)}
     * @param removeNotification If true, the notification previously provided
     */
    public final void stopForeground(boolean removeNotification) {
        if (mPluginServiceStopDelegate != null) {
            mPluginServiceStopDelegate.stopForeground(removeNotification);
        }
    }
    
    
    /**
     * PluginService 不具备的能力
     * 交由{@link IPluginServiceDelegate} 来执行
     * @author michaelzuo
     *
     */
    public static interface IPluginServiceDelegate {
        
        /**
         * PluginService结束自己
         */
        public void stopSelf(PluginService pluginService);
        
        public void startForeground(int id, Notification notification);
        
        public void stopForeground(boolean removeNotification);
    }
}