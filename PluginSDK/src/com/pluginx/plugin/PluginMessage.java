
package com.pluginx.plugin;

import android.content.Context;

/**
 * 宿主与插件通信协议
 * 
 * @author dazhengyang
 */
public class PluginMessage {

    private Context hostContext;

    private String pluginClassName;

    private int functionCode;

    private Object req;

    private Object resp;

    public Context getHostContext() {
        return hostContext;
    }

    public void setHostContext(Context hostContext) {
        this.hostContext = hostContext;
    }

    public String getPluginClassName() {
        return pluginClassName;
    }

    public void setPluginClassName(String pluginClassName) {
        this.pluginClassName = pluginClassName;
    }

    public int getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(int functionCode) {
        this.functionCode = functionCode;
    }

    public Object getReq() {
        return req;
    }

    public void setReq(Object req) {
        this.req = req;
    }

    public Object getResp() {
        return resp;
    }

    public void setResp(Object resp) {
        this.resp = resp;
    }

}
