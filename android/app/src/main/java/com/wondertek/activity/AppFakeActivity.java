package com.wondertek.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.bestv.mishitong.flamingo.R;
import com.wondertek.mishitong.ott.monitor.MonitorManager;
import com.wondertek.mishitong.ott.plugins.SystemInfoPlugin;
import com.wondertek.mishitong.ott.utils.Util;

import io.flutter.app.FlutterActivity;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class AppFakeActivity extends FlutterActivity {

    private static boolean interceptVolumeEvent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.Trace("AppFakeActivity onCreate");
        getWindow().getDecorView().setBackgroundResource(R.mipmap.bg);

        Intent intent = getIntent();
        Util.Trace("intent: " + intent);
        SystemInfoPlugin.parseIntent(intent, new SystemInfoPlugin.NotifyVolumeChangedListener() {
            @Override
            public void showSystemVolume(boolean show) {
                interceptVolumeEvent = !show;
            }
        });

        GeneratedPluginRegistrant.registerWith(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.Trace("AppFakeActivity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Util.Trace("AppFakeActivity onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Util.Trace("AppFakeActivity onDestroy");
        MonitorManager.getInstance().unregisterMonitor(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return interceptVolumeEvent;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return interceptVolumeEvent;
        }
        return super.onKeyUp(keyCode, event);
    }
}
