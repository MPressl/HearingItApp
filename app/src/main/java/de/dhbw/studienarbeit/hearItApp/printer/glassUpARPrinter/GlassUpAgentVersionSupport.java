package de.dhbw.studienarbeit.hearItApp.printer.glassUpARPrinter;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;

import glassup.service.GlassUpAgentInterface;
import glassup.service.GlassUpEvent;

/**
 * Created by root on 1/30/17.
 */

public class GlassUpAgentVersionSupport {
        private static final String PREF_NAME = "glass_up_agent";
        private static final String PREF_APP_CONFIGURATION = "app_config_status";
        private static final int APP_CONFIGURED = 1;
        private static final int APP_NOT_CONFIGURED = 0;
        private int serviceBroadcastMask;
        private Context context;
        private GlassUpAgentInterface.EventListener eventListener;
        private GlassUpAgentInterface.ContentResultListener contentListener;
        private GlassUpAgentInterface.ConnectionListener connectionListener;
        private GlassUpAgentVersionSupport.AgentBroadcastReceiver receiverAgentBroadcast;
        private GlassUpAgentVersionSupport.ServiceBroadcastReceiver receiverServiceBroadcast;
        private boolean isConfigured;
        private int connectionStatus;
        private SharedPreferences prefs;
        private Intent intentConfigure;

        public GlassUpAgentVersionSupport() {
        }

        public void setEventListener(GlassUpAgentInterface.EventListener eventListener) {
            this.eventListener = eventListener;
        }

        public void setContentResultListener(GlassUpAgentInterface.ContentResultListener contentListener) {
            this.contentListener = contentListener;
        }

        public void setConnectionListener(GlassUpAgentInterface.ConnectionListener mConnectionListener) {
            this.connectionListener = mConnectionListener;
        }

        public void onCreate(Context c) {
            this.context = c;
            this.registerToAgentBroadcast();
            this.registerToServiceBroadcast();
            Intent i = new Intent("glassup.service.action.CONNECTION_STATUS");
            i.setPackage("glassup.service");
            i.putExtra("appId", this.context.getPackageName());
            this.context.startService(i);
            this.isConfigured = false;
        }

        public void onResume() {
            Intent i = new Intent("glassup.service.action.CONNECTION_STATUS");
            i.setPackage("glassup.service");
            i.putExtra("appId", this.context.getPackageName());
            this.context.startService(i);
        }

        public void onPause() {
        }

        public void onDestroy() {
            this.unregisterFromAgentBroadcast();
            this.unregisterFromServiceBroadcast();
            this.context = null;
        }

        public int getConnectionStatus() {
            return this.connectionStatus;
        }

        public boolean isConnected() {
            return this.connectionStatus == 2;
        }

        public boolean isConfigured() {
            return this.isConfigured;
        }

        public void sendConfigure(int[] bitmapResIds) {
            Bitmap[] bitmaps = new Bitmap[bitmapResIds.length];

            for(int b = 0; b < bitmapResIds.length; ++b) {
                BitmapDrawable drw = (BitmapDrawable)this.context.getResources().getDrawable(bitmapResIds[b]);
                bitmaps[b] = drw.getBitmap();
                Log.d("GlassUpAgent", "W->" + Integer.toString(bitmaps[b].getWidth()));
                Log.d("GlassUpAgent", "H->" + Integer.toString(bitmaps[b].getHeight()));
            }

            this.saveIntent();
            this.sendConfiguration(bitmaps);
        }

        public void sendConfiguration(Bitmap[] bitmaps) {
            if(this.isConfigured) {
                Log.d("GlassUpAgent", "sendConfig ---:> THE APPs is already configured!!");
            } else {
                Intent intent = new Intent("glassup.service.action.SEND_AGENT_CONFIGURATION");
                intent.setPackage("glassup.service");
                intent.putExtra("appId", this.context.getPackageName());
                intent.putExtra("images", bitmaps);
                this.intentConfigure = intent;
                this.saveIntent();
                this.context.startService(intent);
            }
        }

        private void saveIntent() {
        }

        private void loadIntent() {
        }

        public void registerToEvent(int type) {
            switch(type) {
                case 16:
                    this.serviceBroadcastMask |= 16;
                    break;
                case 32:
                    this.serviceBroadcastMask |= 32;
                    break;
                case 64:
                    this.serviceBroadcastMask |= 64;
            }

            this.registerToServiceBroadcast();
        }

        public void unregisterToEvent(int type) {
            switch(type) {
                case 16:
                    this.serviceBroadcastMask &= -17;
                    break;
                case 32:
                    this.serviceBroadcastMask &= -33;
                    break;
                case 64:
                    this.serviceBroadcastMask &= -65;
            }

            this.registerToServiceBroadcast();
        }

        public void sendContent(int contentId, int layoutId, String[] imagesId, String[] texts) {
            if(!this.isConfigured) {
                Log.d("GlassUpAgent", "sendGraphicsAndText ---:> THE APPs is !NOT! configured, can\'t send text!!!");
            } else {
                Intent intent = new Intent("glassup.service.action.SEND_AGENT_CONTENT");
                intent.setPackage("glassup.service");
                intent.putExtra("appId", this.context.getPackageName());
                intent.putExtra("contentId", contentId);
                intent.putExtra("layoutId", layoutId);
                intent.putExtra("texts", texts);
                intent.putExtra("images.id", imagesId);
                this.context.startService(intent);
            }
        }

        private void registerToAgentBroadcast() {
            String actionConfig = this.context.getPackageName() + "." + "CONFIGURATION_STATUS";
            String actionContent = this.context.getPackageName() + "." + "CONTENT_STATUS";
            String actionEvent = this.context.getPackageName() + "." + "EVENT_BUTTON";
            IntentFilter filter = new IntentFilter();
            filter.addAction(actionConfig);
            filter.addAction(actionContent);
            filter.addAction(actionEvent);
            this.receiverAgentBroadcast = new GlassUpAgentVersionSupport
                    .AgentBroadcastReceiver();
            this.context.registerReceiver(this.receiverAgentBroadcast, filter);
        }

        private void unregisterFromAgentBroadcast() {
            if(this.receiverAgentBroadcast != null) {
                this.context.unregisterReceiver(this.receiverAgentBroadcast);
                this.receiverAgentBroadcast = null;
            }

        }

        private void registerToServiceBroadcast() {
            if(this.receiverServiceBroadcast != null) {
                this.context.unregisterReceiver(this.receiverServiceBroadcast);
            }

            IntentFilter filter = new IntentFilter();
            filter.addAction("glassup.service.action.CONNECTION_STATUS");
            filter.addAction("glassup.service.action.EVENT_BATTERY");
            if((this.serviceBroadcastMask & 16) != 0) {
                filter.addAction("glassup.service.action.EVENT_ACCELEROMETER");
                Log.d("GlassUpAgent", "Register to Accelerometer events");
            }

            if((this.serviceBroadcastMask & 32) != 0) {
                filter.addAction("glassup.service.action.EVENT_COMPASS");
                Log.d("GlassUpAgent", "Register to Compass events");
            }

            if((this.serviceBroadcastMask & 64) != 0) {
                filter.addAction("glassup.service.action.EVENT_LIGHT_SENSOR");
                Log.d("GlassUpAgent", "Register to Light_sensor events");
            }

            if(this.receiverServiceBroadcast == null) {
                this.receiverServiceBroadcast = new GlassUpAgentVersionSupport
                        .ServiceBroadcastReceiver();
                Log.d("GlassUpAgent", "Register initial state");
            }

            this.context.registerReceiver(this.receiverServiceBroadcast, filter);
        }

        private void unregisterFromServiceBroadcast() {
            if(this.receiverServiceBroadcast != null) {
                this.context.unregisterReceiver(this.receiverServiceBroadcast);
                this.receiverServiceBroadcast = null;
            }

        }

        private void notifyEventListener(int type, float[] values) {
            if(this.eventListener != null) {
                GlassUpEvent event = new GlassUpEvent(type);
                if(values != null) {
                    System.arraycopy(values, 0, event.values, 0, values.length);
                }

                this.eventListener.onEvent(event);
            }

        }

        private void notifyEventButtonContentListener(int contentId, float[] values) {
            GlassUpEvent event = new GlassUpEvent(4);
            if(values != null) {
                System.arraycopy(values, 0, event.values, 0, values.length);
            }

            this.eventListener.onButtonEvent(event, contentId);
        }

        private void notifyContentListener(int contentId, int status, String statusMessage) {
            if(this.contentListener != null) {
                Log.d("GlassUpAgent", "Notify Content status");
                this.contentListener.onContentResult(contentId, status, statusMessage);
            }

        }

private class AgentBroadcastReceiver extends BroadcastReceiver {
    private AgentBroadcastReceiver() {
    }

    @SuppressLint({"NewApi"})
    public void onReceive(Context context, Intent intent) {
        Log.d("GlassUpAgent", "RECEIVED INTENT");
        String pkg = context.getPackageName();
        String actionConfig = pkg + "." + "CONFIGURATION_STATUS";
        String actionContent = pkg + "." + "CONTENT_STATUS";
        String actionEvent = pkg + "." + "EVENT_BUTTON";
        if(intent == null) {
            Log.d("GlassUpAgent", "RECEIVED NULL INTENT!!!!");
        } else {
            String action = intent.getAction();
            if(action != null && action.equals(actionConfig)) {
                Log.d("GlassUpAgent", "RECEIVED CONFIG STATUS!!!!");
                int data1 = intent.getExtras().getInt("response.status.extra");
                if(data1 == 1) {
                    GlassUpAgentVersionSupport.this.isConfigured = true;
                } else {
                    GlassUpAgentVersionSupport.this.isConfigured = false;
                }
            } else {
                Bundle data;
                if(action != null && action.equals(actionContent)) {
                    Log.d("GlassUpAgent", "RECEIVED CONTENT STATUS!!!!");
                    data = intent.getExtras();
                    if(data != null) {
                        GlassUpAgentVersionSupport.this
                                .notifyContentListener(data.getInt("contentId"),
                                        data.getInt("response.status.extra"),
                                        data.getString("response.status.extra.message"));
                    }
                } else if(action != null && action.equals(actionEvent)) {
                    Log.d("GlassUpAgent", "RECEIVED EVENT CONTENT!!!!");
                    data = intent.getExtras();
                    if(data != null) {
                        GlassUpAgentVersionSupport.this
                                .notifyEventButtonContentListener(
                                        data.getInt("contentId"),
                                        data.getFloatArray("event.values"));
                    }
                }
            }

        }
    }
}

private class ServiceBroadcastReceiver extends BroadcastReceiver {
    private ServiceBroadcastReceiver() {
    }

    public void onReceive(Context context, Intent intent) {
        Log.d("GlassUpAgent", "RECEIVED INTENT");
        if(intent == null) {
            Log.d("GlassUpAgent", "RECEIVED NULL INTENT!!!!");
        } else {
            String action = intent.getAction();
            Bundle args = intent.getExtras();
            if(action != null && action.equals("glassup.service.action.CONNECTION_STATUS")) {
                Log.d("GlassUpAgent", "RECEIVED CONNECTION STATUS!!!!");
                if(args.getInt("connection.status.extra") == 2) {
                    GlassUpAgentVersionSupport.this.connectionStatus = 2;
                    if(GlassUpAgentVersionSupport.this.intentConfigure != null) {
                        context.startService(GlassUpAgentVersionSupport.this.intentConfigure);
                    }
                } else if(args.getInt("connection.status.extra") == 1) {
                    GlassUpAgentVersionSupport.this.connectionStatus = 1;
                } else if(args.getInt("connection.status.extra") == 0) {
                    GlassUpAgentVersionSupport.this.connectionStatus = 0;
                    GlassUpAgentVersionSupport.this.isConfigured = false;
                }

                if(GlassUpAgentVersionSupport.this.connectionListener != null) {
                    GlassUpAgentVersionSupport.this.connectionListener
                            .onConnectionChanged(GlassUpAgentVersionSupport.this.connectionStatus);
                }
            } else if(action != null && action.equals("glassup.service.action.EVENT_BATTERY")) {
                if(!args.containsKey("event.values")) {
                    Log.d("GlassUpAgent", "ERRORS received event with no VALUEs .....");
                    return;
                }

                GlassUpAgentVersionSupport.this.notifyEventListener(2, args.getFloatArray("event.values"));
            } else if(action != null && action.equals("glassup.service.action.EVENT_ACCELEROMETER")) {
                if(!args.containsKey("event.values")) {
                    Log.d("GlassUpAgent", "ERRORS received event with no VALUEs .....");
                    return;
                }

                GlassUpAgentVersionSupport.this.notifyEventListener(16, args.getFloatArray("event.values"));
            } else if(action != null && action.equals("glassup.service.action.EVENT_COMPASS")) {
                if(!args.containsKey("event.values")) {
                    Log.d("GlassUpAgent", "ERRORS received event with no VALUEs .....");
                    return;
                }

                GlassUpAgentVersionSupport.this.notifyEventListener(32, args.getFloatArray("event.values"));
            } else if(action != null && action.equals("glassup.service.action.EVENT_LIGHT_SENSOR")) {
                if(!args.containsKey("event.values")) {
                    Log.d("GlassUpAgent", "ERRORS received event with no VALUEs .....");
                    return;
                }

                GlassUpAgentVersionSupport.this.notifyEventListener(64, args.getFloatArray("event.values"));
            }

        }
    }
}
}
