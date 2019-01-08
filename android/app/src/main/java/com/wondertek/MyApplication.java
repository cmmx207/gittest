package com.wondertek;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.migu.unionsdk.api.InitMonkeySdk;
import com.wondertek.mishitong.ott.utils.SPUtil;
import com.wondertek.mishitong.ott.utils.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import io.flutter.app.FlutterApplication;

public class MyApplication extends FlutterApplication {

    private static String appPath = null;

    @Override
    public void onCreate() {
        super.onCreate();
		Log.d("cjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj")
        Log.d("WriteLogs", "MyApplication onCreate");
        appPath = getAppAbsPath();
        Util.Trace("appPath is " + appPath);
        unzipVenus();
        initSDK();
    }

    private String getAppAbsPath() {
        if (TextUtils.isEmpty(appPath)) {
            PackageManager pm = getPackageManager();
            PackageInfo info;
            try {
                info = pm.getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_ACTIVITIES);
                if (info != null) {
                    ApplicationInfo appInfo = info.applicationInfo;
                    appPath = appInfo.dataDir.concat("/");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return appPath;
    }

    private void unzipVenus() {
        boolean updateFlag = false;
        int oldVerCode = SPUtil.getInstance(this).getValue("version", 0);
        int newVerCode = getVerCode();
        if (newVerCode != oldVerCode) {
            updateFlag = true;
        }
        Util.Trace("updateFlag = " + updateFlag);
        if (updateFlag) {
            unzip("venus.zip");
            SPUtil.getInstance(this).setValue("version", newVerCode);
            String channelId = Util.loadFileToString(appPath.concat("channelid.ini"), 0);
            SPUtil.getInstance(this).setValue("channelId", channelId);
        }
    }

    private void unzip(String s) {
        try {
            InputStream is = getAssets().open(s);
            ZipInputStream in = new ZipInputStream(is);
            ZipEntry file = in.getNextEntry();

            byte[] c = new byte[1024];

            int len;

            while (file != null) {
                int i = file.getName().lastIndexOf('/');
                if (i != -1) {
                    File dirs = new File(appPath + File.separator
                            + file.getName().substring(0, i));
                    if (!dirs.exists()) {
                        dirs.mkdirs();
                    }
                }

                if (file.isDirectory()) {
                    File dirs = new File(file.getName());
                    if (!dirs.exists()) {
                        dirs.mkdir();
                    }
                } else {
                    String strAbsPath = appPath + File.separator;
                    File f = new File(strAbsPath + file.getName());
                    FileOutputStream out;
                    String parent = f.getParent();
                    if (!parent.contains("saved_file")) {
                        f.delete();
                    }
                    if (!f.exists()) {
                        out = new FileOutputStream(strAbsPath + file.getName());
                    } else {
                        file = in.getNextEntry();
                        continue;
                    }
                    while ((len = in.read(c, 0, c.length)) != -1) {
                        out.write(c, 0, len);
                    }
                    out.close();
                }
                file = in.getNextEntry();
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getVerCode() {
        int verCode = -1;
        try {
            String packageName = getPackageName().replace('/', '.');
            verCode = getPackageManager().getPackageInfo(
                    packageName, 0).versionCode;
        } catch (Exception e) {
            Util.Trace(e.getMessage());
        }
        return verCode;
    }

    private void initSDK() {
        Log.d("WriteLogs", "before initSDK");
        boolean result = InitMonkeySdk.loadInit(this);
        Util.Trace("init result: " + result);
        result = InitMonkeySdk.loadShell(this);
        Util.Trace("shell result: " + result);
        result = InitMonkeySdk.loadAmber(this);
        Util.Trace("amber result: " + result);
    }
}
