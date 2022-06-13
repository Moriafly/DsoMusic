package com.dirror.music.plugin;

import android.app.Activity;
import android.os.Build;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialog;

import com.dirror.music.music.standard.data.StandardSongData;
import com.dirror.music.service.ServiceSongUrl;
import com.dirror.music.util.HttpUtils;
import com.dirror.music.util.TopLevelFuncationKt;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import dalvik.system.DexClassLoader;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class PluginSupport {
    public static final String TAG = "plugin-support";
    private static Map<String, List<Function<Map<String, Object>, Object>>> pluginMap;
    private static final Map<String, Object> pluginContext = new ConcurrentHashMap<>();

    static {
        init();
    }

    private static String getPluginRootPath() {
        //TODO 插件根目录
        return "/sdcard/_DsoMusic";
    }

    private static String getPluginPath() {
        return getPluginRootPath() + "/plugin";
    }

    private static String getTargetPath() {
        return getPluginRootPath() + "/target";
    }

    private static void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            initPluginContext();
        }
        initPlugin();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void initPluginContext() {
        pluginContext.put(PluginConstants.METHOD_DISMISS, new Function<Object, Object>() {
            @Override
            public Object apply(Object s) {
                AppCompatDialog o = (AppCompatDialog) pluginContext.get(PluginConstants.FIELD_DIALOG);
                if (o != null) {
                    o.dismiss();
                }
                return null;
            }
        });


        pluginContext.put(PluginConstants.METHOD_GET_URL_PROXY, new Function<Function, Object>() {
            @Override
            public Object apply(Function success) {
                StandardSongData songData = (StandardSongData) pluginContext.get(PluginConstants.FIELD_SONG);
                ServiceSongUrl.INSTANCE.getUrlProxy(songData, new Function1<Object, Unit>() {
                    @Override
                    public Unit invoke(Object o) {
                        success.apply(o);
                        return Unit.INSTANCE;
                    }
                });
                return null;
            }
        });

        pluginContext.put(PluginConstants.METHOD_TOAST, new Function<String, Object>() {
            @Override
            public Object apply(String s) {
                TopLevelFuncationKt.toast(s);
                return null;
            }
        });

        pluginContext.put(PluginConstants.METHOD_HTTP_GET, new BiFunction<String, Class<?>, Object>() {
            @Override
            public Object apply(String s, Class<?> aClass) {
                return HttpUtils.INSTANCE.get0(s, aClass);
            }
        });
    }

    private static void initPlugin() {
        pluginMap = new HashMap<>();
        String pluginPath = getPluginPath();
        File[] files = new File(pluginPath).listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                continue;
            }
            String name = file.getName();
            if (name.endsWith(".dex")) {
                Map<String, Function<Map<String, Object>, Object>> plugin = loadPlugin(file);
                if (plugin == null) {
                    continue;
                }
                for (Map.Entry<String, Function<Map<String, Object>, Object>> entry : plugin.entrySet()) {
                    String key = entry.getKey();
                    Function<Map<String, Object>, Object> function = entry.getValue();
                    if (!pluginMap.containsKey(key)) {
                        pluginMap.put(key, new ArrayList<>());
                    }
                    pluginMap.get(key).add(function);
                }
            }
        }
    }

    private static Map<String, Function<Map<String, Object>, Object>> loadPlugin(File dexFile) {
        try {
            String pluginName = dexFile.getName();
            pluginName = pluginName.replace(".dex", "");
            DexClassLoader classLoader = new DexClassLoader(dexFile.getAbsolutePath(), getTargetPath() + "/" + pluginName, null, PluginSupport.class.getClassLoader());

            String mainClass = "com.dirror.music.plugin." + pluginName.substring(0, 1).toUpperCase() + pluginName.substring(1) + "Main";
            Class<?> pluginMain = classLoader.loadClass(mainClass);
            Method pluginMethods = pluginMain.getMethod("pluginMethods");
            Object result = pluginMethods.invoke(null);
            if (result instanceof Map) {
                return (Map<String, Function<Map<String, Object>, Object>>) result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setRid(String rid) {
        PluginSupport.pluginContext.put(PluginConstants.FIELD_KUWO_RID, rid);
    }

    public static void setSong(StandardSongData songData) {
        PluginSupport.pluginContext.put(PluginConstants.FIELD_SONG, songData);
    }

    public static void setActivity(Activity activity) {
        PluginSupport.pluginContext.put(PluginConstants.FIELD_ACTIVITY, activity);
    }

    public static void setDialog(AppCompatDialog dialog) {
        PluginSupport.pluginContext.put(PluginConstants.FIELD_DIALOG, dialog);
    }

    public static void setMenu(LinearLayout menuParent) {
        PluginSupport.pluginContext.put(PluginConstants.FIELD_SONG_MENU_PARENT, menuParent);
    }

    public static Object apply(String point) {
        List<Function<Map<String, Object>, Object>> functions = pluginMap.get(point);
        if (functions == null) {
            return null;
        }
        Map<String, Object> context = new HashMap<>(pluginContext);
        for (Function<Map<String, Object>, Object> function : functions) {
            Object result = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                result = function.apply(context);
            }
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
