
package com.pluginx.plugin.business;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.pluginx.plugin.PluginContentResolver;
import com.pluginx.plugin.PluginContext;
import com.pluginx.plugin.fragment.PluginFragment;
import com.pluginx.plugin.util.LogUtil;
import com.pluginx.sample.R;

public class Fragment1 extends PluginFragment {

    private SqliteDemo sqliteDemo;

    @Override
    public View onCreateView() {

        PluginContext context = getContext();
        
        LogUtil.i("Fragment1 pluginContext:" + context);
        
        sqliteDemo = new SqliteDemo(context);

        View view = LayoutInflater.from(context).inflate(R.layout.plugin_one, null);
        
        LogUtil.i("Fragment1 inflate view:" + view);

        view.findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("string", "for you");

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                startFragment(Fragment2.class, intent);
            }
        });
        
        view.findViewById(R.id.button2).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("string", "for you");

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                startFragmentForResult(Fragment3.class, intent, 1);
            }
        });

        view.findViewById(R.id.add_item).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                sqliteDemo.addItem();
            }
        });

        view.findViewById(R.id.query).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), sqliteDemo.query(), Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.test_provider).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                PluginContentResolver resolver = getContext().getPluginContentResolver();

                Uri uri = Uri.parse("content://com.test.provider");
                Cursor c = resolver.query(uri, null, null, null, null);

                String toast = "";
                while (c.moveToNext()) {
                    String user = c.getString(c.getColumnIndex("username"));
                    String password = c.getString(c.getColumnIndex("password"));

                    toast += user + " " + password + "\n";
                }

                Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
            }
        });

        String str = getContext().getString(R.string.test_get_string);

        TextView tv = (TextView)view.findViewById(R.id.textview1);
        tv.setText(str);

        return view;
    }

    public void onResult(int requestCode, int resultCode, Intent data) {
        String toast = "resulce code: " + resultCode + " str:" + data.getStringExtra("startforresult");
        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
    }
}
