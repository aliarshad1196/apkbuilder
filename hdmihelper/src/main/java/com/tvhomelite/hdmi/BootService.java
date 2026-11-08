package com.tvhomelite.hdmi;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BootService extends Service {
    private static final String TAG = "TVFullscreenBoot";
    private Thread worker;

    @Override
    public int onStartCommand(final Intent intent, int flags, final int startId) {
        if (worker == null || !worker.isAlive()) {
            worker = new Thread(new Runnable() {
                @Override public void run() {
                    try {
                        // Give Android and the MStar TV services time to finish booting.
                        Thread.sleep(35000L);

                        Intent fullscreen = new Intent();
                        fullscreen.setComponent(new ComponentName(
                            "mstar.tvsetting.ui",
                            "mstar.tvsetting.ui.RootActivity"));
                        fullscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(fullscreen);

                        Log.i(TAG, "Requested MStar fullscreen TV activity");
                    } catch (Exception e) {
                        Log.e(TAG, "Could not launch fullscreen TV activity", e);
                    } finally {
                        stopSelf(startId);
                    }
                }
            }, "TV-fullscreen-boot");
            worker.start();
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}