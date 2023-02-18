package com.example.sos;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class Lock extends Service {
    DevicePolicyManager devicePolicyManager;
    ComponentName componentName;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, Home.MyDevicePolicyReceiver.class);
        devicePolicyManager.lockNow();
        stopSelf();
        return START_STICKY;
    }
}

