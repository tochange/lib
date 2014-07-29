package com.tochange.yang.lib.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiUtils
{
    public static final int WIFICIPHER_WEP = 0;

    public static final int WIFICIPHER_WPA = 1;

    public static final int WIFICIPHER_NOPASS = 2;

    public static final int WIFICIPHER_INVALID = 3;

    private final static String TAG = "WifiUtils";

    /**
     * 
     * @Title connectAccessPoint
     * @Description TODO 供外部调用，连接指定AccessPoint
     * @param wifiManager
     * @param ssid AP名称
     * @param pwd 密码
     * @param cipherType 加密方式0、WEP; 1、WPA; 2、不加密; 3、不可用
     * @return true 连接成功; false 连接失败
     * @throws
     */
    public static boolean connectAccessPoint(WifiManager wifiManager,
            String ssid, String pwd, int cipherType)
    {
        if (wifiManager == null || ssid == null)
        {
            return false;
        }
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";
        WifiConfiguration tmpConfig = isExsits(wifiManager, ssid);
        if (tmpConfig != null)
        {
            boolean flag = wifiManager.disableNetwork(tmpConfig.networkId);
            Log.d(TAG, "removeNetwork result=" + flag + " tmp config ssid:"
                    + tmpConfig.SSID);
        }
        if (cipherType == WIFICIPHER_NOPASS)
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        else if (cipherType == WIFICIPHER_WEP)
        {
            config.wepKeys[0] = "\"" + pwd + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        else if (cipherType == WIFICIPHER_WPA)
        {
            config.preSharedKey = "\"" + pwd + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        else
        {
            return false;
        }
        int networkId = wifiManager.addNetwork(config);
        boolean result = wifiManager.enableNetwork(networkId, true);
        Log.d(TAG, "connectToHostAP networkId==" + networkId + " result="
                + result);
        return result;
    }

    private static WifiConfiguration isExsits(WifiManager wifiManager,
            String ssid)
    {
        List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
        if (configs != null)
        {
            for (WifiConfiguration config : configs)
            {
                if (config.SSID.equals("\"" + ssid + "\""))
                {
                    return config;
                }
            }
        }
        return null;
    }

    /**
     * 
     * @Title setWifiApEnabled
     * @Description TODO 设置Wifi Ap模式可用
     * @param wifiManager
     * @param ssid
     * @param sharedKey
     * @param enable
     * @return
     * @throws
     */
    public static boolean setWifiApEnabled(WifiManager wifiManager,
            String ssid, String sharedKey, boolean enable)
    {
        // 如果设置wifi AP模式可用并且 wifi sta也可用，先关闭sta模式
        if (enable && wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(false);
        }
        boolean result = false;
        try
        {
            WifiConfiguration config = new WifiConfiguration();
            config.SSID = ssid;
            config.preSharedKey = sharedKey;
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
            Method method = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            result = (Boolean) method.invoke(wifiManager, config, enable);
            Log.d(TAG, "setWifiApEnabled result= " + result);
        }
        catch (IllegalArgumentException e)
        {
            Log.d(TAG, "IllegalArgumentException e:" + e.toString());
        }
        catch (NoSuchMethodException e)
        {
            Log.d(TAG, "NoSuchMethodException e:" + e.toString());
        }
        catch (IllegalAccessException e)
        {
            Log.d(TAG, "IllegalAccessException e:" + e.toString());
        }
        catch (InvocationTargetException e)
        {
            Log.d(TAG, "InvocationTargetException e:" + e.toString());
        }
        return result;
    }

    /**
     * 
     * @Title getWifiApState
     * @Description TODO 获取当前AP状态
     * @param wifiManager
     * @return
     * @throws
     */
    public static int getWifiApState(WifiManager wifiManager)
    {
        int state = 0;
        try
        {
            Method method = wifiManager.getClass().getMethod("getWifiApState");
            state = (Integer) method.invoke(wifiManager);
        }
        catch (IllegalArgumentException e)
        {
            Log.d(TAG, "IllegalArgumentException e:" + e.toString());
        }
        catch (NoSuchMethodException e)
        {
            Log.d(TAG, "NoSuchMethodException e:" + e.toString());
        }
        catch (IllegalAccessException e)
        {
            Log.d(TAG, "IllegalAccessException e:" + e.toString());
        }
        catch (InvocationTargetException e)
        {
            Log.d(TAG, "InvocationTargetException e:" + e.toString());
        }
        return state;
    }
}
