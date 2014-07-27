package com.tochange.yang.lib;

public class log
{

    final static boolean DEBUG = true;

    protected static final String TAG = "yangxj";

    private log()
    {
    }

    /**
     * Send a VERBOSE log message.
     * 
     * @param msg The message you would like logged.
     */
    public static void v(String msg)
    {
        if (DEBUG)
            android.util.Log.v(TAG, buildMessage(msg));
    }

    /**
     * Send a VERBOSE log message and log the exception.
     * 
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void v(String msg, Throwable thr)
    {
        if (DEBUG)
            android.util.Log.v(TAG, buildMessage(msg), thr);
    }

    /**
     * Send a DEBUG log message.
     * 
     * @param msg
     */
    public static void d(String msg)
    {
        if (DEBUG)
            android.util.Log.d(TAG, buildMessage(msg));
    }

    /**
     * Send a DEBUG log message and log the exception.
     * 
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void d(String msg, Throwable thr)
    {
        if (DEBUG)
            android.util.Log.d(TAG, buildMessage(msg), thr);
    }

    /**
     * Send an INFO log message.
     * 
     * @param msg The message you would like logged.
     */
    public static void i(String msg)
    {
        if (DEBUG)
            android.util.Log.i(TAG, buildMessage(msg));
    }

    /**
     * Send a INFO log message and log the exception.
     * 
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void i(String msg, Throwable thr)
    {
        if (DEBUG)
            android.util.Log.i(TAG, buildMessage(msg), thr);
    }

    /**
     * Send an ERROR log message.
     * 
     * @param msg The message you would like logged.
     */
    public static void e(String msg)
    {
        if (DEBUG)
            android.util.Log.e(TAG, buildMessage(msg));
    }

    /**
     * Send a WARN log message
     * 
     * @param msg The message you would like logged.
     */
    public static void w(String msg)
    {
        if (DEBUG)
            android.util.Log.w(TAG, buildMessage(msg));
    }

    /**
     * Send a WARN log message and log the exception.
     * 
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void w(String msg, Throwable thr)
    {
        if (DEBUG)
            android.util.Log.w(TAG, buildMessage(msg), thr);
    }

    /**
     * Send an empty WARN log message and log the exception.
     * 
     * @param thr An exception to log
     */
    public static void w(Throwable thr)
    {
        if (DEBUG)
            android.util.Log.w(TAG, buildMessage(""), thr);
    }

    /**
     * Send an ERROR log message and log the exception.
     * 
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void e(String msg, Throwable thr)
    {
        if (DEBUG)
            android.util.Log.e(TAG, buildMessage(msg), thr);
    }

    /**
     * Building Message
     * 
     * @param msg The message you would like logged.
     * @return Message String
     */
    protected static String buildMessage(String msg)
    {
        StackTraceElement caller = new Throwable().fillInStackTrace()
                .getStackTrace()[2];

        return new StringBuilder().append(caller.getClassName()).append(".")
                .append(caller.getMethodName()).append("(): ").append(msg)
                .toString();

//        return new StringBuilder()
//                .append(caller.getLineNumber()).append(" ").append(msg)
//                .toString();
    }

    // Log.d(TAG,new Exception().getStackTrace()[0].getMethodName()); //鍑芥暟鍚�    // Log.d(TAG, Thread.currentThread().getStackTrace()[2].getMethodName());
    // //鍑芥暟鍚�    // Log.d(TAG, ""+Thread.currentThread().getStackTrace()[2].getLineNumber());
    // //琛屽彿
    // Log.d(TAG, Thread.currentThread().getStackTrace()[2].getFileName());
    // //鏂囦欢鍚�    //
    // Log.d(TAG, "["+Thread.currentThread().getStackTrace()[2].getFileName()
    // +","+Thread.currentThread().getStackTrace()[2].getLineNumber()+"]");
    // //鏂囦欢鍚�琛屽彿

}
