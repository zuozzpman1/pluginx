
package com.pluginx.host.demo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pluginx.plugin.Plugin;
import com.pluginx.plugin.PluginManager;
import com.pluginx.plugin.host.HostActivity;
import com.pluginx.plugin.loader.PluginInstaller;
import com.pluginx.plugin.loader.PluginLoader.IPluginLoadCallback;
import com.pluginx.plugin.util.LogUtil;

/**
 * 测试插件的Activity
 * 
 * @author michaelzuo
 */
public class HomeActivity extends Activity implements OnItemClickListener {

    /**
     * 展现各个插件的ListView
     */
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_plugin_list);

        mListView = (ListView)findViewById(R.id.listView1);

        mListView.setOnItemClickListener(this);

        // 加载插件
        loadPlugin();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LogUtil.i("onDestroy");

        PluginManager.getInstance().destroy();
    }

    private void loadPlugin() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                // FIXME test
                try {
                    // FIXME clear first
                    PluginInstaller.clearPlugins(HomeActivity.this);

                    File installedPluginsDir = PluginInstaller
                            .installAllFromAssets(HomeActivity.this);
                    PluginManager.getInstance().load(HomeActivity.this,
                            installedPluginsDir.getAbsolutePath(), new IPluginLoadCallback() {

                                @Override
                                public void onLoad(final ArrayList<Plugin> plugins) {
                                    if (plugins != null) {
                                        mListView.setAdapter(new BaseAdapter() {

                                            @Override
                                            public View getView(int position, View convertView,
                                                    ViewGroup parent) {
                                                if (convertView == null) {
                                                    convertView = LayoutInflater.from(
                                                            HomeActivity.this).inflate(
                                                            R.layout.listitem_textview, null);
                                                }

                                                Plugin plugin = plugins.get(position);

                                                String title = plugin.getTitle();
                                                Bitmap icon = plugin.loadIcon();

                                                TextView tv = (TextView)convertView
                                                        .findViewById(R.id.tv);
                                                tv.setText(title);

                                                ImageView iv = (ImageView)convertView
                                                        .findViewById(R.id.icon);
                                                iv.setImageBitmap(icon);

                                                return convertView;
                                            }

                                            @Override
                                            public long getItemId(int position) {
                                                // TODO Auto-generated method
                                                // stub
                                                return 0;
                                            }

                                            @Override
                                            public Object getItem(int position) {
                                                // TODO Auto-generated method
                                                // stub
                                                return null;
                                            }

                                            @Override
                                            public int getCount() {
                                                return plugins.size();
                                            }
                                        });
                                    }
                                }
                            });
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

        }.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Plugin plugin = PluginManager.getInstance().getPlugin(position);
        if (plugin == null) {
            LogUtil.i("plugin in null in position:" + position);
            return;
        }

        String classNameOfPlugin = plugin.getPluginInfo().getClassName();
        HostActivity.startHostActivity(this, classNameOfPlugin, new Intent());
    }
}
