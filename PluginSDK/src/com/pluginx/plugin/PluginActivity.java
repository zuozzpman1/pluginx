
package com.pluginx.plugin;

import com.pluginx.plugin.host.HostActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

/**
 * 有着界面逻辑的插件 在Android上以Activity的形态提供
 * 
 * @author michaelzuo
 */
public abstract class PluginActivity extends Plugin {
    
    private Intent intent;
    
    /**
     * 宿主Activity
     */
    private FragmentActivity mHost;

    /**
     * called to do initial creation of the PluginActivity.
     * @param bundle PluginActivity重建时, 可以依赖bundle中的内容进行恢复
     */
    public void onCreate(Bundle bundle) {
    }
    
    /**
     * 设置宿主Activity
     * @param host
     */
    public void setHost(FragmentActivity host) {
        mHost = host;
    }
    
    /**
     * 获得宿主
     * @return
     */
    public Context getHost() {
        return mHost;
    }
    
    /**
     * 得到启动PluginActivity的intent
     * @param baseIntent 传递信息的基础intent, 会被包裹在LaunchIntent中
     * @return 启动Intent
     */
    public Intent getLaunchIntent(Intent baseIntent) {
        String classNameOfPlugin = getPluginInfo() == null ? getClass().getName() : getPluginInfo().getClassName();
        return HostActivity.getLaunchIntent(getContext(), classNameOfPlugin, baseIntent);
    }
    
    /**
     * 得到{@link Window}
     * @return {@link Window}
     */
    public Window getWindow() {
        return mHost.getWindow();
    }
    
    /**
     * 得到{@link LoaderManager}
     * @return {@link LoaderManager}
     */
    public LoaderManager getSupportLoaderManager() {
        return mHost.getSupportLoaderManager();
    }
    
    /**
     * 得到{@link FragmentManager}
     * @return {@link FragmentManager}
     */
    public FragmentManager getSupportFragmentManager() {
        return mHost.getSupportFragmentManager();
    }
    
    public Intent getIntent() {
        return intent;
    }
    
    public void setIntent(Intent intent) {
        this.intent = intent;
    }
    
    /**
     * 设置根视图 需要在{@link #onCreate(Bundle)}中调用
     * @param view 根视图
     */
    public void setContentView(View view) {
        mHost.setContentView(view);
    }
    
    /**
     * 设置根视图 需要在{@link #onCreate(Bundle)}中调用
     * @param layoutResID
     */
    public void setContentView(int layoutResID) {
        mHost.setContentView(layoutResID);
    }
    
    /**
     * 得到根视图
     * @return 根视图
     */
    public View getContentView() {
        return mHost.findViewById(android.R.id.content);
    }
    
    /**
     * 得到LayoutInflater
     * @return {@link LayoutInflater}
     */
    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(getContext());
    }
    
    /**
     * 
     * @param id
     * @return {@link View}
     */
    public View findViewById(int id) {
        return mHost.findViewById(id);
    }

    /**
     * makes the PluginActivity interacting with the user (based on its
     * containing activity being resumed).
     */
    public void onResume() {
    }

    /**
     * PluginActivity is no longer interacting with the user either because its
     * activity is being paused.
     */
    public void onPause() {
    }

    /**
     * called to do final cleanup of the PluginActivity's fragments.
     */
    public void onDestroy() {
    }

    /**
     * Called by the system when the device configuration changes while your
     * component is running based on Activity onConfigurationChanged
     * 
     * @param newConfig {@link Configuration}
     */
    public void onConfigurationChanged(Configuration newConfig) {
    }

    /**
     * Change the desired orientation of this activity.  If the activity
     * is currently in the foreground or otherwise impacting the screen
     * orientation, the screen will immediately be changed (possibly causing
     * the activity to be restarted). Otherwise, this will be used the next
     * time the activity is visible.
     * 
     * @param requestedOrientation An orientation constant as used in
     * {@link ActivityInfo#screenOrientation ActivityInfo.screenOrientation}.
     */
    public void setRequestedOrientation(int requestedOrientation) {
        mHost.setRequestedOrientation(requestedOrientation);
    }
    
    /**
     * Android的Back键被点击
     */
    public void onBackPressed() {
        mHost.finish();
    }
    
    /**
     * Activity的{@link MotionEvent}分发 {@link Activity#dispatchKeyEvent(android.view.KeyEvent)}
     * @param ev {@link MotionEvent}
     * @return  {@link Activity#dispatchKeyEvent(android.view.KeyEvent)}
     */
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }
}
