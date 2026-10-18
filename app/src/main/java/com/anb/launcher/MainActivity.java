package com.anb.launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ANB Launcher prototype.
 * Quick-control switches and sliders are demo-only and do not alter system settings.
 */
public class MainActivity extends Activity {
    private final int white = Color.rgb(248, 250, 252);
    private final int muted = Color.rgb(222, 229, 244);
    private final int glass = Color.argb(178, 22, 31, 54);
    private LinearLayout root;
    private GridLayout appGrid;
    private EditText search;
    private TextView appCount;
    private TextView statusTime, dateLabel, bigClock, calendarDay;
    private final Handler clockHandler = new Handler(Looper.getMainLooper());
    private final Runnable clockTick = new Runnable() {
        @Override public void run() {
            updateClockLabels();
            clockHandler.postDelayed(this, 30_000L);
        }
    };
    private final List<ResolveInfo> installedApps = new ArrayList<>();
    private boolean wifi = true, bluetooth = true, focus = false, flashlight = false;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(38, 61, 105));
        getWindow().setNavigationBarColor(Color.rgb(23, 35, 62));
        loadLaunchableApps();
        renderHome();
    }

    @Override protected void onResume() {
        super.onResume();
        if (root != null) {
            loadLaunchableApps();
            renderHome();
        }
        clockHandler.removeCallbacks(clockTick);
        clockHandler.post(clockTick);
    }

    @Override protected void onPause() {
        clockHandler.removeCallbacks(clockTick);
        super.onPause();
    }

    private void updateClockLabels() {
        Date now = new Date();
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(now);
        String date = new SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(now);
        String day = new SimpleDateFormat("d", Locale.getDefault()).format(now);
        if (statusTime != null) statusTime.setText(time);
        if (bigClock != null) bigClock.setText(time);
        if (dateLabel != null) dateLabel.setText(date);
        if (calendarDay != null) calendarDay.setText("TODAY\\n" + day + "\\nNo events");
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable rounded(int color, int radiusDp, int strokeColor) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radiusDp));
        if (strokeColor != 0) d.setStroke(dp(1), strokeColor);
        return d;
    }

    private TextView label(String text, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);
        if (bold) t.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        return t;
    }

    private LinearLayout vertical() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    private LinearLayout horizontal() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        return l;
    }

    private void pad(View v, int l, int t, int r, int b) {
        v.setPadding(dp(l), dp(t), dp(r), dp(b));
    }

    private void loadLaunchableApps() {
        installedApps.clear();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> results = pm.queryIntentActivities(intent, 0);
        if (results != null) {
            for (ResolveInfo info : results) {
                if (info.activityInfo != null &&
                        !getPackageName().equals(info.activityInfo.packageName)) {
                    installedApps.add(info);
                }
            }
        }
        Collections.sort(installedApps, (a, b) ->
                Collator.getInstance().compare(
                        String.valueOf(a.loadLabel(pm)),
                        String.valueOf(b.loadLabel(pm))));
    }

    private void renderHome() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        root = vertical();
        root.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{Color.rgb(24, 42, 78), Color.rgb(76, 100, 157),
                        Color.rgb(147, 112, 157), Color.rgb(207, 153, 155)}));
        pad(root, 18, 8, 18, 20);

        LinearLayout status = horizontal();
        statusTime = label("", 13, white, true);
        status.addView(statusTime, new LinearLayout.LayoutParams(0, dp(30), 1));
        TextView sys = label("●  ◔  ▰", 12, white, true);
        sys.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        status.addView(sys);
        root.addView(status);

        LinearLayout brandRow = horizontal();
        TextView brand = label("A N B   O S", 13, white, true);
        brandRow.addView(brand, new LinearLayout.LayoutParams(0, dp(44), 1));
        Button controls = smallButton("Quick controls");
        controls.setOnClickListener(v -> showControls());
        brandRow.addView(controls);
        root.addView(brandRow);

        dateLabel = label("", 13, muted, false);
        pad(dateLabel, 2, 18, 2, 0);
        root.addView(dateLabel);
        bigClock = label("", 48, white, false);
        bigClock.setLetterSpacing(-0.04f);
        root.addView(bigClock);

        LinearLayout widgets = horizontal();
        widgets.setGravity(Gravity.TOP);
        TextView weather = label("☀  24°\nWEATHER · DEMO\nClear skies", 14, white, true);
        weather.setLineSpacing(dp(4), 1f);
        pad(weather, 14, 12, 10, 12);
        weather.setBackground(rounded(glass, 22, Color.argb(80,255,255,255)));
        widgets.addView(weather, new LinearLayout.LayoutParams(0, dp(102), 1.15f));
        TextView calendar = label("TODAY\n" + new SimpleDateFormat("d", Locale.getDefault()).format(new Date()) + "\nNo events", 13, white, true);
        calendar.setLineSpacing(dp(3), 1f);
        pad(calendar, 14, 12, 10, 12);
        calendar.setBackground(rounded(glass, 22, Color.argb(80,255,255,255)));
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(0, dp(102), .85f);
        cp.leftMargin = dp(10);
        widgets.addView(calendar, cp);
        LinearLayout.LayoutParams wp = new LinearLayout.LayoutParams(-1, -2);
        wp.topMargin = dp(12);
        root.addView(widgets, wp);

        LinearLayout searchWrap = vertical();
        search = new EditText(this);
        search.setSingleLine(true);
        search.setTextSize(14);
        search.setTextColor(white);
        search.setHintTextColor(Color.rgb(210, 218, 237));
        search.setHint("Search installed apps");
        search.setCompoundDrawablePadding(dp(8));
        search.setBackground(rounded(Color.argb(115, 20, 30, 55), 18, Color.argb(100,255,255,255)));
        pad(search, 14, 4, 14, 4);
        searchWrap.addView(search, new LinearLayout.LayoutParams(-1, dp(48)));
        appCount = label("", 11, muted, false);
        pad(appCount, 2, 10, 2, 4);
        searchWrap.addView(appCount);
        LinearLayout.LayoutParams swp = new LinearLayout.LayoutParams(-1, -2);
        swp.topMargin = dp(14);
        root.addView(searchWrap, swp);

        appGrid = new GridLayout(this);
        appGrid.setColumnCount(calculateGridColumns());
        appGrid.setUseDefaultMargins(false);
        root.addView(appGrid, new LinearLayout.LayoutParams(-1, -2));
        search.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { populateApps(s.toString()); }
            @Override public void afterTextChanged(android.text.Editable e) {}
        });
        populateApps("");

        Button settings = smallButton("ANB Settings");
        settings.setOnClickListener(v -> showSettings());
        LinearLayout.LayoutParams stp = new LinearLayout.LayoutParams(-1, dp(44));
        stp.topMargin = dp(18);
        root.addView(settings, stp);

        scroll.addView(root);
        setContentView(scroll);
        updateClockLabels();
    }

    private void populateApps(String query) {
        if (appGrid == null) return;
        appGrid.removeAllViews();
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        int shown = 0;
        PackageManager pm = getPackageManager();
        for (ResolveInfo info : installedApps) {
            String title = String.valueOf(info.loadLabel(pm));
            if (!title.toLowerCase(Locale.ROOT).contains(q)) continue;
            appGrid.addView(appTile(info, title), appLayoutParams());
            shown++;
        }
        appCount.setText(shown + (shown == 1 ? " app" : " apps"));
        if (shown == 0) {
            TextView empty = label("No matching apps", 13, white, false);
            GridLayout.LayoutParams ep = new GridLayout.LayoutParams();
            ep.width = ViewGroup.LayoutParams.MATCH_PARENT;
            ep.height = dp(64);
            ep.columnSpec = GridLayout.spec(0, 4);
            appGrid.addView(empty, ep);
        }
    }

    private int calculateGridColumns() {
        // Aim for app tiles around 76–96 dp wide; clamp for very narrow/wide screens.
        int screenWidthDp = (int) (getResources().getDisplayMetrics().widthPixels /
                getResources().getDisplayMetrics().density);
        int usableWidthDp = Math.max(240, screenWidthDp - 36);
        return Math.max(3, Math.min(5, usableWidthDp / 82));
    }

    private GridLayout.LayoutParams appLayoutParams() {
        GridLayout.LayoutParams p = new GridLayout.LayoutParams();
        p.width = 0;
        p.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        p.setMargins(dp(2), dp(4), dp(2), dp(8));
        return p;
    }

    private View appTile(ResolveInfo info, String title) {
        LinearLayout tile = vertical();
        tile.setGravity(Gravity.CENTER);
        pad(tile, 2, 5, 2, 5);

        ImageView icon = new ImageView(this);
        Drawable drawable = info.loadIcon(getPackageManager());
        icon.setImageDrawable(drawable);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(dp(54), dp(54));
        tile.addView(icon, ip);

        TextView name = label(title, 10, white, false);
        name.setGravity(Gravity.CENTER);
        name.setMaxLines(2);
        name.setEllipsize(android.text.TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams np = new LinearLayout.LayoutParams(-1, dp(30));
        np.topMargin = dp(3);
        tile.addView(name, np);
        tile.setContentDescription(title + ". Double tap to open. Long press for app info.");
        tile.setFocusable(true);
        tile.setOnLongClickListener(v -> {
            try {
                Intent details = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                details.setData(Uri.parse("package:" + info.activityInfo.packageName));
                startActivity(details);
            } catch (Exception e) {
                Toast.makeText(this, "App info is unavailable for " + title, Toast.LENGTH_SHORT).show();
            }
            return true;
        });
        tile.setOnClickListener(v -> {
            try {
                Intent launch = new Intent(Intent.ACTION_MAIN);
                launch.addCategory(Intent.CATEGORY_LAUNCHER);
                launch.setComponent(new ComponentName(info.activityInfo.packageName, info.activityInfo.name));
                startActivity(launch);
            } catch (Exception e) {
                Toast.makeText(this, "Unable to open " + title, Toast.LENGTH_SHORT).show();
            }
        });
        return tile;
    }

    private Button smallButton(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(11);
        b.setTextColor(white);
        b.setAllCaps(false);
        b.setBackground(rounded(Color.argb(80, 20, 30, 55), 15, Color.argb(100,255,255,255)));
        return b;
    }

    private void showControls() {
        LinearLayout box = vertical();
        pad(box, 10, 6, 10, 4);
        TextView note = label("Prototype toggles only — they do not change device hardware.", 12, muted, false);
        box.addView(note);
        Button w = smallButton("Wi-Fi · " + (wifi ? "On" : "Off"));
        Button bt = smallButton("Bluetooth · " + (bluetooth ? "On" : "Off"));
        Button f = smallButton("Focus · " + (focus ? "On" : "Off"));
        Button fl = smallButton("Flashlight · " + (flashlight ? "On" : "Off"));
        box.addView(w); box.addView(bt); box.addView(f); box.addView(fl);
        w.setOnClickListener(v -> { wifi = !wifi; w.setText("Wi-Fi · " + (wifi ? "On" : "Off")); });
        bt.setOnClickListener(v -> { bluetooth = !bluetooth; bt.setText("Bluetooth · " + (bluetooth ? "On" : "Off")); });
        f.setOnClickListener(v -> { focus = !focus; f.setText("Focus · " + (focus ? "On" : "Off")); });
        fl.setOnClickListener(v -> { flashlight = !flashlight; fl.setText("Flashlight · " + (flashlight ? "On" : "Off")); });
        android.widget.SeekBar brightness = new android.widget.SeekBar(this); brightness.setMax(100); brightness.setProgress(78);
        android.widget.SeekBar volume = new android.widget.SeekBar(this); volume.setMax(100); volume.setProgress(55);
        box.addView(label("Brightness · demo", 12, muted, false)); box.addView(brightness);
        box.addView(label("Volume · demo", 12, muted, false)); box.addView(volume);
        new AlertDialog.Builder(this).setTitle("Quick controls").setView(box).setPositiveButton("Done", null).show();
    }

    private void showSettings() {
        String[] items = {"Set as default Home screen", "Appearance  ·  Dark demo", "Wallpaper  ·  Gradient", "Notifications  ·  Demo", "Privacy & security", "About ANB Launcher"};
        new AlertDialog.Builder(this).setTitle("ANB Settings")
                .setItems(items, (dialog, which) -> {
                    if (which == 0) {
                        try {
                            startActivity(new Intent(Settings.ACTION_HOME_SETTINGS));
                        } catch (Exception e) {
                            Toast.makeText(this, "Open Android Settings and choose Default apps → Home app.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, items[which] + " — prototype only", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("Close", null).show();
    }
}
