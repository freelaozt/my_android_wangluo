package com.fxd.wangluo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by LaoZhang on 2018/3/8.
 */
public class NetworkUtils {

    /**
     * 没有连接类型。
     */
    public static final int TYPE_NONE = -1;

    /**
     * 未知的网络类。
     */
    public static final int NETWORK_CLASS_UNKNOWN = 0;
    /**
     * 广泛定义的“2G”网络类别。
     */
    public static final int NETWORK_CLASS_2_G = 1;
    /**
     * 广泛定义的“3G”网络类别。
     */
    public static final int NETWORK_CLASS_3_G = 2;
    /**
     * 广泛定义的“4G”网络类。
     */
    public static final int NETWORK_CLASS_4_G = 3;

    /**
     * 返回有关当前活动的默认数据网络的详细信息。
     * 连接时，这个网络是传出连接的默认路由。
     * 你应该经常检查{@link NetworkInfo＃isConnected（）}启动网络通信之前。
     * 这可能会返回{null} 当没有默认网络时。
     *
     * @return a {@link NetworkInfo} 对象为当前的默认网络，
     * 如果没有网络默认网络当前处于活动状态，则为{null}
     * <p/>
     * 这种方法需要调用来保持许可
     * {@link android.Manifest.permission#ACCESS_NETWORK_STATE}.
     * @see ConnectivityManager#getActiveNetworkInfo()
     */
    public static NetworkInfo getInfo(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
    }

    /**
     * 报告当前的网络类型。
     *
     * @return {@link ConnectivityManager#TYPE_MOBILE}, {@link ConnectivityManager#TYPE_WIFI} ,
     * {@link ConnectivityManager#TYPE_WIMAX}, {@link ConnectivityManager#TYPE_ETHERNET}, {@link
     * ConnectivityManager#TYPE_BLUETOOTH}, or other types defined by {@link ConnectivityManager}.
     * 如果没有网络连接，则返回-1。
     * @see NetworkInfo#getType()
     */
    public static int getType(Context context) {
        NetworkInfo info = getInfo(context);
        if (info == null || !info.isConnected()) {
            return TYPE_NONE;
        }
        return info.getType();
    }

    /**
     * 返回描述网络子类型的网络类型特定的整数。
     *
     * @return the network subtype
     * @see NetworkInfo#getSubtype()
     */
    public static int getSubType(Context context) {
        NetworkInfo info = getInfo(context);
        if (info == null || !info.isConnected()) {
            return TYPE_NONE;
        }
        return info.getSubtype();
    }

    /**
     * 返回当前数据连接的NETWORK_TYPE_xxxx。
     */
    public static int getNetworkType(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE))
                .getNetworkType();
    }

    /**
     * 检查是否有任何连接
     */
    public static boolean isConnected(Context context) {
        return getType(context) != TYPE_NONE;
    }

    /**
     * 检查是否有连接到Wifi网络
     */
    public static boolean isWifiConnection(Context context) {
        NetworkInfo info = getInfo(context);
        if (info == null || !info.isConnected()) {
            return false;
        }
        switch (info.getType()) {
            case ConnectivityManager.TYPE_WIFI:
                return true;
            default:
                return false;
        }
    }

    /**
     * 检查是否有任何连接到移动网络
     */
    public static boolean isMobileConnection(Context context) {
        NetworkInfo info = getInfo(context);
        if (info == null || !info.isConnected()) {
            return false;
        }
        switch (info.getType()) {
            case ConnectivityManager.TYPE_MOBILE:
                return true;
            default:
                return false;
        }
    }

    /**
     * 检查当前连接是否快速。
     */
    public static boolean isConnectionFast(Context context) {
        NetworkInfo info = getInfo(context);
        if (info == null || !info.isConnected()) {
            return false;
        }
        switch (info.getType()) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_ETHERNET:
                return true;
            case ConnectivityManager.TYPE_MOBILE:
                int networkClass = getNetworkClass(getNetworkType(context));
                switch (networkClass) {
                    case NETWORK_CLASS_UNKNOWN:
                    case NETWORK_CLASS_2_G:
                        return false;
                    case NETWORK_CLASS_3_G:
                    case NETWORK_CLASS_4_G:
                        return true;
                }
            default:
                return false;
        }
    }

    private static int getNetworkClassReflect(int networkType)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getNetworkClass = TelephonyManager.class.getDeclaredMethod("getNetworkClass", int.class);
        if (!getNetworkClass.isAccessible()) {
            getNetworkClass.setAccessible(true);
        }
        return (int) getNetworkClass.invoke(null, networkType);
    }

    /**
     * 返回一般网络类型，如“3G”或“4G”。
     * 在分类的情况下有争议的，这种方法是保守的。
     */
    public static int getNetworkClass(int networkType) {
        try {
            return getNetworkClassReflect(networkType);
        } catch (Exception ignored) {
        }

        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case 16: // TelephonyManager.NETWORK_TYPE_GSM:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case 17: // TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                return NETWORK_CLASS_3_G;
            case TelephonyManager.NETWORK_TYPE_LTE:
            case 18: // TelephonyManager.NETWORK_TYPE_IWLAN:
                return NETWORK_CLASS_4_G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }
}
