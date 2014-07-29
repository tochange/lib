package com.tochange.yang.lib.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;

import com.tochange.yang.lib.log;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class ApkInstaller {
    public static final class apk_install_ret {
        public static final int success = 0;
        public static final int fail = 1;
        public static final int cancel = 2;
        public static final int failed_no_device = 3; // 检测不到设备
        public static final int failed_already_exists = 4; // 程序已存在
        public static final int failed_invalid_apk = 5; // 无效的apk
        public static final int failed_invalid_uri = 6; // 无效的链接
        public static final int failed_insufficient_storage = 7; // 没有足够的存储空间
        public static final int failed_duplicate_package = 8; // 已存在同名程序
        public static final int failed_no_shared_user = 9; // 要求的共享用户不存在
        public static final int failed_update_incompatible = 10; // 版本不能共存
        public static final int failed_shared_user_incompatible = 11; // 需要共享的用户签名错误
        public static final int failed_missing_shared_library = 12; // 要求的共享库已丢失
        public static final int failed_replace_couldnt_delete = 13; // 要求的共享库无效
        public static final int failed_dexopt = 14; // dex优化验证失败
        public static final int failed_older_sdk = 15; // 系统版本过旧
        public static final int failed_conflicting_provider = 16; // 存在同名的内容提供者
        public static final int failed_newer_sdk = 17; // 系统版本过新
        public static final int failed_test_only = 18; // 调用者不被允许测试的测试程序
        public static final int failed_cpu_abi_incompatible = 19; // 包含的本机代码不兼容
        public static final int failed_missing_feature = 20; // 使用了一个无效的特性
        public static final int failed_container_error = 21; // SD卡访问失败
        public static final int failed_invalid_install_location = 22; // 无效的安装路径
        public static final int failed_media_unavailable = 23; // SD卡不存在
        public static final int failed_internal_error = 24; // 系统问题导致安装失败
        public static final int failed_default = 25; // 未知错误
        
    }

	@SuppressWarnings("finally")
	public static int install(String apkPath) {
		String cmd = "adb install -r " + apkPath + "\n";
		Process proc = null;
		try {
			proc = Runtime.getRuntime().exec("su");
			DataOutputStream out = new DataOutputStream(proc.getOutputStream());
			out.writeBytes(cmd);
			out.writeBytes("exit \n");
			out.flush();
			out.close();

			DataInputStream es = new DataInputStream(proc.getErrorStream());
			String esStr = null;
			while ((esStr = es.readLine()) != null && esStr.isEmpty() == false) {
				Log.v(mTag, "error stream: " + esStr);
				if (esStr.contains("device not found")) {
					es.close();
					return apk_install_ret.failed_no_device;
				} else if (esStr.contains("error")) {
					es.close();
					return  apk_install_ret.failed_default;
				}
			}
			es.close();
			
			DataInputStream in = new DataInputStream(proc.getInputStream());
			String inStr = null;
			while ((inStr = in.readLine()) != null && inStr.isEmpty() == false) {
				Log.v(mTag, "input stream: " + inStr);
				if (inStr.contains("Success")) {
					in.close();
					return  apk_install_ret.success;
				} else if (inStr.contains("INSTALL_FAILED_ALREADY_EXISTS")) {
					in.close();
					return  apk_install_ret.failed_already_exists;
				} else if (inStr.contains("INSTALL_FAILED_INVALID_APK")) {
					in.close();
					return  apk_install_ret.failed_invalid_apk;
				} else if (inStr.contains("INSTALL_FAILED_INVALID_URI")) {
					in.close();
					return  apk_install_ret.failed_invalid_uri;
				} else if (inStr.contains("INSTALL_FAILED_INSUFFICIENT_STORAGE")) {
					in.close();
					return  apk_install_ret.failed_insufficient_storage;
				} else if (inStr.contains("INSTALL_FAILED_DUPLICATE_PACKAGE")) {
					in.close();
					return  apk_install_ret.failed_duplicate_package;
				} else if (inStr.contains("INSTALL_FAILED_NO_SHARED_USER")) {
					in.close();
					return  apk_install_ret.failed_no_shared_user;
				} else if (inStr.contains("INSTALL_FAILED_UPDATE_INCOMPATIBLE")) {
					in.close();
					return  apk_install_ret.failed_update_incompatible;
				} else if (inStr.contains("INSTALL_FAILED_SHARED_USER_INCOMPATIBLE")) {
					in.close();
					return  apk_install_ret.failed_shared_user_incompatible;
				} else if (inStr.contains("INSTALL_FAILED_MISSING_SHARED_LIBRARY")) {
					in.close();
					return  apk_install_ret.failed_missing_shared_library;
				} else if (inStr.contains("INSTALL_FAILED_REPLACE_COULDNT_DELETE")) {
					in.close();
					return  apk_install_ret.failed_replace_couldnt_delete;
				} else if (inStr.contains("INSTALL_FAILED_DEXOPT")) {
					in.close();
					return  apk_install_ret.failed_dexopt;
				} else if (inStr.contains("INSTALL_FAILED_OLDER_SDK")) {
					in.close();
					return  apk_install_ret.failed_older_sdk;
				} else if (inStr.contains("INSTALL_FAILED_CONFLICTING_PROVIDER")) {
					in.close();
					return  apk_install_ret.failed_conflicting_provider;
				} else if (inStr.contains("INSTALL_FAILED_NEWER_SDK")) {
					in.close();
					return  apk_install_ret.failed_newer_sdk;
				} else if (inStr.contains("INSTALL_FAILED_TEST_ONLY")) {
					in.close();
					return  apk_install_ret.failed_test_only;
				} else if (inStr.contains("INSTALL_FAILED_CPU_ABI_INCOMPATIBLE")) {
					in.close();
					return  apk_install_ret.failed_cpu_abi_incompatible;
				} else if (inStr.contains("INSTALL_FAILED_MISSING_FEATURE")) {
					in.close();
					return  apk_install_ret.failed_missing_feature;
				} else if (inStr.contains("INSTALL_FAILED_CONTAINER_ERROR")) {
					in.close();
					return  apk_install_ret.failed_container_error;
				} else if (inStr.contains("INSTALL_FAILED_INVALID_INSTALL_LOCATION")) {
					in.close();
					return  apk_install_ret.failed_invalid_install_location;
				} else if (inStr.contains("INSTALL_FAILED_MEDIA_UNAVAILABLE")) {
					in.close();
					return  apk_install_ret.failed_media_unavailable;
				} else if (inStr.contains("INSTALL_FAILED_INTERNAL_ERROR")) {
					in.close();
					return  apk_install_ret.failed_internal_error;
				} else if (inStr.contains("DEFAULT")) {
					in.close();
					return  apk_install_ret.failed_default;
				}
			}
			in.close();
			proc.destroy();	
			return  apk_install_ret.failed_default;
		} catch (Exception e) {
			Log.v("roottools", e.toString());
			return  apk_install_ret.failed_default;
		}
	}
	
	@SuppressWarnings("finally")
	public static boolean startApk(String packegeName, String mainAcName) {
		String cmd = "adb shell am start -n " + packegeName + "/" + mainAcName + " \n";
		
		boolean ret = true;
		try {
			Process proc = Runtime.getRuntime().exec("su");
			DataOutputStream out = new DataOutputStream(proc.getOutputStream());
			out.writeBytes(cmd);
			out.writeBytes("exit \n");
			out.flush();
			out.close();
			
			DataInputStream in = new DataInputStream(proc.getInputStream());
			String inStr = null;
			while ((inStr = in.readLine()) != null && inStr.isEmpty() == false) {
				Log.v("roottools", "input stream: " + inStr);
				if (inStr.contains("Error")) {
					ret = false;
					break;
				}
			}
			in.close();			
			proc.waitFor();			
			proc.destroy();
		} catch (Exception e) {
			e.printStackTrace();
			Log.v("roottools", e.toString());
			ret = false;
		} finally {
			return ret;
		}

	}
	private static final String mTag = "roottools";
	
    public static void uninstallApp(Context cc, String packageName)
    {
        Uri packageURI = Uri.parse("package:" + packageName);
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        cc.startActivity(uninstallIntent);
        // setIntentAndFinish(true, true);
    }

    public static void forceStopApp(Context cc, String packageName)
    {
        ActivityManager am = (ActivityManager) cc
                .getSystemService(Context.ACTIVITY_SERVICE);
        // am.forceStopPackage(packageName);

        // Class c =
        // Class.forName("com.android.settings.applications.ApplicationsState");
        // Method m = c.getDeclaredMethod("getInstance", Application.class);

    }
    public static void openApp(Context c, String packageName)
    {
        Intent i = c.getPackageManager().getLaunchIntentForPackage(packageName);
        if (i != null)
            c.startActivity(i);
        else
            Toast.makeText(c, "cann't launcher this app", Toast.LENGTH_SHORT)
                    .show();

    }

    public static void openAppTroublesome(Context c, String packageName)

    {
        PackageInfo pi;
        try
        {
            pi = c.getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(pi.packageName);
            List<ResolveInfo> apps = c.getPackageManager()
                    .queryIntentActivities(resolveIntent, 0);

            ResolveInfo ri = apps.iterator().next();
            if (ri != null)
            {
                String packageName1 = ri.activityInfo.packageName;
                String className = ri.activityInfo.name;

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);

                ComponentName cn = new ComponentName(packageName1, className);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(cn);
                c.startActivity(intent);
            }
        }
        catch (NameNotFoundException e)
        {
            log.e("open app error,maybe cann't find the launcher activity");
            e.printStackTrace();
        }
    }
}
