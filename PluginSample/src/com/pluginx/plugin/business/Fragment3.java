
package com.pluginx.plugin.business;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.pluginx.plugin.fragment.PluginFragment;

public class Fragment3 extends PluginFragment {

    @Override
    public View onCreateView() {
        Button b = new Button(getContext());
        b.setText("set result");
        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                
                if (mRequestCode == 1) {
                    Intent i = new Intent();
                    i.putExtra("startforresult", "fragment3 set result str");
                    setResult(2, i);
                }
                finish();
            }
        });
        return b;
    }
}
