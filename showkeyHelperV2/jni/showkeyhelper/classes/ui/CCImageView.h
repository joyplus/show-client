#ifndef _JOYPLUS_CCIMAGEVIEW_H_
#define _JOYPLUS_CCIMAGEVIEW_H_
#include "cocos2d.h"
#include "cocos-ext.h"
#include "HttpRequest.h"
#include "HttpClient.h"
#include <vector>
#include <string.h>
#include <algorithm>
#include "Log.h"
#include "platform/android/jni/Java_org_cocos2dx_lib_Cocos2dxHelper.h"
using namespace std;
USING_NS_CC_EXT;
USING_NS_CC;

class CCImageViewDownLoadDelegte{
public:
	virtual void onResult(const char* url, bool isSucced) = 0;
};

class CCImageView : public cocos2d::CCSprite
{
public:
	static CCImageView* createWithLocalPath(const char *path);
	static CCImageView* createWithNetUrl(const char *url, const char *default_local_path, CCSize boundsize);
	static CCImageView* createWithNetUrl(const char *url, const char *default_local_path, CCSize boundsize, bool isSave,CCImageViewDownLoadDelegte * delegte);
	void onDownLoadComplete(CCNode* node,CCObject* obj);
	CCSize getBoundSize();
	void setBoundSize(CCSize size);
	bool initWithUrl(const char *url,const char *default_local_path);
	bool initWithUrl(const char *url,const char *default_local_path,bool isSave);
	bool isSave;
	CCImageViewDownLoadDelegte * delegere;
private:
	string getFileNameFromUrl(const char * url);
	bool initWithDownLoadFile(const char * filePath);
//	std::vector<std::string> split(std::string str,std::string pattern);
	CCSize m_size;

};
#endif
