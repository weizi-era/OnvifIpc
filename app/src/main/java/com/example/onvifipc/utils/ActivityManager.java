package com.example.onvifipc.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理Activity
 */
public class ActivityManager {

    // 保存所有创建的Activity
    private List<Activity> activities = new ArrayList<>();

    /**
     * 添加Activity
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (activity != null) {
            activities.add(activity);
        }
    }

    /**
     * 移除Activity
     * @param activity
     */
    public void removeActivity(Activity activity) {
        if (activity != null) {
            activities.remove(activity);
        }
    }

    /**
     * 关闭所有Activity
     */
    public void finishAll() {
        for (Activity activity: activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        activities.clear();
    }
}
