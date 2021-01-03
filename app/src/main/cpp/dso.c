#include <jni.h>
#include <stdio.h>
#include <stdlib.h>

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