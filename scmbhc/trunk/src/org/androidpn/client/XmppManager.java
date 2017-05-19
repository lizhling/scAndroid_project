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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.provider.ProviderManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.text.TextUtils;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.utils.LogUtlis;

/**
 * This class is to manage the XMPP connection between client and server.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class XmppManager {

    private static final String LOGTAG = "XmppManager";

    private static final String XMPP_RESOURCE_NAME = "AndroidpnClient";

    private Context context;

    private PushMessageService.TaskSubmitter taskSubmitter;

    private PushMessageService.TaskTracker taskTracker;

    private SharedPreferences sharedPrefs;

    private String xmppHost;

    private int xmppPort;

    private XMPPConnection connection;

    private String username;

    private String password;

    private ConnectionListener connectionListener;

    private PacketListener notificationPacketListener;

    private Handler handler;

    private List<Runnable> taskList;

    private boolean running = false;

    private Future<?> futureTask;

    private Thread reconnection;

    public XmppManager(PushMessageService notificationService) {
        context = notificationService;
        taskSubmitter = notificationService.getTaskSubmitter();
        taskTracker = notificationService.getTaskTracker();
        sharedPrefs = notificationService.getSharedPreferences();

        connectionListener = new PersistentConnectionListener(this);
        notificationPacketListener = new NotificationPacketListener(this);

        handler = new Handler();
        taskList = new ArrayList<Runnable>();
        reconnection = new ReconnectionThread(this);
    }

	private void initConnectInfo() {
		xmppHost = sharedPrefs.getString(Constants.XMPP_HOST, "localhost");
        xmppPort = sharedPrefs.getInt(Constants.XMPP_PORT, 5222);
        username = App.sSettingsPreferences.getPushMessageUserName();
        password = App.sSettingsPreferences.getPushMessagePassword();
	}

    public Context getContext() {
        return context;
    }

    
	public void connect() {
		initConnectInfo();
		LogUtlis.showLogD(LOGTAG, "xmppHost:"+xmppHost);
		LogUtlis.showLogD(LOGTAG, "xmppPort:"+xmppPort);
		LogUtlis.showLogD(LOGTAG, "username:"+username);
		LogUtlis.showLogD(LOGTAG, "password:"+password);
		LogUtlis.showLogD(LOGTAG, "XmppManager:connect()...");
		submitLoginTask();
	}

	public void disconnect() {
		LogUtlis.showLogD(LOGTAG, "XmppManager:disconnect()...");
		terminatePersistentConnection();
	}

	public void terminatePersistentConnection() {
		LogUtlis.showLogD(LOGTAG, "XmppManager:terminatePersistentConnection()...");
		Runnable runnable = new Runnable() {

			final XmppManager xmppManager = XmppManager.this;

			public void run() {
				if (xmppManager.isConnected()) {
					LogUtlis.showLogD(LOGTAG, "XmppManager:terminatePersistentConnection()... run()");
					xmppManager.getConnection().removePacketListener(
							xmppManager.getNotificationPacketListener());
					xmppManager.getConnection().disconnect();
				}
				xmppManager.runTask();
			}

        };
        addTask(runnable);
    }

    public XMPPConnection getConnection() {
        return connection;
    }

    public void setConnection(XMPPConnection connection) {
        this.connection = connection;
    }

  
    public ConnectionListener getConnectionListener() {
        return connectionListener;
    }

    public PacketListener getNotificationPacketListener() {
        return notificationPacketListener;
    }

    public void startReconnectionThread() {
        synchronized (reconnection) {
            if (!reconnection.isAlive()) {
                reconnection.setName("Xmpp Reconnection Thread");
                reconnection.start();
            }
        }
    }

    public Handler getHandler() {
        return handler;
    }

    public void reregisterAccount() {
        removeAccount();
        submitLoginTask();
        runTask();
    }

    public List<Runnable> getTaskList() {
        return taskList;
    }

    public Future<?> getFutureTask() {
        return futureTask;
    }

	public void runTask() {
		LogUtlis.showLogD(LOGTAG, "XmppManager:runTask()...");
		synchronized (taskList) {
			running = false;
			futureTask = null;
			if (!taskList.isEmpty()) {
				Runnable runnable = (Runnable) taskList.get(0);
				taskList.remove(0);
				running = true;
				futureTask = taskSubmitter.submit(runnable);
				if (futureTask == null) {
					taskTracker.decrease();
				}
			}
		}
		taskTracker.decrease();
		LogUtlis.showLogD(LOGTAG, "XmppManager:runTask()...done");
	}

/*    private String newRandomUUID() {
        String uuidRaw = UUID.randomUUID().toString();
        return uuidRaw.replaceAll("-", "");
    }*/

    private boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    private boolean isAuthenticated() {
        return connection != null && connection.isConnected()
                && connection.isAuthenticated();
    }

/*    private boolean isRegistered() {
        return sharedPrefs.contains(Constants.XMPP_USERNAME)
                && sharedPrefs.contains(Constants.XMPP_PASSWORD);
    }*/

	private void submitConnectTask() {
		LogUtlis.showLogD(LOGTAG, "XmppManager��submitConnectTask()...");
		addTask(new ConnectTask());
	}

/*	private void submitRegisterTask() {
		LogUtlis.showLogD(LOGTAG, "XmppManager��submitRegisterTask()...");
		submitConnectTask();
		addTask(new RegisterTask());
	}*/

	private void submitLoginTask() {
		LogUtlis.showLogD(LOGTAG, "XmppManager��submitLoginTask()...");
		submitConnectTask();
		//submitRegisterTask();
		addTask(new LoginTask());
	}

	private void addTask(Runnable runnable) {
		LogUtlis.showLogD(LOGTAG, "XmppManager��addTask(runnable)...");
		taskTracker.increase();
		synchronized (taskList) {
			if (taskList.isEmpty() && !running) {
				running = true;
				futureTask = taskSubmitter.submit(runnable);
				if (futureTask == null) {
					taskTracker.decrease();
				}
			} else {
				LogUtlis.showLogD(LOGTAG, "XmppManager:addTask(runnable)... rerun Task");
				//��������������,�ͻ��˲��ܳɹ�l��androidpn������
	            runTask();
				taskList.add(runnable);
			}
		}
		LogUtlis.showLogD(LOGTAG, "XmppManager:addTask(runnable)... done");
	}

    private void removeAccount() {
        Editor editor = sharedPrefs.edit();
        editor.remove(Constants.XMPP_USERNAME);
        editor.remove(Constants.XMPP_PASSWORD);
        editor.commit();
    }

    /**
     * A runnable task to connect the server. 
     */
    private class ConnectTask implements Runnable {

        final XmppManager xmppManager;

        private ConnectTask() {
            this.xmppManager = XmppManager.this;
        }

		public void run() {
			LogUtlis.showLogI(LOGTAG, "XmppManager: ConnectTask.run()...");

            if (!xmppManager.isConnected()) {
                // Create the configuration for this new connection
                ConnectionConfiguration connConfig = new ConnectionConfiguration(
                        xmppHost, xmppPort);
                // connConfig.setSecurityMode(SecurityMode.disabled);
                connConfig.setSecurityMode(SecurityMode.required);
                connConfig.setSASLAuthenticationEnabled(false);
                connConfig.setCompressionEnabled(false);

                XMPPConnection connection = new XMPPConnection(connConfig);
                xmppManager.setConnection(connection);

				try {
					// Connect to the server
					connection.connect();
					LogUtlis.showLogI(LOGTAG, "XmppManager: XMPP connected successfully");

                    // packet provider
                    ProviderManager.getInstance().addIQProvider("notification",
                            "androidpn:iq:notification",
                            new NotificationIQProvider());

                } catch (XMPPException e) {
                    LogUtlis.showLogE(LOGTAG, "XMPP connection failed", e);
                }

                xmppManager.runTask();

			} else {
				LogUtlis.showLogI(LOGTAG, "XmppManager: XMPP connected already");
				xmppManager.runTask();
			}
		}
	}

  /*  *//**
     * A runnable task to register a new user onto the server. 
     *//*
    private class RegisterTask implements Runnable {

        final XmppManager xmppManager;

        private RegisterTask() {
            xmppManager = XmppManager.this;
        }

        public void run() {
            LogUtlis.showLogI(LOGTAG, "RegisterTask.run()...");

            if (!xmppManager.isRegistered()) {
                final String newUsername = newRandomUUID();
                final String newPassword = newRandomUUID();

                Registration registration = new Registration();

                PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
                        registration.getPacketID()), new PacketTypeFilter(
                        IQ.class));

                PacketListener packetListener = new PacketListener() {

                    public void processPacket(Packet packet) {
                        LogUtlis.showLogD("RegisterTask.PacketListener",
                                "processPacket().....");
                        LogUtlis.showLogD("RegisterTask.PacketListener", "packet="
                                + packet.toXML());

                        if (packet instanceof IQ) {
                            IQ response = (IQ) packet;
                            if (response.getType() == IQ.Type.ERROR) {
                                if (!response.getError().toString().contains(
                                        "409")) {
                                    LogUtlis.e(LOGTAG,
                                            "Unknown error while registering XMPP account! "
                                                    + response.getError()
                                                            .getCondition());
                                }
                            } else if (response.getType() == IQ.Type.RESULT) {
                                xmppManager.setUsername(newUsername);
                                xmppManager.setPassword(newPassword);
                                LogUtlis.showLogD(LOGTAG, "username=" + newUsername);
                                LogUtlis.showLogD(LOGTAG, "password=" + newPassword);

                                Editor editor = sharedPrefs.edit();
                                editor.putString(Constants.XMPP_USERNAME,
                                        newUsername);
                                editor.putString(Constants.XMPP_PASSWORD,
                                        newPassword);
                                editor.commit();
                                Log
                                        .showLogI(LOGTAG,
                                                "Account registered successfully");
                                xmppManager.runTask();
                            }
                        }
                    }
                };

                connection.addPacketListener(packetListener, packetFilter);

                registration.setType(IQ.Type.SET);
                // registration.setTo(xmppHost);
                // Map<String, String> attributes = new HashMap<String, String>();
                // attributes.put("username", rUsername);
                // attributes.put("password", rPassword);
                // registration.setAttributes(attributes);
                registration.addAttribute("username", newUsername);
                registration.addAttribute("password", newPassword);
                connection.sendPacket(registration);

            } else {
                LogUtlis.showLogI(LOGTAG, "Account registered already");
                xmppManager.runTask();
            }
        }
    }*/

    /**
     * A runnable task to log into the server. 
     */
    private int waiting;
    private class LoginTask implements Runnable {

        final XmppManager xmppManager;

        private LoginTask() {
            this.xmppManager = XmppManager.this;
        }

        private int waiting() {
            if (waiting > 20) {
                return 600;
            }
            if (waiting > 13) {
                return 300;
            }
            return waiting <= 7 ? 10 : 60;
        }
        
		public void run() {
			LogUtlis.showLogI(LOGTAG, "XmppManager: XmppManager: LoginTask.run()...");

			if (!xmppManager.isAuthenticated()) {
				LogUtlis.showLogI(LOGTAG, "XmppManager: LoginTask.run()... Start Logged in.");
				LogUtlis.showLogD(LOGTAG, "XmppManager:username=" + username);
				LogUtlis.showLogD(LOGTAG, "XmppManager:password=" + password);

				try {
					if (TextUtils.isEmpty(username)) {
						LogUtlis.showLogE(LOGTAG, "XmppManager: LoginTask.run()... username empty!");
						xmppManager.startReconnectionThread();
						throw new Exception("username empty");
					}
					
					xmppManager.getConnection().login(
							username,
							password, 
							XMPP_RESOURCE_NAME);
					
					LogUtlis.showLogD(LOGTAG, "XmppManager:Loggedn in successfully");

                    // connection listener
                    if (xmppManager.getConnectionListener() != null) {
                        xmppManager.getConnection().addConnectionListener(
                                xmppManager.getConnectionListener());
                    }

                    // packet filter
                    PacketFilter packetFilter = new PacketTypeFilter(
                            NotificationIQ.class);
                    // packet listener
                    PacketListener packetListener = xmppManager
                            .getNotificationPacketListener();
                    connection.addPacketListener(packetListener, packetFilter);

                    xmppManager.runTask();

                } catch (XMPPException e) {
                    LogUtlis.showLogE(LOGTAG, "LoginTask.run()... xmpp error");
                    LogUtlis.showLogE(LOGTAG, "Failed to login to xmpp server. Caused by: "
                            + e.getMessage());
                    String INVALID_CREDENTIALS_ERROR_CODE = "401";
                    String errorMessage = e.getMessage();
					if (errorMessage != null && errorMessage.contains(INVALID_CREDENTIALS_ERROR_CODE)) {
						  try {
							Thread.sleep((long) waiting() * 1000L);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						LogUtlis.showLogD(LOGTAG, "XmppManager:ReLoggedn in successfully");
						xmppManager.reregisterAccount();
			            waiting++;
                        return;
                    }
                    xmppManager.startReconnectionThread();

                } catch (Exception e) {
                    LogUtlis.showLogE(LOGTAG, "LoginTask.run()... other error");
                    LogUtlis.showLogE(LOGTAG, "Failed to login to xmpp server. Caused by: "
                            + e.getMessage());
                    xmppManager.startReconnectionThread();
                }

			} else {
				LogUtlis.showLogI(LOGTAG, "XmppManager: Logged in already");
				xmppManager.runTask();
			}

        }
    }

}
