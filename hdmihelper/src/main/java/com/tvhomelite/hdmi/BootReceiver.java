package com.tvhomelite.hdmi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import java.lang.reflect.Method;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "HDMI1Startup";
    private static final String ROOT_DESC = "com.mstar.tv.service.interfaces.ITvServiceServer";
    private static final String COMMON_DESC = "com.mstar.tv.service.interfaces.ITvServiceServerCommon";
    private static final int HDMI1_ENUM_ORDINAL = 23;

    @Override public void onReceive(final Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;
        final PendingResult pending = goAsync();
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    Thread.sleep(12000L);
                    Exception last = null;
                    for (int attempt = 0; attempt < 20; attempt++) {
                        try {
                            switchToHdmi1();
                            Log.i(TAG, "HDMI 1 selected successfully");
                            last = null;
                            break;
                        } catch (Exception e) {
                            last = e;
                            Log.w(TAG, "TV service not ready; retry " + (attempt + 1), e);
                            Thread.sleep(3000L);
                        }
                    }
                    if (last != null) Log.e(TAG, "Unable to select HDMI 1", last);
                } catch (Exception e) {
                    Log.e(TAG, "Startup input switch failed", e);
                } finally { pending.finish(); }
            }
        }, "HDMI1-startup").start();
    }

    private static void switchToHdmi1() throws Exception {
        Class<?> sm = Class.forName("android.os.ServiceManager");
        Method getService = sm.getDeclaredMethod("getService", String.class);
        getService.setAccessible(true);
        IBinder root = (IBinder) getService.invoke(null, "tv_services");
        if (root == null) throw new RemoteException("tv_services binder is not available");
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        IBinder common;
        try {
            data.writeInterfaceToken(ROOT_DESC);
            if (!root.transact(3, data, reply, 0)) throw new RemoteException("getCommonManager transaction rejected");
            reply.readException();
            common = reply.readStrongBinder();
        } finally { data.recycle(); reply.recycle(); }
        if (common == null) throw new RemoteException("CommonManager binder is null");
        data = Parcel.obtain();
        reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(COMMON_DESC);
            data.writeInt(1);
            data.writeInt(HDMI1_ENUM_ORDINAL);
            if (!common.transact(11, data, reply, 0)) throw new RemoteException("SetInputSource transaction rejected");
            reply.readException();
        } finally { data.recycle(); reply.recycle(); }
    }
}
