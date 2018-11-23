package com.example.a10767.electronic_wardrobe;


import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10767 on 2018/9/18.
 */

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>(); //所有活动的集合

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finaishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        activities.clear();
    }
}
