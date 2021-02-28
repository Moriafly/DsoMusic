#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

JNIEXPORT jstring JNICALL
Java_com_dirror_music_MyApplication_getBmobAppKey(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "0d1d3b9214e037c76de958993ddd6563");
}

JNIEXPORT jstring JNICALL
Java_com_dirror_music_MyApplication_getUmAppKey(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "5fb38e09257f6b73c0961382");
}

JNIEXPORT jint JNICALL
Java_com_dirror_music_util_Secure_getSignatureHash(JNIEnv *env, jobject thiz) {
    return -1550371158;
}

JNIEXPORT jstring JNICALL
Java_com_dirror_music_util_sky_SkySecure_getAppNameMd5(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "9884b247104cbdb489aeeaca91f49584");
}

/**
 * Xposed 监测
 * 存在返回 true
 */
JNIEXPORT jboolean JNICALL
Java_com_dirror_music_util_sky_SkySecure_checkXposed(JNIEnv *env, jobject thiz) {
    // 找到 ClassLoader 类
    jclass classloaderClass = (*env)->FindClass(env, "java/lang/ClassLoader");
    // 找到 ClassLoader 类中的静态方法 getSystemClassLoader
    jmethodID getSysLoaderMethod = (*env)->GetStaticMethodID(env, classloaderClass, "getSystemClassLoader",
                                                             "()Ljava/lang/ClassLoader;");
    // 调用ClassLoader中的getSystemClassLoader方法，返回ClassLoader对象
    jobject classLoader = (*env)->CallStaticObjectMethod(env, classloaderClass, getSysLoaderMethod);
    // DexClassLoader：能够加载自定义的jar/apk/dex
    // PathClassLoader：只能加载系统中已经安装过的apk
    jclass dexLoaderClass = (*env)->FindClass(env, "dalvik/system/DexClassLoader");
    // 找到 ClassLoader中的方法 loadClass
    jmethodID loadClass = (*env)->GetMethodID(env, dexLoaderClass, "loadClass",
                                              "(Ljava/lang/String;)Ljava/lang/Class;");
    // 调用 DexClassLoader 的 loadClass 方法，加载需要调用的类
    jstring dir = (*env)->NewStringUTF(env, "de.robv.android.xposed.XposedBridge");
    jclass targetClass = (jclass) (*env)->CallObjectMethod(env, classLoader, loadClass, dir);

    if ((*env)->ExceptionCheck(env)) { // 检查 JNI 调用是否有引发异常
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env); // 清除引发的异常，在 Java 层不会打印异常的堆栈信息
        return false;
    }

    if (targetClass != NULL) {
        jfieldID disableHooksFiled = (*env)->GetStaticFieldID(env, targetClass, "disableHooks", "Z");
        (*env)->SetStaticBooleanField(env, targetClass, disableHooksFiled, true);
        jfieldID runtimeFiled = (*env)->GetStaticFieldID(env, targetClass, "runtime", "I");
        (*env)->SetStaticIntField(env, targetClass, runtimeFiled, 2);
        return true;
    } else {
        return false;
    }
}