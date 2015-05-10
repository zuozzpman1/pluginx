package com.pluginx.plugin.business;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.pluginx.plugin.fragment.PluginFragment;
import com.pluginx.sample.R;

public class Fragment2 extends PluginFragment {

    @Override
    public View onCreateView() {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.plugin_two, null);
        
        String str = getIntent().getStringExtra("string");
        
        EditText editText = (EditText)contentView.findViewById(R.id.editText1);
        editText.setText(str);
        
        View button1 = contentView.findViewById(R.id.button1);
        button1.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("msg", "hey you see me");
//                getContext().startService(pluginService.getClass().getName(), intent);
                getContext().startPluginService(CustomerService.class.getName(), intent);
//                HostService.startService(getContext(), pluginService.getClass().getName(), intent);
            }
        });
        
        View button2 = contentView.findViewById(R.id.button2);
        button2.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                getContext().stopPluginService(CustomerService.class.getName());
            }
        });
        
        contentView.findViewById(R.id.button_show_notification).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                showNotification();
            }
        });
        
        return contentView;
    }
    
    /**
     * 展示一条notification
     */
    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
        builder.setContentTitle("我是title");
        builder.setContentText("我是内容");
        builder.setSmallIcon(R.drawable.icon1);

        Intent baseIntent = new Intent();
        baseIntent.putExtra("data", true);
        
        Intent intent = getLaunchIntent(baseIntent);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(pendingIntent);
        
        NotificationManager notificationMgr =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        
        notificationMgr.notify(0, builder.build());
    }
    

    @Override
    protected void finish() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.finish();
    }
    
    

}
