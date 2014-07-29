package com.tochange.yang.lib.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import com.tochange.yang.lib.log;

import android.content.Context;
/**
 * 要执行的文件放在项目中的assets文件夹
 * 如若文件名是shell，则executeCmdWithRoot(this, "shell", 5000);
 * @author yangxj
 *
 */
public class ExecuteShellCommand
{
    private static List<String> mCommandList = new ArrayList<String>();

    private static int mExitValue;
    
    
    public static int execByRoot(String cmd)
    {

        int ret = 0;
        try
        {
            Process proc = Runtime.getRuntime().exec("su");
            DataOutputStream out = new DataOutputStream(proc.getOutputStream());
            out.writeBytes(cmd + " \n");
            out.writeBytes("exit \n");
            out.flush();
            out.close();
            proc.waitFor();
            ret = proc.exitValue();
            // proc.destroy();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return ret;
        }
    }

    /**
     * 
     * @Title readCommandFromAssets
     * @Description 从assets中读取相应命令内容，赋值给mCommandList
     * @param context 应用上下文
     * @param assetsFileName assets文件名
     * @return 是否成功读取到命令的标识
     */
    private static boolean readCommandFromAssets(Context context,
            String assetsFileName)
    {
        InputStream is = null;
        String result = "";
        mCommandList.clear();
        try
        {
            is = context.getAssets().open(assetsFileName);
            int length = is.available();
            byte[] buffer = new byte[length + 1];
            is.read(buffer, 1, length);
            result = EncodingUtils.getString(buffer, "UTF-8").trim();
            String[] arr = result.split("[\\n]");
            for (int i = 0; i < arr.length; i++)
            {
                mCommandList.add(arr[i] + "\n");
                log.e("mCommandList add:" + arr[i]);
            }
            is.close();
            if (mCommandList.size() > 0)
            {
                return true;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            log.e(e.toString());
        }
        return false;
    }

    /**
     * 
     * @Title executeCmdWithRoot
     * @Description 使用Root权限执行shell命令
     * @param context 应用上下文
     * @param assetsFileName assets文件名
     * @param timeout 执行超时时间(ms)
     * @return 操作是否出现异常的标识
     * @throws
     */
    public static int executeCmdWithRoot(Context context,
            String assetsFileName, long timeout)
    {
        int res = -1;
        if (readCommandFromAssets(context, assetsFileName))
        {
            try
            {
                mExitValue = -1;

                final Process p = Runtime.getRuntime().exec("su");

                DataOutputStream os = new DataOutputStream(p.getOutputStream());
                for (int i = 0; i < mCommandList.size(); i++)
                {
                    os.writeBytes(mCommandList.get(i));
                }
                os.flush();
                os.close();

                Thread thread = new Thread(new Runnable() {
                    public void run()
                    {
                        try
                        {
                            p.waitFor();

                            mExitValue = p.exitValue();
                            log.e("p.exitValue()=" + p.exitValue());
                            if (p.exitValue() != 0)
                            {
                                // 在指定的seconds之内子进程没有结束,就强制结束子进程。
                                p.destroy();
                            }
                            synchronized (mCommandList)
                            {
                                mCommandList.notify();
                            }
                        }
                        catch (InterruptedException e)
                        {
                            log.e("InterruptedException occur");
                        }
                    }
                });
                thread.start();
                synchronized (mCommandList)
                {
                    try
                    {
                        mCommandList.wait(timeout);
                    }
                    catch (Exception e)
                    {

                    }
                    if (thread.isAlive())
                    {
                        log.e("thread.isAlive, interrupt thread");
                        thread.interrupt();
                    }
                }
                res = mExitValue;
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                log.e("Exception str=" + e.toString());
                e.printStackTrace();
            }
        }
        return res;
    }
}
