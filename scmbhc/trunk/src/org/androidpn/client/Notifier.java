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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.R;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.database.ScmbhcDbManager;
import com.sunrise.scmbhc.entity.Message;
import com.sunrise.scmbhc.ui.activity.SingleFragmentActivity;
import com.sunrise.scmbhc.ui.fragment.MessageListFragment;
import com.sunrise.scmbhc.utils.LogUtlis;

/** 
 * This class is to notify the user of messages with NotificationManager.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class Notifier {

    private static final String LOGTAG = LogUtil.makeLogTag(Notifier.class);
    public final static int NOF_ID=0;

    private Context context;

    private SharedPreferences sharedPrefs;

    private NotificationManager notificationManager;
    private ScmbhcDbManager mMessageManager;

    public Notifier(Context context) {
        this.context = context;
        this.sharedPrefs = context.getSharedPreferences(
                Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        this.notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if(mMessageManager==null)
        	mMessageManager=ScmbhcDbManager.getInstance(context.getContentResolver());
    }

    public void notify(String notificationId, String apiKey, String title,
            String message, String uri, String optionType, String optionContent) {
        LogUtlis.showLogD(LOGTAG, "notify()...");

        LogUtlis.showLogD(LOGTAG, "notificationId=" + notificationId);
        LogUtlis.showLogD(LOGTAG, "notificationApiKey=" + apiKey);
        LogUtlis.showLogD(LOGTAG, "notificationTitle=" + title);
        LogUtlis.showLogD(LOGTAG, "notificationMessage=" + message);
        LogUtlis.showLogD(LOGTAG, "notificationUri=" + uri);
        LogUtlis.showLogD(LOGTAG, "optionType=" + optionType);
        LogUtlis.showLogD(LOGTAG, "optionContent=" + optionContent);

        if (isNotificationEnabled()) {
            // Show the toast
            if (isNotificationToastEnabled()) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }

            // Notification
            Notification notification = new Notification();
            notification.icon = getNotificationIcon();
            notification.defaults = Notification.DEFAULT_LIGHTS;
            if (isNotificationSoundEnabled()) {
                notification.defaults |= Notification.DEFAULT_SOUND;
            }
            if (isNotificationVibrateEnabled()) {
                notification.defaults |= Notification.DEFAULT_VIBRATE;
            }
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.when = System.currentTimeMillis();
            notification.tickerText = message;
            
            Intent intent = new Intent(context, SingleFragmentActivity.class);
			intent.putExtra(App.ExtraKeyConstant.KEY_FRAGMENT, MessageListFragment.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Intent intent1 = new Intent();
            intent1.setComponent(new ComponentName("com.sunrise.scmbhc", "com.sunrise.scmbhc.ui.activity.StartActivity"));
            PendingIntent contentIntent ;
            if ("5".equals(optionType)) {
            	contentIntent = PendingIntent.getActivity(context, 0,
                		intent, PendingIntent.FLAG_UPDATE_CURRENT);
                
            } else {
            	contentIntent = PendingIntent.getActivity(context, 0,
                		intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                	
            }
            
     	   Message msg=new Message(message, title, System.currentTimeMillis());
     	   msg.setNotificationId(notificationId);
     	   msg.setOptionType(optionType);
     	   msg.setOptionContent(optionContent);
     	   msg.setUserName(UserInfoControler.getInstance().getUserName());
     	   
            mMessageManager.recordMessage(msg);         
           int missMessageCount=mMessageManager.getMissMessageCount();
            if(missMessageCount>1)
            	message=missMessageCount+context.getString(R.string.new_message);
            notification.setLatestEventInfo(context, title, message,
            		contentIntent);
            notificationManager.notify(NOF_ID, notification);
        } else {
            LogUtlis.w(LOGTAG, "Notificaitons disabled.");
        }
    }

    private int getNotificationIcon() {
        return sharedPrefs.getInt(Constants.NOTIFICATION_ICON, 0);
    }

    private boolean isNotificationEnabled() {
        return sharedPrefs.getBoolean(Constants.SETTINGS_NOTIFICATION_ENABLED,
                true);
    }

    private boolean isNotificationSoundEnabled() {
        return sharedPrefs.getBoolean(Constants.SETTINGS_SOUND_ENABLED, true);
    }

    private boolean isNotificationVibrateEnabled() {
        return sharedPrefs.getBoolean(Constants.SETTINGS_VIBRATE_ENABLED, true);
    }

    private boolean isNotificationToastEnabled() {
        return sharedPrefs.getBoolean(Constants.SETTINGS_TOAST_ENABLED, false);
    }

}
