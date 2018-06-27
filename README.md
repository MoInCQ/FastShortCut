# FastShortCut 使用指南

### 概述
- 实现Android快速添加及删除ShortCut
    - API25（Nougat 7.1）以下机型为桌面ShortCut
    - API25（Nougat 7.1）及以上机型为弹出式ShortCut

- 实现弹出式ShortCut显示顺序切换
- 实现ShortCut跳转任意指定Activity
- 实现app卸载时ShortCut随之一起移除

### 一、添加依赖库
##### 1、Add it in your root build.gradle at the end of repositories:
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
##### 2、Add the dependency
```
dependencies {
        implementation 'com.github.MoInCQ:FastShortCut:v1.1'
}
```
### 二、基本使用

##### 1、API25（Nougat 	7.1）以下机型
- （1）在AndroidManifest.xml中配置ShortCut目标跳转的Activity的自定义action
    ```
    <activity android:name=".MainActivity">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />

            //目标跳转的Activity标签下必须新增以下两条标签
            //标签1：用于自定义aciton
            <action android:name="@strings/main_activity_action" />
            //标签2：用于确保自定义action可以生效
            <category android:name="android.intent.category.DEFAULT" />
            
        </intent-filter>
    </activity>
    ```
- （2）创建Builder构建快捷方式相关信息
    ```
    //必须指定跳转activity的action
    ShortCut shortCut = new ShortCut.ShortCutBuilder("@strings/main_activity_action")  
                        .icon(resourceId)  //指定ShortCut的con
                        .name("test")  //指定ShortCut的name
                        .build();
    ```
- （3）创建桌面快捷方式
    - ==createShortCut(Context context, String tag)==
        - context：上下文
        - tag：**唯一值**，用于标记当前shortCut的Id
    
    - 示例
        ```
        shortCut.createShortCut(MainActivity.this, "1");
        ```
- （4）删除桌面快捷方式
    - ==deleteShortCut(Context context, String tag)==
        - **静态方法**，使用类名直接调用 
        - context：上下文
        - tag：**唯一值**，用于标记当前shortCut的Id
    
    - 示例
        ```
        ShortCut.deleteShortCut(MainActivity.this, "1")
        ```

##### 2、API25（Nougat 	7.1）及以上机型
- （1）创建Builder构建快捷方式相关信息
    ```
    //必须指定待跳转activity
    ShortCut shortCut2 = new ShortCut.ShortCutBuilder(MainActivity.class)  
                        .shortLabel("shortLabel")  //指定ShortCut短名称
                        .longLabel("This is longLabel")  //指定ShortCut长名称
                        .icon(resourceId)  //指定ShortCut的icon
                        .build();
    ```
- （2）创建弹出式快捷方式
    - ==createDynamicShortCut(Context context, String dynamicShortCutId)==
        - context：上下文
        - dynamicShortCutId：**唯一值**，用于标记当前shortCut的Id
    
    - 示例
        ```
        shortCut.createDynamicShortCut(MainActivity.this, "3");
        ```

- （3）更改弹出式快捷方式的顺序
    - ==updateDynamicShortCutByRank(Context context, String targetDynamicShortCutId, @IntRange(from = 1, to = 4 )int targetRank, String beMovedDynamicShortCutId, @IntRange(from = 1, to = 4 )int beMovedRank)==
        - **静态方法**，使用类名直接调用 
        - **rank值（1-4）越大离app越远**
        - context：上下文
        - targetDynamicShortCutId：目标移动的ShortCut的Id
        - targetRank：目标移动的ShortCut的Rank值
            - 只能传入1-4 
        - beMovedDynamicShortCutId：被目标移动的ShortCut的Rank值指定的 该Rank值位置上原来的ShortCut的Id
        - beMovedRank：被目标移动的ShortCut的Rank值指定的 该Rank值位置上原来的ShortCut的新的Rank值
            - 只能传入1-4 

    - 示例 
        ```
        ShortCut.updateDynamicShortCutByRank(MainActivity.this, "2", 3, "3", 2);
        ```
- （4）删除弹出式快捷方式
    - ==deleteDynamicShortCut(Context context, String dynamicShortCutId)==
        - **静态方法**，使用类名直接调用
        - context：上下文
        - dynamicShortCutId：**唯一值**，用于标记当前shortCut的Id
    
    - 示例
        ```
        ShortCut.deleteDynamicShortCut(MainActivity.this, "1");
        ```

### 三、Tips

##### 1、目前存在的问题

- 部分机型删除及同步卸载功能不能实现
    
    - 小米手机使用删除桌面ShortCut方法失效

##### 2、注意

- ShortCut打开的Activity要注意其生命周期的变化
