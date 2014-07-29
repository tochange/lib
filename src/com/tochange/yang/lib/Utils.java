package com.tochange.yang.lib;

import static android.view.Gravity.BOTTOM;
import static com.tochange.yang.lib.toast.AppMsg.LENGTH_SHORT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.appmsg.R;
import com.tochange.yang.lib.toast.AppMsg;
import com.tochange.yang.lib.ui.FZProgressBar;
import com.tochange.yang.lib.ui.FZProgressBar.Mode;

public class Utils
{
    static Context mContext;

    public static void setContext(Context c)
    {
        mContext = c;
    }

    public static class FileInfos
    {
        Calendar c;

        public File file;

        public FileInfos(Calendar c, File file)
        {
            this.c = c;
            this.file = file;
        }
    }

    static class TimeComparator implements Comparator<FileInfos>
    {
        public int compare(FileInfos o1, FileInfos o2)
        {
            return (o1.c.compareTo(o2.c));
        }
    }

    /**
     * 自定义正则格式匹配，返回是否匹配该正则表达式
     */
    public static boolean matchPattern(String str, String pattStr)
    {
        boolean res = false;
        Pattern pattern = Pattern.compile(pattStr, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches())
        {
            res = true;
        }
        return res;
    }

    /**
     * delete old file，only left 10
     * 
     * @param path
     * @param appName
     * @return
     */
    public static List<FileInfos> getDeletedFiles(String path, String appName)
    {
        ArrayList<FileInfos> ret = new ArrayList<FileInfos>();
        File f = new File(path);
        if (!f.isDirectory())
            log.e(path + " not a directory!");
        else
        {
            File[] fileList = f.listFiles();
            getFileInfo(fileList, ret, appName);
        }
        Collections.sort(ret, new TimeComparator());

        // for (FileInfos ff : ret)
        // log.e(ff.file.getAbsolutePath());
        if (ret.size() >= 10)
            return ret.subList(0, ret.size() - 10);
        else
            return null;
    }

    public static void vibrate(Context c)
    {

        Vibrator mVibrator = (Vibrator) c.getApplicationContext()
                .getSystemService(Service.VIBRATOR_SERVICE);
        mVibrator.vibrate(new long[] { 50, 50, 0, 0 }, -1);
    }

    public static void getFileInfo(File[] fileList, ArrayList<FileInfos> list,
            String appName)
    {
        int length = fileList.length;
        for (int i = 0; i < length; i++)
        {
            File tmp = fileList[i];
            String path = tmp.getAbsolutePath();
            if (tmp.isFile() && path.contains(appName)
                    && !path.endsWith(appName))
            {
                path = path.replace("_", "-").replace(".", "-");
                String[] array = path.split("-");
                Calendar c = Calendar.getInstance();
                c.set(Integer.parseInt(array[1]), Integer.parseInt(array[2]),
                        Integer.parseInt(array[3]), Integer.parseInt(array[4]),
                        Integer.parseInt(array[5]), Integer.parseInt(array[6]));
                list.add(new FileInfos(c, tmp));
            }
            else if (tmp.isDirectory())
            {
                getFileInfo(tmp.listFiles(), list, appName);
            }
        }
    }

    public static void sleep(int millisecond)
    {
        try
        {
            Thread.sleep(millisecond);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 判断APP是在后台执行或是在前台执行
     */
    public static boolean isAppOnForeground(Context context)
    {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
        {
            return false;
        }
        for (RunningAppProcessInfo appProcess : appProcesses)
        {
            if (appProcess.processName.equals(context.getPackageName())
                    && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
            {
                return true;
            }
        }
        return false;
    }

    public static Intent getViewIntent(File file)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String extension = android.webkit.MimeTypeMap
                .getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String minType = android.webkit.MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(extension);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, minType);
        return intent;
    }

    public static Intent getShareIntent(File file)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String extension = android.webkit.MimeTypeMap
                .getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String minType = android.webkit.MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(extension);
        Uri uri = Uri.fromFile(file);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType(minType);
        return intent;
    }

    public static ComponentName getTopActivity(Activity context)
    {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        if (runningTaskInfos != null)
            return runningTaskInfos.get(0).topActivity;
        else
            return null;
    }

    public static void Toast(Context c, String msg)
    {
        if (!(c instanceof Activity))
        {
            // AppMsg only accept activity context
            Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
            return;
        }
        AppMsg appMsg = AppMsg.makeText((Activity) c, msg, R.color.button_col,
                new AppMsg.Style(LENGTH_SHORT, R.color.toast_col));
        appMsg.setLayoutGravity(BOTTOM);

        appMsg.setAnimation(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
        appMsg.show();
    }

    public static FZProgressBar setProgressBar(FZProgressBar b, int color)
    {
        b.animation_config(1, 20);
        int[] colors1 = { color, Color.TRANSPARENT };
        b.bar_config(10, 0, 10, Color.TRANSPARENT, colors1);
        return b;
    }

    public static FZProgressBar setProgressBar(FZProgressBar b, int colorStart,
            int colorEnd)
    {
        b.animation_config(1, 20);
        int[] colors1 = { colorStart, colorEnd };
        b.bar_config(10, 0, 10, Color.TRANSPARENT, colors1);
        return b;
    }

    public static void showFZProgressBar(FZProgressBar fZProgressBar)
    {
        if (fZProgressBar.getVisibility() == View.GONE)
        {
            fZProgressBar.animation_start(Mode.INDETERMINATE);
            fZProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public static void closeFZProgressBar(FZProgressBar fZProgressBar)
    {
        if (!(fZProgressBar.getVisibility() == View.GONE))
        {
            fZProgressBar.setVisibility(View.GONE);
            fZProgressBar.animation_stop();
        }

    }

    public static boolean serviceIsRunning(Context mContext, String className)
    {

        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(30);
        if ((serviceList.size() <= 0))
            return false;
        int size = serviceList.size();
        for (int i = 0; i < size; i++)
            if (serviceList.get(i).service.getClassName().equals(className) == true)
                return true;
        return false;
    }

    public static String getUsedPercentValue(Context context)
    {
        String dir = "/proc/meminfo";
        try
        {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine
                    .indexOf("MemTotal:"));
            br.close();
            long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll(
                    "\\D+", ""));
            long availableSize = getAvailableMemory(context) / 1024;
            int percent = (int) ((totalMemorySize - availableSize)
                    / (float) totalMemorySize * 100);
            return percent + "%";
        }
        catch (IOException e)
        {
            log.e("getUsedPercentValue error");
            e.printStackTrace();
        }
        return "error";
    }

    private static long getAvailableMemory(Context context)
    {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        getActivityManager(context).getMemoryInfo(mi);
        return mi.availMem;
    }

    static ActivityManager mActivityManager;

    private static ActivityManager getActivityManager(Context context)
    {
        if (mActivityManager == null)
        {
            mActivityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
        }
        return mActivityManager;
    }

    public static int getThisProcessMemeryInfoInKbit(Context context)
    {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        int pid = android.os.Process.myPid();
        android.os.Debug.MemoryInfo[] memoryInfoArray = activityManager
                .getProcessMemoryInfo(new int[] { pid });
        return memoryInfoArray[0].getTotalPrivateDirty();
    }
   
    public void setUpFloatWindow(String message)
    {
        LinearLayout tv = (LinearLayout) LayoutInflater.from(
                mContext.getApplicationContext()).inflate(R.layout.app_msg,
                null);
        TextView t = (TextView) tv.findViewById(android.R.id.message);
        t.setText(message);
        t.setTextColor(Color.RED);
        t.setTextSize(25);
        WindowManager wm = (WindowManager) mContext.getSystemService("window");
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.alpha = 1.0f; // lower than one makes it more transparent
        lp.dimAmount = 0f; // set it higher if you want to dim behind the window
        lp.gravity = Gravity.CENTER;
        lp.type = LayoutParams.TYPE_PRIORITY_PHONE;
        lp.format = PixelFormat.RGBA_8888;// transparent
        lp.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                | LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.width = LayoutParams.FILL_PARENT;
        lp.height = LayoutParams.WRAP_CONTENT;// -2
        wm.addView(tv, lp);

        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 5, 1, 5,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scaleAnimation.setDuration(5000);
        t.startAnimation(scaleAnimation);
    }

    class Monitor implements Runnable
    {
        List<String> backchild;

        private volatile boolean go = false;

        public Monitor(List<String> backchild)
        {
            this.backchild = backchild;
        }

        public synchronized void gotMessage() throws InterruptedException
        {
            go = true;
            notify();
        }

        public synchronized void watching() throws InterruptedException
        {
            while (go == false)
                wait();
            // beginUpdate(backchild);
        }

        public void run()
        {
            try
            {
                watching();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    class RefreshTask extends TimerTask
    {
        Handler handler;

        @Override
        public void run()
        {
            handler.post(new Runnable() {
                @Override
                public void run()
                {
                    Toast.makeText(mContext,
                            Utils.getUsedPercentValue(mContext),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // timer = new Timer();
    // timer.scheduleAtFixedRate(new RefreshTask(), 0, 500);
    // timer.cancel();

    class SleepTask extends AsyncTask<String, Integer, String>
    {

        @Override
        protected String doInBackground(String... arg0)
        {
            Utils.sleep(Integer.parseInt(arg0[0]));
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
        }
    }
    // new SleepTask().execute(1 + "");
}