package com.pluginx.plugin.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.pluginx.plugin.PluginActivity;
import com.pluginx.plugin.PluginContext;
import com.pluginx.plugin.util.LogUtil;

/**
 * PluginActivity的状态机定义
 * 
 * 赋予PluginActivity内可以根据状态 管理View组合的能力
 * 
 * @author michaelzuo dazhengyang
 *
 */
public abstract class PluginFragment {
    
    /**
     * TAG for Logger
     */
    private static final String LOG_TAG = "PluginActivityFragment";
    
    /**
     * 描述fragment当前的生命周期阶段
     */
    private LifeCircle mLife;
    
    /**
     * Fragment附着的PluginFragmentActivity
     */
    private PluginFragmentActivity mPluginActivity;
    
    /**
     * 启动当前fragment的意图
     */
    private Intent mIntent;
    
    public static final int DEFAULT_REQUEST_CODE = -100;
    public static final int DEFAULT_RESULT_CODE = -100;
    protected int mRequestCode = DEFAULT_REQUEST_CODE;
    private int mResultCode = DEFAULT_RESULT_CODE;
    private Intent mResultData;
    
    public PluginFragment() {
        mLife = LifeCircle.UN_CREATE;
    }
    
    /**
     * 设置intent
     * @param intent
     */
    public void setIntent(Intent intent) {
        mIntent = intent;
    }
    
    /**
     * 得到intent
     * @return
     */
    public Intent getIntent() {
        return mIntent;
    }
    
    /**
     * 得到启动插件的intent
     * @param baseIntent
     * @return
     */
    public Intent getLaunchIntent(Intent baseIntent) {
        return mPluginActivity.getLaunchIntent(baseIntent);
    }
    
    /**
     * called to do initial creation of the PluginActivityfragment.
     * @param pluginFragmentActivity fragment 附着在哪个 {@link PluginFragmentActivity} 上
     * @param bundle
     */
    public void onCreate(PluginFragmentActivity pluginFragmentActivity) {
        mPluginActivity = pluginFragmentActivity;
        mLife = LifeCircle.CREATED;
        
        log("onCreate");
    }
    
    /**
     * 得到Fragment附着的{@link PluginActivity}
     * @return
     */
    public PluginFragmentActivity getActivity() {
        return mPluginActivity;
    }

    /**
     * creates and returns the view hierarchy associated with the PluginActivity
     * 
     * @param layoutInflater
     * @param pluginContextDelegate
     * @return
     */
    public abstract View onCreateView();
    
    /**
     * inflate view
     * @return
     */
    public View inflate(int resId) {
        LayoutInflater inflater = mPluginActivity.getLayoutInflater();
        return inflater.inflate(resId, null);
    }
    
    /**
     * makes the PluginActivity interacting with the user (based on its containing PluginActivity being resumed).
     */
    public void onResume() {
        mLife = LifeCircle.FRONT;
        log("onResume");
    }

    /**
     * PluginActivity is no longer interacting with the user either because its PluginActivity is being paused.
     */
    public void onPause() {
        mLife = LifeCircle.BACKGROUND;
        log("onPause");
    }
    
    /**
     * called to do final cleanup of the PluginFragment.
     */
    public void onDestroy() {
        mLife = LifeCircle.DESTROYED;
        log("onDestroyed");
    }
    
    /**
     * 横竖屏切换回调
     * @param newConfig
     */
    public void onConfigurationChanged (Configuration newConfig) {
        log("onConfigurationChanged");
    }
    
    public void onResult(int requestCode, int resultCode, Intent data) {
        log("onResult");
    }
    
    /**
     * Android的Back键被点击
     * 默认为 finish掉自己
     */
    public void onBackPressed() {
        finish();
    }
    
    /**
     * Activity的{@link MotionEvent}分发 {@link Activity#dispatchKeyEvent(android.view.KeyEvent)}
     * @param ev
     * @return
     */
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }
    
    /**
     * 设置切换动画
     * @param enterAnim
     * @param exitAnim
     */
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        mPluginActivity.overridePendingTransition(enterAnim, exitAnim);
    }
    
    /**
     * 启动一个fragment
     * @param classOfFragment
     * @param intent 启动fragment的intent
     */
    public void startFragment(Class<? extends PluginFragment> classOfFragment, Intent intent) {
        mPluginActivity.startFragment(classOfFragment, intent);
    }
    
    /**
     * 启动一个fragment
     * @param requestCode
     * @param classOfFragment
     * @param intent
     */
    public void startFragmentForResult(Class<? extends PluginFragment> classOfFragment, Intent intent, int requestCode) {
        mPluginActivity.startFragment(classOfFragment, intent, requestCode);
    }
    
    final void setRequestCode(int requestCode) {
        mRequestCode = requestCode;
    }
    
    public final void setResult(int resultCode, Intent data) {
        mResultCode = resultCode;
        mResultData = data;
    }
    
    /**
     * 得到当前的生命周期标记
     * @return
     */
    LifeCircle getLifeCircle() {
        return mLife;
    }
    
    /**
     * 获取title
     * @return
     */
    String getTitle() {
        return mPluginActivity.getTitle();
    }
    
    /**
     * 得到PluginContext对象
     * @return
     */
    public PluginContext getContext() {
        return mPluginActivity.getContext();
    }
    
    /**
     * 根据resId得到String
     * @param resId
     * @return
     */
    protected String getString(int resId) {
        return getContext().getString(resId);
    }
    
    /**
     * 得到name为当前PluginFragment类型名的{@link SharedPreferences}
     * 按照PluginFragment的类别分组
     * @param mode
     * @return
     */
    protected SharedPreferences getPreferences(int mode) {
        return getContext().getSharedPreferences(getClass().getName(), mode);
    }
    
    /**
     * finish掉自己
     */
    protected void finish() {
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        
        mPluginActivity.finishFragment(this, mRequestCode, mResultCode, mResultData);

        mRequestCode = DEFAULT_REQUEST_CODE;
        mResultCode = DEFAULT_RESULT_CODE;
        mResultData = null;
        
    }
    
    /**
     * log PluginFragment
     * @param msg
     */
    private void log(String msg) {
        LogUtil.i(LOG_TAG, "name:" + getClass().getName() + ":" + msg);
    }
    
    /**
     * PluginFragment的生命周期
     * @author michaelzuo
     *
     */
    public static enum LifeCircle {
        
        /**
         * 还未创建
         */
        UN_CREATE,
        
        /**
         * 已经创建
         */
        CREATED,
        
        /**
         * 在前台
         */
        FRONT,
        
        /**
         * 在后台 不可见
         */
        BACKGROUND,
        
        /**
         * 已经被销毁
         */
        DESTROYED
    }
}

