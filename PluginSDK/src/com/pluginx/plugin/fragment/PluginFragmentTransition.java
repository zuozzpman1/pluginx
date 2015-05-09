
package com.pluginx.plugin.fragment;

import com.pluginx.plugin.IPluginContextDelegate;
import com.pluginx.plugin.fragment.PluginFragment.LifeCircle;
import com.pluginx.plugin.util.LogUtil;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;

/**
 * 处理{@link PluginFragment}间的切换
 * 
 * @author michaelzuo
 */
public class PluginFragmentTransition {

    /**
     * 进入退出的默认动画
     */
    public static final int ANIM_DEFAULT = 0;

    /**
     * 向UI线程同步消息的Handler
     */
    private static final UIHandler sUIHandler;
    static {
        sUIHandler = new UIHandler(Looper.getMainLooper());
    }
    
    /**
     * 处理两个{@link PluginFragment}的切换
     * 
     * @param context android上下文对象
     * @param front 将要压在上边的{@link PluginFragment}
     * @param back 将要退到下边的{@link PluginFragment}
     * @param layoutInflater
     * @param contextDelegate
     * @param listener
     * @param enterAnim 进入动画
     * @param exitAnim 退出动画
     */
    public static void transition(final Context context, final PluginFragment front,
            PluginFragment back, final LayoutInflater layoutInflater,
            final IPluginContextDelegate contextDelegate,
            final IPluginFragmentTransitionListener listener, final int enterAnim, int exitAnim) {

        // back fragment 先pause
        if (back != null) {
            back.onPause();

            // 播放退出动画
            View backContentView = back.getActivity().getContentView();
            startTransitionAnim(context, backContentView, exitAnim, new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    LogUtil.i("transition onAnimationStart");
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    LogUtil.i("transition onAnimationEnd");
                    
                    // 启动新的fragment的进入动画
                    MsgEnterAnim msg = new MsgEnterAnim();
                    msg.context = context;
                    msg.contextDelegate = contextDelegate;
                    msg.front = front;
                    msg.layoutInflater = layoutInflater;
                    msg.listener = listener;
                    msg.enterAnim = enterAnim;
                    
                    sUIHandler.sendMessage(Message.obtain(sUIHandler, UIHandler.MSG_START_ENTER_ANIM, msg));
                }
            });
        } else {
            // 如果没有back 则直接播放enter的动画
            performEnter(context, front, layoutInflater, contextDelegate, listener, enterAnim);
        }
    }

    /**
     * 执行进入新fragment的逻辑
     * @param context
     * @param front
     * @param layoutInflater
     * @param contextDelegate
     * @param listener
     * @param enterAnim
     */
    private static void performEnter(Context context, PluginFragment front,
            LayoutInflater layoutInflater,
            IPluginContextDelegate contextDelegate,
            final IPluginFragmentTransitionListener listener, int enterAnim) {
        // createView
        final View view = front.onCreateView();

        // resume
        if (front.getLifeCircle() != LifeCircle.FRONT) {
            // 在前台则不需要resume
            front.onResume();
        }
        
        // 把view给到监听者装配完成
        listener.onEnd(view);
        
        // 播放fragment enter的动画
        startTransitionAnim(context, view, enterAnim, null);
    }

    /**
     * 播放切换动画
     * 
     * @param context
     * @param contentView
     * @param anim
     * @param listener
     */
    private static void startTransitionAnim(Context context, View contentView, int anim,
            AnimationListener listener) {
        // 目前的默认是没有切换动画
        if (anim == ANIM_DEFAULT) {
            // 直接告诉监听者动画结束
            if (listener != null) {
                listener.onAnimationEnd(null);
            }
            return;
        }

        Animation animation = AnimationUtils.loadAnimation(context, anim);
        if (listener != null) {
            animation.setAnimationListener(listener);
        }
        contentView.startAnimation(animation);
    }

    /**
     * 向UI线程中同步消息的Handler
     * @author michaelzuo
     *
     */
    private static class UIHandler extends Handler {
       
        /**
         * 启动进入的Fragment的动画
         */
        private static final int MSG_START_ENTER_ANIM = 1; 
        

        public UIHandler(Looper looper) {
            super(looper);
        }
        
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_ENTER_ANIM:
                    MsgEnterAnim msgEnterAnim = (MsgEnterAnim)msg.obj;
                    
                    Context context = msgEnterAnim.context;
                    PluginFragment front = msgEnterAnim.front;
                    LayoutInflater layoutInflater = msgEnterAnim.layoutInflater;
                    IPluginContextDelegate contextDelegate = msgEnterAnim.contextDelegate;
                    IPluginFragmentTransitionListener listener = msgEnterAnim.listener;
                    int enterAnim = msgEnterAnim.enterAnim;
                    
                    // 执行进入Fragment的初始化 以及 View动画播放逻辑
                    performEnter(context, front, layoutInflater, contextDelegate, listener, enterAnim);
                    break;
            }
            
        }
        
        
    }
    
    /**
     * 进入动画的数据结构
     * @author michaelzuo
     *
     */
    private static class MsgEnterAnim {
        Context context;
        PluginFragment front;
        LayoutInflater layoutInflater;
        IPluginContextDelegate contextDelegate;
        IPluginFragmentTransitionListener listener;
        int enterAnim;
    }
    
    /**
     * Fragment切换监听
     * 
     * @author michaelzuo
     */
    public static interface IPluginFragmentTransitionListener {

        /**
         * 切换结束
         * 
         * @param contentView 新的contentView
         */
        void onEnd(View contentView);

    }

}
