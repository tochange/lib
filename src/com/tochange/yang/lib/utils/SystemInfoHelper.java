package com.tochange.yang.lib.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;

import com.tochange.yang.lib.log;

import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class SystemInfoHelper
{

    private final static String TAG = "SystemInfoHelper";

    public static void showHardwareInfoLog(Context context)
    {
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics m = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(m);
        float d = m.density;
        int dd = m.densityDpi;
        log.e("density:" + d + ";densityDpi:" + dd + ";screenSize:"
                + m.widthPixels + "*" + m.heightPixels + ";MANUFACTURER:"
                + Build.MANUFACTURER + ";BRAND:" + Build.BRAND + ";MODEL:"
                + Build.MODEL + ";RELEASE:" + Build.VERSION.RELEASE + ";SDK:"
                + Build.VERSION.SDK);
    }
    
    /**
     * 获取CPU序列号
     * 
     * @return CPU序列号(16位) 读取失败为"0000000000000000"
     */
    public static String getCPUSerial()
    {
        String str = "", strCPU = "", cpuAddress = "0000000000000000";
        try
        {
            // 读取CPU信息
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            // 查找CPU序列号
            for (int i = 1; i < 100; i++)
            {
                str = input.readLine();
                if (str != null)
                {
                    // 查找到序列号所在行
                    if (str.indexOf("Serial") > -1)
                    {
                        // 提取序列号
                        strCPU = str.substring(str.indexOf(":") + 1,
                                str.length());
                        cpuAddress = strCPU.trim();
                        break;
                    }
                }
                else
                    break;
            }
        }
        catch (IOException ex)
        {
            log.e(ex.toString());
        }
        return cpuAddress;
    }

    /**
     * 获取设备IMEI号
     * 
     * @param context 应用程序上下文对象
     * @return IMEI 读取失败为"000000000000000"
     */
    public static String getDeviceImei(Context context)
    {
        String platform = "";
        try
        {
            Class<?> c2 = Class.forName("android.os.SystemProperties");
            Method get2 = c2.getMethod("get", String.class, String.class);
            platform = (String) (get2
                    .invoke(c2, "ro.board.platform", "unknown"));
            Log.d(TAG, "getDeviceImei platform:" + platform);
            Build bd = new Build();
            String model = bd.MODEL;
        }
        catch (Exception ignored)
        {
            Log.d(TAG,
                    "get ro.serialno from android.os.SystemProperties exception,"
                            + ignored.toString());
            return "000000000000000";
        }
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        if ((imei == null) || (imei.length() == 0))
            imei = "000000000000000";
        return imei;
    }

    /**
     * 获取Android唯一标识号
     * 
     * @param context 应用程序上下文对象
     * @return Android唯一标识号 恢复出厂值后(第一次启动系统)，androidid会改变。
     */
    public static String getAndroidId(Context context)
    {
        String android_id = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);
        return android_id;
    }

    /**
     * 获取ro.serialno，该唯一标识仅对Android2.3及以上版本有效，如果取不到，根据不同机型做特殊处理
     * 
     * @return Android设备唯一标识号
     */
    public static String getDeviceId()
    {
        String deviceId = "";
        try
        {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            deviceId = (String) (get.invoke(c, "ro.serialno", "unknown"));
            if (deviceId.equals("unknown"))
            {
                // RK29
                try
                {
                    Class<?> c2 = Class.forName("android.os.SystemProperties");
                    Method get2 = c2.getMethod("get", String.class,
                            String.class);
                    String platform = (String) (get2.invoke(c2,
                            "ro.board.platform", "unknown"));
                    Log.d(TAG, "getDeviceId platform=" + platform);
                    Build bd = new Build();
                    String model = bd.MODEL;
                    deviceId = (String) (get.invoke(c, "ro.aceruuid",
                            "aceruuid_unknown"));
                }
                catch (Exception ignored)
                {
                    Log.d(TAG,
                            "get ro.serialno from android.os.SystemProperties exception,"
                                    + ignored.toString());
                    deviceId = "";
                }
            }
        }
        catch (Exception ignored)
        {
            Log.d(TAG,
                    "get ro.serialno from android.os.SystemProperties exception,"
                            + ignored.toString());
            deviceId = "";
        }
        return deviceId;
    }
}
