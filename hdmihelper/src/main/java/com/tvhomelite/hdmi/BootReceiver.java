package com.tvhomelite.hdmi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "HDMI1Startup";
    private static final String SOURCE_ACTIVITY =
        "com.mstar.tvsetting.hotkey/.InputSourceListViewActivity";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;

        final PendingResult pending = goAsync();
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    // Give Android and the MStar TV service time to finish booting.
                    Thread.sleep(45000L);

                    String command =
                        "am start -n " + SOURCE_ACTIVITY +
                        "; sleep 4; " +
                        "i=0; while [ $i -lt 6 ]; do " +
                        "input keyevent 20; sleep 0.25; i=$((i+1)); " +
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
                    pending.finish();
                }
            }
        }, "HDMI1-boot-switch").start();
    }
}
