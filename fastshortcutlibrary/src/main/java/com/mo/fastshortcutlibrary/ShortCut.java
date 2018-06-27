package com.mo.fastshortcutlibrary;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.IntRange;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * Created by work on 2018/6/25.
 */

public class ShortCut {

    private static final String TAG = "ShortCut.class";

    /**
     * 待添加属性
     */
    private String action;
    private String shortCutName;
    private String tag;
    private int resourceId;
    private String shortLabel;
    private String longLabel;
    private Class<?> targetClass;
    private String dynamicShortCutId;


    /**
     * 跳转意图Intent
     */
    private Intent intent;

    /**
     * 用于创建或移除DynamicShortCut
     */
    private ShortcutInfo shortcutInfo;


    /**
     * ShortCut的构造方法
     *
     * @param builder
     */
    private ShortCut(ShortCutBuilder builder) {

        Log.d(TAG, "ShortCut:Shortcut()");

        this.action = builder.action;
        this.shortCutName = builder.shortCutName;
        this.resourceId = builder.resourceId;
        this.shortLabel = builder.shortLabel;
        this.longLabel = builder.longLabel;
        this.targetClass = builder.targetClass;
    }


    /**
     * Builder静态内部类
     */
    public static class ShortCutBuilder {

        /**
         * API25之前所需的属性
         */
        private String action;
        private String shortCutName;
        private int resourceId;

        /**
         * API25及之后所需的属性
         */
        private Class<?> targetClass;
        private String shortLabel;
        private String longLabel;



        /**
         * API25之前添加快捷方式的Builder构造方法
         *
         * @param action
         */
        public ShortCutBuilder(String action) {
            Log.d(TAG, "ShortCutBuilder:ShortCutBuilder(String action)");
            this.action = action;
        }

        /**
         * API25之前添加快捷方式名称
         *
         * @param shortCutName
         * @return
         */
        public ShortCutBuilder name(String shortCutName) {
            Log.d(TAG, "ShortCutBuilder:name()");
            this.shortCutName = shortCutName;
            return this;
        }

        /**
         * 通用添加快捷方式Icon
         *
         * @param resourceId
         * @return
         */
        public ShortCutBuilder icon(int resourceId) {
            Log.d(TAG, "ShortCutBuilder:icon()");
            this.resourceId = resourceId;
            return this;
        }




        /**
         * API25及之后添加快捷方式的Builder构造方法
         *
         * @param targetClass
         */
        public ShortCutBuilder(Class<?> targetClass) {
            Log.d(TAG, "ShortCutBuilder:ShortCutBuilder(Class<?> targetClass, String dynamicShortCutId)");
            this.targetClass = targetClass;
        }

        /**
         * API25及之后添加快捷方式短名称
         *
         * @param shortLabel
         * @return
         */
        public ShortCutBuilder shortLabel(String shortLabel) {
            Log.d(TAG, "ShortCutBuilder:shortLabel()");
            this.shortLabel = shortLabel;
            return this;
        }

        /**
         * API25及之后添加快捷方式长名称
         *
         * @param longLabel
         * @return
         */
        public ShortCutBuilder longLabel(String longLabel) {
            Log.d(TAG, "ShortCutBuilder:longLabel()");
            this.longLabel = longLabel;
            return this;
        }



        /**
         * 创建ShortCut对象
         *
         * @return
         */
        public ShortCut build() {
            Log.d(TAG, "ShortCutBuilder:build()");
            return new ShortCut(this);
        }
    }


    /**
     * API25之前创建桌面快捷方式
     *
     * @param context
     * @param tag
     */
    public void createShortCut(Context context, String tag) {

        if (Build.VERSION.SDK_INT < 25) {
            Log.d(TAG, "ShortCut:createShortCut：" + "创建桌面快捷方式");

            //构建跳转意图的Intent
            intent = new Intent(action);

            //构建创建快捷方式的Intent
            Intent createIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

            //默认不允许重复创建快捷方式
            createIntent.putExtra("duplicate", false);

            //设置快捷方式名称
            if (shortCutName == null) {
                //设置默认名称
                createIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "快捷方式");
            } else {
                createIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortCutName);
            }

            //设置快捷方式Icon
            if (resourceId == 0) {
                //设置默认图标
                Parcelable icon = Intent.ShortcutIconResource.fromContext(context, R.drawable.ic_launcher);
                createIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
            } else {
                Parcelable icon = Intent.ShortcutIconResource.fromContext(context, resourceId);
                createIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
            }

            //传入跳转意图Intent
            createIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

            //发送广播创建
            context.sendBroadcast(createIntent);

            //将创建的Shortcut对象以Tag为key传入HashMap中
            this.tag = tag;
            ShortCutHelper.getSingleShortCutHashMap().put(tag, this);

        } else {
            Log.d(TAG, "ShortCut:createShortCut：" + "创建失败，API25及以上请使用createDynamicShortCut()");
        }
    }

    /**
     * API25之前删除桌面快捷方式
     *
     * @param context
     * @param tag
     */
    public static void deleteShortCut(Context context, String tag) {

        if (Build.VERSION.SDK_INT < 25) {
            Log.d(TAG, "ShortCut:deleteShortCut：" + "删除桌面快捷方式");

            //从HashMap中获取到该Tag（Key）对应的ShortCut实例
            ShortCut shortCut = ShortCutHelper.getSingleShortCutHashMap().get(tag);

            //判断是否重复删除
            if (shortCut == null) {
                Log.d(TAG, "ShortCut:deleteShortCut：" + "删除失败，未能根据Tag找到相应的ShortCut");
                return;
            }

            //构建删除快捷方式的Intent
            Intent deleteIntent = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");

            //设置快捷方式名称
            if (shortCut.shortCutName == null) {
                //设置默认名称
                deleteIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "快捷方式");
            } else {
                deleteIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortCut.shortCutName);
            }

            //传入跳转意图Intent
            deleteIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCut.intent);

            // 发送广播
            context.sendBroadcast(deleteIntent);

            //将此ShortCut从HashMap中移除
            ShortCutHelper.getSingleShortCutHashMap().remove(tag);
        } else {
            Log.d(TAG, "ShortCut:deleteShortCut：" + "删除失败，API25及以上请使用deleteDynamicShortCut()");
        }

    }


    /**
     * API25及之后创建弹出式快捷方式(API25之前会报ClassNotFoundException)
     *
     * @param context
     * @param dynamicShortCutId 如果DynamicShortCutId重复设置，则后面的会覆盖前面的
     */
    public void createDynamicShortCut(Context context, String dynamicShortCutId) {

        if (Build.VERSION.SDK_INT >= 25) {

            //DynamicShortCut最多只能显示4个
            //第5个能添加但不能显示
            //添加超过5个会报错java.lang.IllegalArgumentException: Max number of dynamic shortcuts exceeded
            if (ShortCutHelper.getSingleDynamicShortCutList().size() < 4) {

                Log.d(TAG, "ShortCut:createDynamicShortCut：" + "创建弹出式快捷方式");

                //构建跳转意图的Intent
                intent = new Intent(context, targetClass);
                intent.setAction(Intent.ACTION_VIEW);

                //对Builder中元素进行默认赋值
                if (shortLabel == null) {
                    shortLabel = "快捷方式" + dynamicShortCutId;
                }

                if (longLabel == null) {
                    longLabel = "快捷方式" + dynamicShortCutId;
                }

                if (resourceId == 0) {
                    resourceId = R.drawable.ic_launcher;
                }

                //构建ShortcutInfo
                shortcutInfo = new ShortcutInfo.Builder(context, dynamicShortCutId)
                        .setShortLabel(shortLabel)
                        .setLongLabel(longLabel)
                        .setIcon(Icon.createWithResource(context, resourceId))
                        .setIntent(intent)
                        .build();

                //将ShortcutInfo加入到DynamicShortCutList当中
                ShortCutHelper.getSingleDynamicShortCutList().add(shortcutInfo);

                //将DynamicShortCutList添加到ShortCutManager中
                ShortCutHelper.getShortcutManager(context).setDynamicShortcuts(ShortCutHelper.getSingleDynamicShortCutList());

                //保存当前DynamicShortCut对象的Id
                this.dynamicShortCutId = dynamicShortCutId;
            } else {
                Log.d(TAG, "ShortCut:createDynamicShortCut：" + "创建失败，DynamicShortCut最多只能显示4个\n" +
                        "            第5个能添加但不能显示\n" +
                        "            添加超过5个会抛出异常java.lang.IllegalArgumentException: Max number of dynamic shortcuts exceeded");
            }

        } else {
            Log.d(TAG, "ShortCut:createDynamicShortCut：" + "创建失败，API25以下的桌面快捷方式创建请使用createShortCut()");
        }
    }

    /**
     * API25及之后修改弹出式快捷方式的顺序
     *
     * rank值（1-4）越大离app越远
     *
     * @param context
     * @param targetDynamicShortCutId  目标移动的ShortCut的Id
     * @param targetRank 目标移动的ShortCut的Rank值
     * @param beMovedDynamicShortCutId  被目标移动的ShortCut的Rank值指定的 该Rank值位置上原来的ShortCut的Id
     * @param beMovedRank  被目标移动的ShortCut的Rank值指定的 该Rank值位置上原来的ShortCut的新的Rank值
     */
    public static void updateDynamicShortCutByRank(Context context, String targetDynamicShortCutId, @IntRange(from = 1, to = 4 )int targetRank, String beMovedDynamicShortCutId, @IntRange(from = 1, to = 4 )int beMovedRank) {

        if (Build.VERSION.SDK_INT >= 25) {

            if (ShortCutHelper.getSingleDynamicShortCutList().size() == 0) {
                Log.d(TAG, "ShortCut:updateDynamicShortCutByRank：" + "更改位置失败，请先创建快捷方式");
                return;
            } else {

                boolean isTargetExist = false;
                boolean isBeMovedExist = false;

                //获取到ShortCutManager封装的DynamicShortCuts
                List<ShortcutInfo> list = ShortCutHelper.getShortcutManager(context).getDynamicShortcuts();

                for (int i = 0; i < list.size(); i++) {
                    ShortcutInfo info = list.get(i);
                    //检查Target是否存在
                    if (targetDynamicShortCutId.equals(info.getId())) {
                        isTargetExist = true;
                    }
                    //检查BeMoved是否存在
                    if (beMovedDynamicShortCutId.equals(info.getId())) {
                        isBeMovedExist = true;
                    }
                }

                if (isTargetExist && isBeMovedExist) {
                    ShortcutInfo targetInfo = new ShortcutInfo.Builder(context, targetDynamicShortCutId)
                            .setRank(targetRank)
                            .build();

                    ShortcutInfo beMovedInfo = new ShortcutInfo.Builder(context, beMovedDynamicShortCutId)
                            .setRank(beMovedRank)
                            .build();

                    ShortCutHelper.getShortcutManager(context).updateShortcuts(Arrays.asList(targetInfo, beMovedInfo));
                } else if (!isTargetExist) {
                    Log.d(TAG, "ShortCut:updateDynamicShortCutByRank：" + "更改位置失败，无法找到该Id的targetDynamicShortCut");
                } else if (!isBeMovedExist) {
                    Log.d(TAG, "ShortCut:updateDynamicShortCutByRank：" + "更改位置失败，无法找到该Id的beMovedDynamicShortCut");
                }

            }

        } else {
            Log.d(TAG, "ShortCut:updateDynamicShortCutByRank：" + "更改位置失败，API25以下无需使用此方法");
        }
    }

    /**
     * API25及之后删除弹出式快捷方式
     *
     * @param context
     * @param dynamicShortCutId
     */
    public static void deleteDynamicShortCut(Context context, String dynamicShortCutId) {

        if (Build.VERSION.SDK_INT >= 25) {

            //获取到ShortCutManager封装的DynamicShortCuts
            List<ShortcutInfo> list = ShortCutHelper.getShortcutManager(context).getDynamicShortcuts();

            for (int i = 0; i < list.size(); i++){
                ShortcutInfo info = list.get(i);
                if (dynamicShortCutId.equals(info.getId())) {
                    //将ShortCutManager封装的DynamicShortCuts中的对象删除
                    ShortCutHelper.getShortcutManager(context).removeDynamicShortcuts(Arrays.asList(info.getId()));
                }
            }

        } else {
            Log.d(TAG, "ShortCut:deleteDynamicShortCut：" + "删除失败，API25以下的桌面快捷方式删除请使用deleteShortCut()");
        }
    }


}
