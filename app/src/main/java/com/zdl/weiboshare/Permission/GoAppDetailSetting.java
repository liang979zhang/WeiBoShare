package com.zdl.weiboshare.Permission;

import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by 89667 on 2018/3/14.
 */

public class GoAppDetailSetting {


    static String SCHEME = "package";
    //调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
    static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    //调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
    static final String APP_PKG_NAME_22 = "pkg";
    //InstalledAppDetails所在包名
    static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    //InstalledAppDetails类名
    static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";


    /*
    检测手机类型
     */
    private static class CheckPhoneSystemUtils {
        private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
        private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
        private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

        /**
         * 检测MIUI
         *
         * @return
         */
        public static boolean isMIUI() {
            try {
                final BuildProperties prop = BuildProperties.newInstance();
                return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
            } catch (final IOException e) {
                return false;
            }
        }

        /**
         * 检测Flyme
         *
         * @return
         */
        public static boolean isFlyme() {
            try { // Invoke Build.hasSmartBar()
                final Method method = Build.class.getMethod("hasSmartBar");
                return method != null;
            } catch (final Exception e) {
                return false;
            }
        }
    }

    /**
     * 判断手机类型工具类
     */
    public static class BuildProperties {
        private final Properties properties;

        private BuildProperties() throws IOException {
            properties = new Properties();
            properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
        }

        public boolean containsKey(final Object key) {
            return properties.containsKey(key);
        }

        public boolean containsValue(final Object value) {
            return properties.containsValue(value);
        }

        public Set<Map.Entry<Object, Object>> entrySet() {
            return properties.entrySet();
        }

        public String getProperty(final String name) {
            return properties.getProperty(name);
        }

        public String getProperty(final String name, final String defaultValue) {
            return properties.getProperty(name, defaultValue);
        }

        public boolean isEmpty() {
            return properties.isEmpty();
        }

        public Enumeration<Object> keys() {
            return properties.keys();
        }

        public Set<Object> keySet() {
            return properties.keySet();
        }

        public int size() {
            return properties.size();
        }

        public Collection<Object> values() {
            return properties.values();
        }

        public static BuildProperties newInstance() throws IOException {
            return new BuildProperties();
        }
    }
}
