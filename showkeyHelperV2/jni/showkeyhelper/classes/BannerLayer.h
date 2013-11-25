#ifndef _JOYPLUS_BANNERLAYER_H_
#define _JOYPLUS_BANNERLAYER_H_

#include "cocos2d.h"
#include "cocos-ext.h"
#include "Log.h"
#include "platform/android/jni/Java_org_cocos2dx_lib_Cocos2dxHelper.h"
USING_NS_CC_EXT;
USING_NS_CC;

class BannerLayer : public CCLayer{
public:
	virtual ~BannerLayer();
	virtual bool init();
	CREATE_FUNC(BannerLayer);
	virtual void setSelected(bool isSelected);
	void displayErWeiMa(const char* date);

private:
	void runBreath(CCTime dt);
	CCSprite * m_background;
};

#endif /* _JOYPLUS_BANNERLAYER_H_ */
