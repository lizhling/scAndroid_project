/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidpn.client;

import java.util.Properties;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.utils.LogUtlis;

/** 
 * This class is to manage the notificatin service and to load the configuration.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public final class ServiceManager {

    private static final String LOGTAG = LogUtil
            .makeLogTag(ServiceManager.class);

    private Context context;

    private SharedPreferences sharedPrefs;

    private Properties props;

    private String version = "0.5.0";

    private String apiKey;

    private String xmppHost;

    private String xmppPort;

    public ServiceManager(Context context) {
        this.context = context;

        props = loadProperties();
        apiKey = props.getProperty("apiKey", "");
        xmppHost = props.getProperty("xmppHost", "127.0.0.1");
        xmppPort = props.getProperty("xmppPort", "5222");
        
        //for test
        String setXmppHost=App.sSettingsPreferences.getServerIp();
        String setXmppPort=App.sSettingsPreferences.getServerPort();
        if(setXmppHost!=null)
        	xmppHost=setXmppHost;
        if(setXmppPort!=null)
        	xmppPort=setXmppPort;
        
        sharedPrefs = context.getSharedPreferences(
                Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor editor = sharedPrefs.edit();
        editor.putString(Constants.API_KEY, apiKey);
        editor.putString(Constants.VERSION, version);
        editor.putString(Constants.XMPP_HOST, xmppHost);
        editor.putInt(Constants.XMPP_PORT, Integer.parseInt(xmppPort));
        editor.commit();
    }

    public void startService() {
//        Thread serviceThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
            	
            	LogUtlis.showLogI("ServiceManager", "starting pushmessageService");
                Intent intent = new Intent(context, PushMessageService.class);//PushMessageService.getIntent();
                context.startService(intent);
//            }
//        });
//        serviceThread.start();
    }

    public void stopService() {
        Intent intent = PushMessageService.getIntent();
        context.stopService(intent);
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try {
            int id = context.getResources().getIdentifier("androidpn", "raw",
                    context.getPackageName());
            props.load(context.getResources().openRawResource(id));
        } catch (Exception e) {
            LogUtlis.showLogE(LOGTAG, "Could not find the properties file.", e);
            // e.printStackTrace();
        }
        return props;
    }
    
    public void setNotificationIcon(int iconId) {
        Editor editor = sharedPrefs.edit();
        editor.putInt(Constants.NOTIFICATION_ICON, iconId);
        editor.commit();
    }

}
