package com.tvhomelite.hdmi;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class BootService extends Service {
    private static final String TAG = "HDMI1Startup";
    private Thread worker;

    @Override
    public int onStartCommand(final Intent intent, int flags, final int startId) {
        if (worker == null || !worker.isAlive()) {
            worker = new Thread(new Runnable() {
                @Override public void run() {
                    try {
                        // Allow Android and the MStar TV service to finish booting.
                        Thread.sleep(45000L);

                        String command =
                            "am start -n com.mstar.tvsetting.hotkey/.InputSourceListViewActivity" +
                            "; sleep 4; " +
                            "i=0; while [ $i -lt 6 ]; do " +
                            "input keyevent 20; sleep 1; i=$((i+1)); " +
                            "done; input keyevent 23";

                        Process process = Runtime.getRuntime().exec(
                            new String[] { "su", "-c", command });

                        BufferedReader stdout = new BufferedReader(
                            new InputStreamReader(process.getInputStream()));
                        BufferedReader stderr = new BufferedReader(
                            new InputStreamReader(process.getErrorStream()));

                        String line;
                        while ((line = stdout.readLine()) != null) {
                            Log.i(TAG, "su: " + line);
                        }
                        while ((line = stderr.readLine()) != null) {
                            Log.w(TAG, "su: " + line);
                        }

                        int exitCode = process.waitFor();
                        if (exitCode == 0) {
                            Log.i(TAG, "Boot HDMI 1 key sequence completed");
                        } else {
                            Log.e(TAG, "Boot HDMI 1 command failed, exit=" + exitCode);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Boot HDMI 1 switch failed", e);
                    } finally {
                        stopSelf(startId);
                    }
                }
            }, "HDMI1-boot-switch");
            worker.start();
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
