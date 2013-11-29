#ifndef __COCOS2D_LOG_H__
#define __COCOS2D_LOG_H__

#include <android/log.h>

#define DEBUG 0

#if !defined(DEBUG) || DEBUG == 0
#define LOGD(LOG_TAG ,...) do{}while(0)
#elif DEBUG == 1
#define  LOGD(LOG_TAG ,...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#endif // COCOS2D_DEBUG
#endif
