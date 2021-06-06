#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

JNIEXPORT jstring JNICALL
Java_com_dirror_music_MyApp_getBmobAppKey(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "0d1d3b9214e037c76de958993ddd6563");
}

JNIEXPORT jstring JNICALL
Java_com_dirror_music_MyApp_getUmAppKey(JNIEnv *env, jobject thiz) {
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


