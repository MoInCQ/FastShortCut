package com.mo.fastshortcutlibrary;

import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by work on 2018/6/25.
 */

public class ShortCutHelper {

    private volatile static HashMap<String, ShortCut> hashMap = null;
    private volatile static List<ShortcutInfo> list = null;
    private volatile static ShortcutManager shortcutManager = null;

    private ShortCutHelper() {
    }

    /**
     * 获取桌面快捷方式的HashMap
     *
     * @return
     */
    public static HashMap<String, ShortCut> getSingleShortCutHashMap() {
        if (hashMap == null) {
            synchronized (ShortCutHelper.class) {
                if (hashMap == null) {
                    hashMap = new HashMap();
                }
            }
        }
        return hashMap;
    }


    /**
     * 获取弹出式快捷方式列表
     *
     * @return
     */
    public static List<ShortcutInfo> getSingleDynamicShortCutList(){
        if (list == null) {
            synchronized (ShortCutHelper.class) {
                if (list == null) {
                    list = new ArrayList<>();
                }
            }
        }
        return list;
    }


    /**
     * 获取ShotCutManager
     *
     * @param context
     * @return
     */
    public static ShortcutManager getShortcutManager(Context context){
        if (shortcutManager == null) {
            synchronized (ShortCutHelper.class) {
                if (shortcutManager == null) {
                    shortcutManager = context.getSystemService(ShortcutManager.class);
                }
            }
        }
        return shortcutManager;
    }


}
