package com.tvhomelite;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends Activity {
    private int dp(float value) {
        return (int)(value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().setBackgroundDrawableResource(android.R.color.black);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(28), dp(18), dp(28), dp(18));
        root.setBackgroundColor(Color.rgb(16, 24, 32));

        TextView heading = new TextView(this);
        heading.setText("TV HOME");
        heading.setTextColor(Color.WHITE);
        heading.setTextSize(26);
        heading.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        root.addView(heading);

        TextView sub = new TextView(this);
        sub.setText("Choose an app");
        sub.setTextColor(Color.LTGRAY);
        sub.setTextSize(14);
        sub.setPadding(0, dp(4), 0, dp(14));
        root.addView(sub);

        ScrollView scroll = new ScrollView(this);
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        grid.setUseDefaultMargins(true);

        Intent query = new Intent(Intent.ACTION_MAIN, null);
        query.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> apps = pm.queryIntentActivities(query, 0);
        Collections.sort(apps, new Comparator<ResolveInfo>() {
            public int compare(ResolveInfo a, ResolveInfo b) {
                return a.loadLabel(getPackageManager()).toString().compareToIgnoreCase(
                    b.loadLabel(getPackageManager()).toString());
            }
        });

        for (final ResolveInfo info : apps) {
            final String pkg = info.activityInfo.packageName;
            if (pkg.equals(getPackageName())) continue;
            Button b = new Button(this);
            b.setText(info.loadLabel(pm));
            b.setTextColor(Color.WHITE);
            b.setTextSize(17);
            b.setAllCaps(false);
            b.setMinHeight(dp(82));
            b.setBackgroundColor(Color.rgb(39, 55, 68));
            b.setFocusable(true);
            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent launch = getPackageManager().getLaunchIntentForPackage(pkg);
                    if (launch != null) startActivity(launch);
                }
            });
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = dp(210);
            lp.height = dp(82);
            lp.setMargins(dp(6), dp(6), dp(6), dp(6));
            grid.addView(b, lp);
        }

        scroll.addView(grid);
        root.addView(scroll, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

        Button settings = new Button(this);
        settings.setText("Settings");
        settings.setTextColor(Color.WHITE);
        settings.setTextSize(17);
        settings.setAllCaps(false);
        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
            }
        });
        root.addView(settings, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(54)));
        setContentView(root);
    }
}
