#include <stdlib.h>
#include <jni.h>
#include <android/log.h>
#include <string>
#include "JniHelper.h"
#include "cocoa/CCString.h"
#include "Java_org_cocos2dx_lib_Cocos2dxHelper.h"
#include <android/bitmap.h>


#define  LOG_TAG    "Java_org_cocos2dx_lib_Cocos2dxHelper.cpp"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#define  CLASS_NAME "org/cocos2dx/lib/Cocos2dxHelper"

static EditTextCallback s_pfEditTextCallback = NULL;
static XunLeiLoginCallback s_pfXunLeiLoginCallback = NULL;
static BaiduLoginCallback s_pfBaiduLoginCallback = NULL;
static MainGeneratePincode s_pfMainGeneratePincode = NULL;
static SettingDilogCallback s_pfSettingCallBack = NULL;
static MainUpdateQQCallback s_pfMainUpdateQQCallback = NULL;
static void* s_ctx = NULL;
static void* x_ctx = NULL;
static void* b_ctx = NULL;
static void* m_ctx = NULL;
static void* set_ctx = NULL;

using namespace cocos2d;
using namespace std;

string g_apkPath;

extern "C" {

    JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxHelper_nativeSetApkPath(JNIEnv*  env, jobject thiz, jstring apkPath) {
        g_apkPath = JniHelper::jstring2string(apkPath);
    }

    JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxHelper_nativeSetEditTextDialogResult(JNIEnv * env, jobject obj, jbyteArray text) {
        jsize  size = env->GetArrayLength(text);

        if (size > 0) {
            jbyte * data = (jbyte*)env->GetByteArrayElements(text, 0);
            char* pBuf = (char*)malloc(size+1);
            if (pBuf != NULL) {
                memcpy(pBuf, data, size);
                pBuf[size] = '\0';
                // pass data to edittext's delegate
                if (s_pfEditTextCallback) s_pfEditTextCallback(pBuf, s_ctx);
                free(pBuf);
            }
            env->ReleaseByteArrayElements(text, data, 0);
        } else {
            if (s_pfEditTextCallback) s_pfEditTextCallback("", s_ctx);
        }
    }

    JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxHelper_nativeSetXunLeiLoginDialogResult(JNIEnv * env, jobject obj,jint isBack) {

//    	char * c_account = (char *) env->GetStringUTFChars(accout, 0);
//    	char * c_passowrd = (char *) env->GetStringUTFChars(password, 0);
//    	char * c_yanzhengma = (char *) env->GetStringUTFChars(yanzhengma, 0);

        bool isCancle = false;
        if(isBack!=0){
        	isCancle = true;
        }

        if (s_pfXunLeiLoginCallback){
        	s_pfXunLeiLoginCallback(isCancle,x_ctx);
		}

//    	jsize  size_account = env->GetArrayLength(accout);
//    	jsize  size_password = env->GetArrayLength(password);
//    	jsize  size_yanzhengma = env->GetArrayLength(yanzhengma);
//
//        if (size_account > 0) {
//            jbyte * data = (jbyte*)env->GetByteArrayElements(accout, 0);
//            c_account = (char*)malloc(size_account+1);
//            memset(c_account,0,size_account+1);
//            if (c_account != NULL) {
//            	//strcat(c_account,accout);
//                memcpy(c_account, data, size_account);
//                c_account[size_account] = '\0';
//            }
//            env->ReleaseByteArrayElements(accout, data, 0);
//        }
//
//        if(size_password>0){
//        	jbyte * data = (jbyte*)env->GetByteArrayElements(password, 0);
//        	c_passowrd = (char*)malloc(size_password+1);
//			if (c_passowrd != NULL) {
//				memcpy(c_account, data, size_password);
//				c_passowrd[size_password] = '\0';
//			}
//			env->ReleaseByteArrayElements(password, data, 0);
//        }
//
//        if(size_yanzhengma>0){
//        	jbyte * data = (jbyte*)env->GetByteArrayElements(yanzhengma, 0);
//        	c_yanzhengma = (char*)malloc(size_yanzhengma+1);
//			if (c_yanzhengma != NULL) {
//				memcpy(c_account, data, size_yanzhengma);
//				c_yanzhengma[size_yanzhengma] = '\0';
//			}
//			env->ReleaseByteArrayElements(yanzhengma, data, 0);
//        }
//
//        bool isCancle = false;
//        if(isBack!=0){
//        	isCancle = true;
//        }
//
//        if (s_pfXunLeiLoginCallback){
//        	s_pfXunLeiLoginCallback(isBack, c_account,c_passowrd,c_yanzhengma);
//		}
//
//        free(c_account);
//        free(c_passowrd);
//        free(c_yanzhengma);
    }

    JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxHelper_nativeSetBaiduLoginDialogResult(JNIEnv * env, jobject obj,jint isBack) {
    	bool isCancle = false;
		if(isBack!=0){
			isCancle = true;
		}

		if (s_pfBaiduLoginCallback){
			s_pfBaiduLoginCallback(isCancle,b_ctx);
		}
    }

    JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxHelper_nativeSetPincodeResult(JNIEnv * env, jobject obj,jint isSuccess) {
    	bool successed = false;
		if(isSuccess!=0){
			successed = true;
		}
		if (s_pfMainGeneratePincode){
			s_pfMainGeneratePincode(successed,m_ctx);
		}
    }
    JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxHelper_nativeSetSettingResult(JNIEnv * env, jobject obj,jint isSuccess) {
    	bool successed = false;
		if(isSuccess!=0){
			successed = true;
		}
		if (s_pfSettingCallBack){
			s_pfSettingCallBack(successed,set_ctx);
		}
    }
    JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Cocos2dxHelper_nativeUpdateQQ(JNIEnv * env, jobject obj) {
		if (s_pfMainUpdateQQCallback){
			s_pfMainUpdateQQCallback(m_ctx);
		}
    }

}

const char * getApkPath() {
    return g_apkPath.c_str();
}

void showDialogJNI(const char * pszMsg, const char * pszTitle) {
    if (!pszMsg) {
        return;
    }

    JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "showDialog", "(Ljava/lang/String;Ljava/lang/String;)V")) {
        jstring stringArg1;

        if (!pszTitle) {
            stringArg1 = t.env->NewStringUTF("");
        } else {
            stringArg1 = t.env->NewStringUTF(pszTitle);
        }

        jstring stringArg2 = t.env->NewStringUTF(pszMsg);
        t.env->CallStaticVoidMethod(t.classID, t.methodID, stringArg1, stringArg2);

        t.env->DeleteLocalRef(stringArg1);
        t.env->DeleteLocalRef(stringArg2);
        t.env->DeleteLocalRef(t.classID);
    }
}

void showEditTextDialogJNI(const char* pszTitle, const char* pszMessage, int nInputMode, int nInputFlag, int nReturnType, int nMaxLength, EditTextCallback pfEditTextCallback, void* ctx) {
    if (pszMessage == NULL) {
        return;
    }

    s_pfEditTextCallback = pfEditTextCallback;
    s_ctx = ctx;

    JniMethodInfo t;
    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "showEditTextDialog", "(Ljava/lang/String;Ljava/lang/String;IIII)V")) {
        jstring stringArg1;

        if (!pszTitle) {
            stringArg1 = t.env->NewStringUTF("");
        } else {
            stringArg1 = t.env->NewStringUTF(pszTitle);
        }

        jstring stringArg2 = t.env->NewStringUTF(pszMessage);

        t.env->CallStaticVoidMethod(t.classID, t.methodID, stringArg1, stringArg2, nInputMode, nInputFlag, nReturnType, nMaxLength);

        t.env->DeleteLocalRef(stringArg1);
        t.env->DeleteLocalRef(stringArg2);
        t.env->DeleteLocalRef(t.classID);
    }
}

void showBaiduLoginDialog(BaiduLoginCallback pfBaiduLoginCallback, void* ctx){
	s_pfBaiduLoginCallback = pfBaiduLoginCallback;
	b_ctx = ctx;
	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "showBaiduLoginDialog", "()V")) {
		t.env->CallStaticVoidMethod(t.classID, t.methodID);
		t.env->DeleteLocalRef(t.classID);
	}
}

void playVideoJNI(const char* date){
	JniMethodInfo t;

	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "playVideo", "(Ljava/lang/String;)V")) {
		jstring stringArg = t.env->NewStringUTF(date);
		t.env->CallStaticVoidMethod(t.classID, t.methodID, stringArg);
		t.env->DeleteLocalRef(t.classID);
		t.env->DeleteLocalRef(stringArg);
	}
}

void showXunLeiLoginDialog(XunLeiLoginCallback pfXunLeiLoginCallback, void* ctx){
	s_pfXunLeiLoginCallback = pfXunLeiLoginCallback;
	x_ctx = ctx;
	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "showXunleiLoginDialog", "()V")) {
//		jstring stringArg1;
//
//		if (!account) {
//			stringArg1 = t.env->NewStringUTF("");
//		} else {
//			stringArg1 = t.env->NewStringUTF(account);
//		}
//		jstring stringArg2;
//		if (!password) {
//			stringArg2 = t.env->NewStringUTF("");
//		} else {
//			stringArg2 = t.env->NewStringUTF(password);
//		}
//		jstring stringArg3;
//		if (!yanzhengmaUrl) {
//			stringArg3 = t.env->NewStringUTF("");
//		} else {
//			stringArg3 = t.env->NewStringUTF(yanzhengmaUrl);
//		}
//		jint arg4 = 0;
//		if(hasYanZhengMa){
//			arg4 = 1;
//		}
//		t.env->CallStaticVoidMethod(t.classID, t.methodID, stringArg1, stringArg2, stringArg3, arg4);
		t.env->CallStaticVoidMethod(t.classID, t.methodID);

//		t.env->DeleteLocalRef(stringArg1);
//		t.env->DeleteLocalRef(stringArg2);
//		t.env->DeleteLocalRef(stringArg3);
		t.env->DeleteLocalRef(t.classID);
	}

}

void terminateProcessJNI() {
    JniMethodInfo t;

    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "terminateProcess", "()V")) {
        t.env->CallStaticVoidMethod(t.classID, t.methodID);
        t.env->DeleteLocalRef(t.classID);
    }
}

std::string getPackageNameJNI() {
    JniMethodInfo t;
    std::string ret("");

    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "getCocos2dxPackageName", "()Ljava/lang/String;")) {
        jstring str = (jstring)t.env->CallStaticObjectMethod(t.classID, t.methodID);
        t.env->DeleteLocalRef(t.classID);
        ret = JniHelper::jstring2string(str);
        t.env->DeleteLocalRef(str);
    }
    return ret;
}

std::string getFileDirectoryJNI() {
    JniMethodInfo t;
    std::string ret("");

    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "getCocos2dxWritablePath", "()Ljava/lang/String;")) {
        jstring str = (jstring)t.env->CallStaticObjectMethod(t.classID, t.methodID);
        t.env->DeleteLocalRef(t.classID);
        ret = JniHelper::jstring2string(str);
        t.env->DeleteLocalRef(str);
    }
    
    return ret;
}

std::string getCurrentLanguageJNI() {
    JniMethodInfo t;
    std::string ret("");
    
    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "getCurrentLanguage", "()Ljava/lang/String;")) {
        jstring str = (jstring)t.env->CallStaticObjectMethod(t.classID, t.methodID);
        t.env->DeleteLocalRef(t.classID);
        ret = JniHelper::jstring2string(str);
        t.env->DeleteLocalRef(str);
    }

    return ret;
}

void enableAccelerometerJNI() {
    JniMethodInfo t;

    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "enableAccelerometer", "()V")) {
        t.env->CallStaticVoidMethod(t.classID, t.methodID);
        t.env->DeleteLocalRef(t.classID);
    }
}

void setAccelerometerIntervalJNI(float interval) {
    JniMethodInfo t;

    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "setAccelerometerInterval", "(F)V")) {
        t.env->CallStaticVoidMethod(t.classID, t.methodID, interval);
        t.env->DeleteLocalRef(t.classID);
    }
}

void disableAccelerometerJNI() {
    JniMethodInfo t;

    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "disableAccelerometer", "()V")) {
        t.env->CallStaticVoidMethod(t.classID, t.methodID);
        t.env->DeleteLocalRef(t.classID);
    }
}

// functions for CCUserDefault
bool getBoolForKeyJNI(const char* pKey, bool defaultValue)
{
    JniMethodInfo t;
    
    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "getBoolForKey", "(Ljava/lang/String;Z)Z")) {
        jstring stringArg = t.env->NewStringUTF(pKey);
        jboolean ret = t.env->CallStaticBooleanMethod(t.classID, t.methodID, stringArg, defaultValue);
        
        t.env->DeleteLocalRef(t.classID);
        t.env->DeleteLocalRef(stringArg);
        
        return ret;
    }
    
    return defaultValue;
}

int getIntegerForKeyJNI(const char* pKey, int defaultValue)
{
    JniMethodInfo t;
    
    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "getIntegerForKey", "(Ljava/lang/String;I)I")) {
        jstring stringArg = t.env->NewStringUTF(pKey);
        jint ret = t.env->CallStaticIntMethod(t.classID, t.methodID, stringArg, defaultValue);
        
        t.env->DeleteLocalRef(t.classID);
        t.env->DeleteLocalRef(stringArg);
        
        return ret;
    }
    
    return defaultValue;
}

float getFloatForKeyJNI(const char* pKey, float defaultValue)
{
    JniMethodInfo t;
    
    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "getFloatForKey", "(Ljava/lang/String;F)F")) {
        jstring stringArg = t.env->NewStringUTF(pKey);
        jfloat ret = t.env->CallStaticFloatMethod(t.classID, t.methodID, stringArg, defaultValue);
        
        t.env->DeleteLocalRef(t.classID);
        t.env->DeleteLocalRef(stringArg);
        
        return ret;
    }
    
    return defaultValue;
}

double getDoubleForKeyJNI(const char* pKey, double defaultValue)
{
    JniMethodInfo t;
    
    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "getDoubleForKey", "(Ljava/lang/String;D)D")) {
        jstring stringArg = t.env->NewStringUTF(pKey);
        jdouble ret = t.env->CallStaticDoubleMethod(t.classID, t.methodID, stringArg);
        
        t.env->DeleteLocalRef(t.classID);
        t.env->DeleteLocalRef(stringArg);
        
        return ret;
    }
    
    return defaultValue;
}

std::string getStringForKeyJNI(const char* pKey, const char* defaultValue)
{
    JniMethodInfo t;
    std::string ret("");

    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "getStringForKey", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;")) {
        jstring stringArg1 = t.env->NewStringUTF(pKey);
        jstring stringArg2 = t.env->NewStringUTF(defaultValue);
        jstring str = (jstring)t.env->CallStaticObjectMethod(t.classID, t.methodID, stringArg1, stringArg2);
        ret = JniHelper::jstring2string(str);
        
        t.env->DeleteLocalRef(t.classID);
        t.env->DeleteLocalRef(stringArg1);
        t.env->DeleteLocalRef(stringArg2);
        t.env->DeleteLocalRef(str);
        
        return ret;
    }
    
    return defaultValue;
}

std::string getStringResouceByKeyJNI(const char* pKey)
{
	JniMethodInfo t;
	std::string ret("");

	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "getResouceString", "(Ljava/lang/String;)Ljava/lang/String;")) {
		jstring stringArg1 = t.env->NewStringUTF(pKey);
		jstring str = (jstring)t.env->CallStaticObjectMethod(t.classID, t.methodID, stringArg1);
		ret = JniHelper::jstring2string(str);

		t.env->DeleteLocalRef(t.classID);
		t.env->DeleteLocalRef(stringArg1);
		t.env->DeleteLocalRef(str);
	}

	return ret;
}
std::string getPincodeJNI() {
	JniMethodInfo t;
	std::string ret("");

	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "getPincode", "()Ljava/lang/String;")) {
		jstring str = (jstring)t.env->CallStaticObjectMethod(t.classID, t.methodID);
		ret = JniHelper::jstring2string(str);
		t.env->DeleteLocalRef(t.classID);
		t.env->DeleteLocalRef(str);
	}
	return ret;
}


//
//void setPincodeJNI(const char* date) {
//	JniMethodInfo t;
//
//	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "setPincode", "(Ljava/lang/String;)V")) {
//		jstring stringArg = t.env->NewStringUTF(date);
//		t.env->CallStaticVoidMethod(t.classID, t.methodID, stringArg);
//		t.env->DeleteLocalRef(t.classID);
//		t.env->DeleteLocalRef(stringArg);
//	}
//}


//
//void setChannelJNI(const char* date) {
//	JniMethodInfo t;
//
//	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "setChannel", "(Ljava/lang/String;)V")) {
//		jstring stringArg = t.env->NewStringUTF(date);
//		t.env->CallStaticVoidMethod(t.classID, t.methodID, stringArg);
//		t.env->DeleteLocalRef(t.classID);
//		t.env->DeleteLocalRef(stringArg);
//	}
//}



void startFayeService() {
	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "startService", "()V")) {
		t.env->CallStaticVoidMethod(t.classID, t.methodID);
		t.env->DeleteLocalRef(t.classID);
	}
}

extern void generatePincode(MainGeneratePincode pfXunLeiLoginCallback,
		void* ctx) {
	s_pfMainGeneratePincode = pfXunLeiLoginCallback;
	m_ctx = ctx;
	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "generatePincode", "()V")) {
		t.env->CallStaticVoidMethod(t.classID, t.methodID);
		t.env->DeleteLocalRef(t.classID);
	}
}

void* getErweimaDateJNI(const char* date, int width, unsigned int * pSize) {
	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "generateErweima", "(Ljava/lang/String;I)[B")) {
		jstring stringArg1 = t.env->NewStringUTF(date);
		jobject obj = t.env->CallStaticObjectMethod(t.classID, t.methodID,stringArg1,width);
		if(!obj){
			return NULL;
		}
		jbyteArray array = (jbyteArray)obj;
		t.env->DeleteLocalRef(t.classID);
		t.env->DeleteLocalRef(stringArg1);
		jsize  size = t.env->GetArrayLength(array);
		*pSize = size;
//		void* data = (void*)t.env->GetByteArrayElements(array, 0);

		if (size > 0) {
			jbyte * data = (jbyte*)t.env->GetByteArrayElements(array, 0);
			char* pBuf = (char*)malloc(size+1);
			if (pBuf != NULL) {
				memcpy(pBuf, data, size);
				pBuf[size] = '\0';
			}
			t.env->ReleaseByteArrayElements(array, data, 0);
			return pBuf;
		}else{
			return NULL;
		}
//		env->ReleaseByteArrayElements(array, data, 0);
//		AndroidBitmapInfo  info;
//		void* pixels;
//		int ret;
//		if ((ret = AndroidBitmap_getInfo(t.env, bitmap, &info)) < 0) {
//			LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
//			return NULL;
//		}
//		if ((ret = AndroidBitmap_lockPixels(t.env, bitmap, &pixels)) < 0) {
//			LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
//			return NULL;
//		}
//		AndroidBitmap_unlockPixels(t.env, bitmap);
//		return pixels;
	}else{
		return NULL;
	}
}

std::string getPlayHistoryListJNI() {
	JniMethodInfo t;
	std::string ret("");

	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "getPlayList", "()Ljava/lang/String;")) {
		jstring str = (jstring)t.env->CallStaticObjectMethod(t.classID, t.methodID);
		ret = JniHelper::jstring2string(str);
		t.env->DeleteLocalRef(t.classID);
		t.env->DeleteLocalRef(str);
	}
	return ret;
}

std::string fomartTime(int t) {
	if(t<=0){
		return "--:--:--";
	}else{
		int h = t/3600;
		int m = (t-h*3600)/60;
		int s = t%60;
		char url[32] = {};
		sprintf(url,"%02d:%02d:%02d",h,m,s);
		return string(url);
	}
}

void startSetting(SettingDilogCallback pfSettingDilogCallbackFunc,void* ctx){
	s_pfSettingCallBack = pfSettingDilogCallbackFunc;
	set_ctx = ctx;
	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "startSetting", "()V")) {
		t.env->CallStaticVoidMethod(t.classID, t.methodID);
		t.env->DeleteLocalRef(t.classID);
	}
}

void deleatePlayHistoryListJNI(const char* date) {
	JniMethodInfo t;

	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "deletePlayList", "(Ljava/lang/String;)V")) {
		jstring stringArg = t.env->NewStringUTF(date);
		t.env->CallStaticVoidMethod(t.classID, t.methodID, stringArg);
		t.env->DeleteLocalRef(t.classID);
		t.env->DeleteLocalRef(stringArg);
	}
}

std::string getErweimaUrlJNI() {
	JniMethodInfo t;
	std::string ret("");

	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "getErweimaUrl", "()Ljava/lang/String;")) {
		jstring str = (jstring)t.env->CallStaticObjectMethod(t.classID, t.methodID);
		ret = JniHelper::jstring2string(str);
		t.env->DeleteLocalRef(t.classID);
		t.env->DeleteLocalRef(str);
	}

	return ret;
}

void setUpdateQQCallback(MainUpdateQQCallback mainUpdateQQCallback,
		void* ctx) {
	m_ctx = ctx;
	s_pfMainUpdateQQCallback = mainUpdateQQCallback;
	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "updateQQ", "()V")) {
		t.env->CallStaticVoidMethod(t.classID, t.methodID);
		t.env->DeleteLocalRef(t.classID);
	}
}

std::string getQQNameJNI() {
	JniMethodInfo t;
	std::string ret("");

	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "getQQName", "()Ljava/lang/String;")) {
		jstring str = (jstring)t.env->CallStaticObjectMethod(t.classID, t.methodID);
		ret = JniHelper::jstring2string(str);
		t.env->DeleteLocalRef(t.classID);
		t.env->DeleteLocalRef(str);
	}
	return ret;
}

std::string getQQAvatarJNI() {
	JniMethodInfo t;
	std::string ret("");

	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "getQQAvatar", "()Ljava/lang/String;")) {
		jstring str = (jstring)t.env->CallStaticObjectMethod(t.classID, t.methodID);
		ret = JniHelper::jstring2string(str);
		t.env->DeleteLocalRef(t.classID);
		t.env->DeleteLocalRef(str);
	}
	return ret;
}

std::string getDecodeStringFromJNI(const char* pKey)
{
	JniMethodInfo t;
	std::string ret("");

	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "decode", "(Ljava/lang/String;)Ljava/lang/String;")) {
		jstring stringArg1 = t.env->NewStringUTF(pKey);
		jstring str = (jstring)t.env->CallStaticObjectMethod(t.classID, t.methodID, stringArg1);
		ret = JniHelper::jstring2string(str);

		t.env->DeleteLocalRef(t.classID);
		t.env->DeleteLocalRef(stringArg1);
		t.env->DeleteLocalRef(str);
	}

	return ret;
}

std::string getXunLeiUserInfoJNI()
{
	JniMethodInfo t;
	std::string ret("");
	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "getXunleiUserInfo", "()Ljava/lang/String;")) {

		jstring str = (jstring)t.env->CallStaticObjectMethod(t.classID, t.methodID);
		ret = JniHelper::jstring2string(str);
		t.env->DeleteLocalRef(t.classID);
		t.env->DeleteLocalRef(str);
	}

	return ret;
}
std::string getXunleiCookiesJNI()
{
	JniMethodInfo t;
	std::string ret("");
	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "getXunleiCookies", "()Ljava/lang/String;")) {

		jstring str = (jstring)t.env->CallStaticObjectMethod(t.classID, t.methodID);
		ret = JniHelper::jstring2string(str);
		t.env->DeleteLocalRef(t.classID);
		t.env->DeleteLocalRef(str);
	}
	return ret;
}
std::string getBaiduTokenJNI()
{
	JniMethodInfo t;
	std::string ret("");
	if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "getBaiduAccessToken", "()Ljava/lang/String;")) {

		jstring str = (jstring)t.env->CallStaticObjectMethod(t.classID, t.methodID);
		ret = JniHelper::jstring2string(str);
		t.env->DeleteLocalRef(t.classID);
		t.env->DeleteLocalRef(str);
	}
	return ret;
}

std::vector<std::string> stringSplit(std::string str,std::string pattern)
 {
     std::string::size_type pos;
     std::vector<std::string> result;
     str+=pattern;//扩展字符串以方便操作
     int size=str.size();

     for(int i=0; i<size; i++)
     {
         pos=str.find(pattern,i);
         if(pos<size)
         {
             std::string s=str.substr(i,pos-i);
             result.push_back(s);
             i=pos+pattern.size()-1;
         }
     }
     return result;
 }

std::string getCurrentTimeJNI()
{
	JniMethodInfo t;
		std::string ret("");
		if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "getCurrentTime", "()Ljava/lang/String;")) {

			jstring str = (jstring)t.env->CallStaticObjectMethod(t.classID, t.methodID);
			ret = JniHelper::jstring2string(str);
			t.env->DeleteLocalRef(t.classID);
			t.env->DeleteLocalRef(str);
		}
		return ret;
}

void setBoolForKeyJNI(const char* pKey, bool value)
{
    JniMethodInfo t;
    
    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "setBoolForKey", "(Ljava/lang/String;Z)V")) {
        jstring stringArg = t.env->NewStringUTF(pKey);
        t.env->CallStaticVoidMethod(t.classID, t.methodID, stringArg, value);
        
        t.env->DeleteLocalRef(t.classID);
        t.env->DeleteLocalRef(stringArg);
    }
}

void setIntegerForKeyJNI(const char* pKey, int value)
{
    JniMethodInfo t;
    
    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "setIntegerForKey", "(Ljava/lang/String;I)V")) {
        jstring stringArg = t.env->NewStringUTF(pKey);
        t.env->CallStaticVoidMethod(t.classID, t.methodID, stringArg, value);
        
        t.env->DeleteLocalRef(t.classID);
        t.env->DeleteLocalRef(stringArg);
    }
}

void setFloatForKeyJNI(const char* pKey, float value)
{
    JniMethodInfo t;
    
    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "setFloatForKey", "(Ljava/lang/String;F)V")) {
        jstring stringArg = t.env->NewStringUTF(pKey);
        t.env->CallStaticVoidMethod(t.classID, t.methodID, stringArg, value);
        
        t.env->DeleteLocalRef(t.classID);
        t.env->DeleteLocalRef(stringArg);
    }
}

void setDoubleForKeyJNI(const char* pKey, double value)
{
    JniMethodInfo t;
    
    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "setDoubleForKey", "(Ljava/lang/String;D)V")) {
        jstring stringArg = t.env->NewStringUTF(pKey);
        t.env->CallStaticVoidMethod(t.classID, t.methodID, stringArg, value);
        
        t.env->DeleteLocalRef(t.classID);
        t.env->DeleteLocalRef(stringArg);
    }
}

void setStringForKeyJNI(const char* pKey, const char* value)
{
    JniMethodInfo t;
    
    if (JniHelper::getStaticMethodInfo(t, CLASS_NAME, "setStringForKey", "(Ljava/lang/String;Ljava/lang/String;)V")) {
        jstring stringArg1 = t.env->NewStringUTF(pKey);
        jstring stringArg2 = t.env->NewStringUTF(value);
        t.env->CallStaticVoidMethod(t.classID, t.methodID, stringArg1, stringArg2);
        
        t.env->DeleteLocalRef(t.classID);
        t.env->DeleteLocalRef(stringArg1);
        t.env->DeleteLocalRef(stringArg2);
    }
}
