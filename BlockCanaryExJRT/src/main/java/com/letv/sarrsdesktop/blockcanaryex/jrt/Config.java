package com.letv.sarrsdesktop.blockcanaryex.jrt;

import com.letv.sarrsdesktop.blockcanaryex.jrt.internal.BlockMonitor;
import com.letv.sarrsdesktop.blockcanaryex.jrt.internal.ProcessUtils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Looper;

/**
 * author: zhoulei date: 2017/3/2.
 */
public class Config implements BlockMonitor.BlockObserver {
    private final Context mContext;

    public Config(Context context) {
        if(context == null) {
            throw new IllegalArgumentException("context must not be null!");
        }
        mContext = context.getApplicationContext();
    }

    /**
     * get the context we use
     *
     * @return context
     */
    public final Context getContext() {
        return mContext;
    }

    /**
     * provide the looper to watch, default is Looper.mainLooper()
     *
     * @return the looper you want to watch
     */
    public Looper provideWatchLooper() {
        return Looper.getMainLooper();
    }

    /**
     * If need notification to notice block.
     *
     * @return true if need, else if not need.
     */
    public boolean displayNotification() {
        return true;
    }

    /**
     * judge whether the loop is blocked, you can override this to decide
     * whether it is blocked by your logic
     *
     * Note: running in none ui thread
     *
     * @param startTime in mills
     * @param endTime in mills
     * @param startThreadTime in mills
     * @param endThreadTime in mills
     * @return true if blocked, else false
     */
    public boolean isBlock(long startTime, long endTime, long startThreadTime, long endThreadTime) {
        return (endTime - startTime) > 100L && (endThreadTime - startThreadTime) > 8L;
    }

    /**
     * judge whether the method is heavy method, we will print heavy method in log
     *
     * Note: running in none ui thread
     *
     * @param methodInfo {@link MethodInfo}
     * @return true if it is heavy method, else false
     */
    public boolean isHeavyMethod(MethodInfo methodInfo) {
        return methodInfo.getCostThreadTime() > 0L;
    }

    /**
     * judge whether the method is called frequently, we will print frequent method in log
     *
     * Note: running in none ui thread
     *
     * @param frequentMethodInfo the execute info of same method in this loop {@link FrequentMethodInfo}
     * @return true if it is frequent method, else false
     */
    public boolean isFrequentMethod(FrequentMethodInfo frequentMethodInfo) {
        return frequentMethodInfo.getTotalCostRealTimeMs() > 1L && frequentMethodInfo.getCalledTimes() > 1;
    }

    /**
     * Path to save log, like "/blockcanary/", will save to sdcard if can, else we will save to
     * "${context.getFilesDir()/${provideLogPath()}"}"
     *
     * Note: running in none ui thread
     *
     * @return path of log files
     */
    public String provideLogPath() {
        return "/blockcanaryex/" + getContext().getPackageName() + "/";
    }

    /**
     * Network type to record in log, you should impl this if you want to record this
     *
     * @return {@link String} like 2G, 3G, 4G, wifi, etc.
     */
    public String provideNetworkType() {
        return "unknown";
    }

    /**
     * unique id to record in log, you should impl this if you want to record this
     *
     * @return {@link String} like imei, account id...
     */
    public String provideUid() {
        return "unknown";
    }

    /**
     * Implement in your project.
     *
     * @return Qualifier which can specify this installation, like version + flavor.
     */
    @TargetApi(Build.VERSION_CODES.DONUT)
    public String provideQualifier() {
        PackageInfo packageInfo = ProcessUtils.getPackageInfo(getContext());
        ApplicationInfo applicationInfo = getContext().getApplicationInfo();
        if(packageInfo != null) {
            return applicationInfo.name + "-" + packageInfo.versionName;
        }
        return "unknown";
    }

    /**
     * Block listener, developer may provide their own actions
     *
     * @param blockInfo {@link BlockInfo}
     */
    @Override
    public void onBlock(BlockInfo blockInfo) {
    }
}
