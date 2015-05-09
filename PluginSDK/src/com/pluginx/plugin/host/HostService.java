
package com.pluginx.plugin.host;

import com.pluginx.plugin.PluginContext;
import com.pluginx.plugin.PluginFactory;
import com.pluginx.plugin.PluginService;
import com.pluginx.plugin.PluginService.IPluginServiceDelegate;
import com.pluginx.plugin.util.LogUtil;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * 宿主service {@link Service}
 * 
 * @author michaelzuo
 */
public class HostService extends Service implements IPluginServiceDelegate {

    /**
     * 携带的所有service
     */
    private static ArrayList<PluginService> sPluginServices = new ArrayList<PluginService>();

    /**
     * 用来传递PluginService的className
     */
    private static String sPluginServiceClassName;

    /**
     * 用来传递PluginService是否被创建过
     */
    private static boolean sPluginServiceIsAlreadyThere;

    @Override
    public IBinder onBind(Intent intent) {
        // 插件的service 暂时不使用binder机制 会引入过多的复杂度
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }

        LogUtil.i("HostService onStartCommand");
        String pluginServiceClassName = sPluginServiceClassName;

        PluginService pluginService = getPluginService(pluginServiceClassName);
        if (pluginService == null) {
            return START_STICKY;
        }

        boolean isAlreadyThere = sPluginServiceIsAlreadyThere;
        if (!isAlreadyThere) {
            pluginService.setDelegate(this);
            pluginService.onCreate();
        }

        // call PluginService onStartCommand
        pluginService.onStartCommand(intent, flags, startId);

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        LogUtil.i("HostService onDestroy");

        synchronized (sPluginServices) {
            for (PluginService pluginService : sPluginServices) {
                pluginService.onDestroy();
            }

            sPluginServices.clear();
        }
    }

    /**
     * 启动service 直接调用这个方法 需要先提前动态加载插件完毕
     * 
     * @param baseContext
     * @param context
     * @param classNameOfPluginService 指定的{@link PluginService}的className
     * @param baseIntent
     * @return 是否成功找到对应的PluginService 并且启动成功
     */
    public static boolean startService(Context baseContext, PluginContext context,
            String classNameOfPluginService, Intent baseIntent) {
        Intent intent = new Intent(baseIntent);
        intent.setClass(baseContext, HostService.class);

        try {
            PluginService pluginService = getPluginService(classNameOfPluginService);

            // 如果PluginService不存在 需要创建一个
            if (pluginService == null) {
                pluginService = PluginFactory
                        .createPluginService(context, classNameOfPluginService);
                pluginService.setContext(context);
                addPluginService(pluginService);
                // intent.putExtra(EXTRA_NAME_PLUGIN_SERVICE_IS_ALREADY_THERE,
                // false);
                sPluginServiceIsAlreadyThere = false;
            } else {
                // intent.putExtra(EXTRA_NAME_PLUGIN_SERVICE_IS_ALREADY_THERE,
                // true);
                sPluginServiceIsAlreadyThere = true;
            }

            // 添加一个PluginService
            // 如果PluginService 已经被创建过了 则需要明确标识 保证PluginService的onCreate只创建一次
            // intent.putExtra(EXTRA_NAME_PLUGIN_SERVICE_CLASS_NAME,
            // classNameOfPluginService);
            sPluginServiceClassName = classNameOfPluginService;

            baseContext.startService(intent);
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 停止一个 {@link PluginService}
     * 
     * @param classNameOfPluginService 操作目标PluginService的类名
     * @return 是否找到了指定的 {@link PluginService}
     */
    public static boolean stopService(String classNameOfPluginService) {
        PluginService pluginService = getPluginService(classNameOfPluginService);
        if (pluginService == null) {
            return false;
        }

        return stopServiceImpl(pluginService);
    }

    /**
     * 添加一个PluginService
     * 
     * @param pluginService
     */
    private static void addPluginService(PluginService pluginService) {
        // check is duplicate
        synchronized (sPluginServices) {
            sPluginServices.add(pluginService);
        }
    }

    /**
     * 根据className获得对应的{@link PluginService}
     * 
     * @param className
     * @return may be null
     */
    private static PluginService getPluginService(String className) {
        synchronized (sPluginServices) {
            for (PluginService service : sPluginServices) {
                if (className.equals(service.getClass().getName())) {
                    return service;
                }
            }
        }

        return null;
    }

    @Override
    public void stopSelf(PluginService pluginService) {
        stopServiceImpl(pluginService);
    }

    /**
     * 停止PluginService
     * 
     * @param classNameOfPluginService
     * @return 是否找到了对应的service 并且进行了停止操作
     */
    private static boolean stopServiceImpl(PluginService pluginService) {
        synchronized (sPluginServices) {
            boolean suc = sPluginServices.remove(pluginService);
            if (!suc) {
                return false;
            }

            pluginService.onDestroy();
            return true;
        }
    }

    /**
     * 是否还有{@link PluginService} 存在
     * 
     * @return
     */
    public static boolean hasPluginService() {
        synchronized (sPluginServices) {
            return !sPluginServices.isEmpty();
        }
    }
}
