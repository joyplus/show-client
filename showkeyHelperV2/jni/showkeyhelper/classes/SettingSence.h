#ifndef _JOYPLUS_SETTINGSENCE_H_
#define _JOYPLUS_SETTINGSENCE_H_
#include "cocos2d.h"
#include "cocos-ext.h"
#include "Log.h"
#include "platform/android/jni/Java_org_cocos2dx_lib_Cocos2dxHelper.h"
USING_NS_CC_EXT;
USING_NS_CC;
class SettingSence :  public cocos2d::CCLayer{
public:
	virtual ~SettingSence();
	void popSence();
	virtual bool init();
	static cocos2d::CCScene* scene();
	CREATE_FUNC(SettingSence);
	void onEnterTransitionDidFinish();
	void onExitTransitionDidStart();
};

#endif /* _JOYPLUS_SETTINGSENCE_H_ */
