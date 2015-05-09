package com.pluginx.plugin;

import java.lang.reflect.InvocationTargetException;
import android.content.res.Resources;
import dalvik.system.DexClassLoader;

/**
 * 插件工厂, 负责插件的生产
 * @author michaelzuo
 *
 */
public class PluginFactory {
	
	/**
	 * 创建一个plugin
	 * @param pluginInfo 插件信息
	 * @param parentClassLoader 宿主的类加载器
	 * @param hostResources 宿主的Resources对象
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws NoSuchMethodException 
	 */
	public static final Plugin createPlugin(PluginInfo pluginInfo, ClassLoader parentClassLoader, Resources hostResources) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
		ClassLoader classLoader = createDexClassLoader(pluginInfo, parentClassLoader);
		
		// create instance with class name
		String clsName = pluginInfo.getClassName();
		Class<?> cls = classLoader.loadClass(clsName);
		Plugin plugin = (Plugin)cls.newInstance();
		
		// 加载插件
		plugin.onLoad(pluginInfo, classLoader, hostResources);
		return plugin;
	}
	
	/**
	 * 反射构造一个{@link PluginService}
	 * @param context
	 * @param classNameOfPluginService
	 * @return
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws ClassNotFoundException
	 */
	public static PluginService createPluginService(PluginContext context, String classNameOfPluginService) throws NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
	    ClassLoader loader = context.getClassLoader();
	    Class<?> cls = loader.loadClass(classNameOfPluginService);
        
        PluginService pluginService = (PluginService)cls.newInstance();
        
        return pluginService;
	}
	
	/**
	 * 创建DexClassLoader
	 * @param adapter
	 * @param hostClassLoader
	 * @return
	 */
	private static ClassLoader createDexClassLoader(IPluginAdapter adapter, ClassLoader hostClassLoader) {
		String dexPath = adapter.getDexPath();
		String optimizedDirectory = adapter.getOptimizedDirectory();
		String libraryPath = adapter.getLibraryPath();
		return new DexClassLoader(dexPath, optimizedDirectory, libraryPath, hostClassLoader);
		
//		DexClassLoader dexClassLoader = new DexClassLoader(dexPath, optimizedDirectory, libraryPath, hostClassLoader.getParent());
//		
//		  Field f;
//	        try {
//	            f = ClassLoader.class.getDeclaredField("parent");
//	            f.setAccessible(true);
//	            f.set(hostClassLoader, dexClassLoader);
//	        } catch (NoSuchFieldException e) {
//	            e.printStackTrace();
//	        } catch (IllegalArgumentException e) {
//	            e.printStackTrace();
//	        } catch (IllegalAccessException e) {
//	            e.printStackTrace();
//	        }       
//		
//		return hostClassLoader;
	}
}