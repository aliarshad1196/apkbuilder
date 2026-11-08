package com.tvhomelite;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
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

    private GradientDrawable shape(int color, int strokeColor, int strokeWidth) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(12));
        if (strokeWidth > 0) d.setStroke(dp(strokeWidth), strokeColor);
        return d;
    }

    private StateListDrawable tileBackground() {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_focused},
            shape(Color.rgb(28, 103, 158), Color.rgb(112, 202, 255), 2));
        states.addState(new int[]{android.R.attr.state_pressed},
            shape(Color.rgb(20, 82, 126), Color.rgb(112, 202, 255), 2));
        states.addState(new int[]{},
            shape(Color.rgb(32, 45, 59), Color.rgb(48, 65, 82), 1));
        return states;
    }

    private TextView label(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        t.setTypeface(Typeface.DEFAULT, bold ? Typeface.BOLD : Typeface.NORMAL);
        return t;
    }

    private View makeTile(String title, android.graphics.drawable.Drawable icon,
                          final Runnable action, int height) {
        LinearLayout tile = new LinearLayout(this);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER);
        tile.setPadding(dp(8), dp(10), dp(8), dp(8));
        tile.setBackgroundDrawable(tileBackground());
        tile.setFocusable(true);
        tile.setClickable(true);
        tile.setContentDescription(title);
        if (icon != null) {
            ImageView image = new ImageView(this);
            image.setImageDrawable(icon);
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(dp(48), dp(48));
            ip.bottomMargin = dp(8);
            tile.addView(image, ip);
        } else {
            TextView symbol = label("⚙", 34, Color.WHITE, true);
            tile.addView(symbol, new LinearLayout.LayoutParams(dp(48), dp(48)));
        }
        TextView name = label(title, 15, Color.WHITE, true);
        name.setSingleLine(true);
        name.setEllipsize(android.text.TextUtils.TruncateAt.END);
        tile.addView(name, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(28)));
        tile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { action.run(); }
        });
        tile.setMinimumHeight(dp(height));
        return tile;
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(30), dp(22), dp(30), dp(18));
        root.setBackgroundColor(Color.rgb(12, 18, 27));

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout titles = new LinearLayout(this);
        titles.setOrientation(LinearLayout.VERTICAL);
        TextView heading = label("TV HOME", 28, Color.WHITE, true);
        heading.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        titles.addView(heading);
        TextView sub = label("YOUR APPS, ALL IN ONE PLACE", 11,
            Color.rgb(145, 166, 185), true);
        sub.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        sub.setPadding(0, dp(3), 0, 0);
        titles.addView(sub);
        header.addView(titles, new LinearLayout.LayoutParams(0,
            LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView badge = label("SELECT AN APP", 11, Color.rgb(170, 194, 214), true);
        badge.setPadding(dp(14), dp(8), dp(14), dp(8));
        badge.setBackgroundDrawable(shape(Color.rgb(25, 37, 51), 0, 0));
        header.addView(badge);
        root.addView(header);

        View line = new View(this);
        line.setBackgroundColor(Color.rgb(39, 55, 71));
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        lineParams.topMargin = dp(16);
        lineParams.bottomMargin = dp(16);
        root.addView(line, lineParams);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(false);
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        grid.setUseDefaultMargins(false);

        Intent query = new Intent(Intent.ACTION_MAIN, null);
        query.addCategory(Intent.CATEGORY_LAUNCHER);
        final PackageManager pm = getPackageManager();
        List<ResolveInfo> apps = pm.queryIntentActivities(query, 0);
        Collections.sort(apps, new Comparator<ResolveInfo>() {
            public int compare(ResolveInfo a, ResolveInfo b) {
                return a.loadLabel(pm).toString().compareToIgnoreCase(
                    b.loadLabel(pm).toString());
            }
        });

        int count = 0;
        for (final ResolveInfo info : apps) {
            final String pkg = info.activityInfo.packageName;
            if (pkg.equals(getPackageName())) continue;
            final String activityName = info.activityInfo.name;
            View tile = makeTile(info.loadLabel(pm).toString(), info.loadIcon(pm),
                new Runnable() {
                    public void run() {
                        Intent launch = new Intent(Intent.ACTION_MAIN);
                        launch.addCategory(Intent.CATEGORY_LAUNCHER);
                        launch.setClassName(pkg, activityName);
                        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try { startActivity(launch); } catch (Exception ignored) { }
                    }
                }, 126);
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = dp(190);
            lp.height = dp(126);
            lp.setMargins(dp(7), dp(7), dp(7), dp(7));
            grid.addView(tile, lp);
            count++;
        }

        if (count == 0) {
            TextView empty = label("No launchable apps found", 18,
                Color.LTGRAY, false);
            grid.addView(empty);
        }

        scroll.addView(grid);
        root.addView(scroll, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

        LinearLayout footer = new LinearLayout(this);
        footer.setGravity(Gravity.CENTER_VERTICAL);
        footer.setPadding(0, dp(12), 0, 0);
        TextView hint = label("Use your remote to select an app", 12,
            Color.rgb(126, 146, 164), false);
        hint.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        footer.addView(hint, new LinearLayout.LayoutParams(0, dp(48), 1));

        View settings = makeTile("SETTINGS", null, new Runnable() {
            public void run() {
                try { startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS)); }
                catch (Exception ignored) { }
            }
        }, 48);
        settings.setPadding(dp(16), 0, dp(16), 0);
        footer.addView(settings, new LinearLayout.LayoutParams(dp(150), dp(48)));
        root.addView(footer);

        setContentView(root);
        if (grid.getChildCount() > 0) grid.getChildAt(0).requestFocus();
    }
}
