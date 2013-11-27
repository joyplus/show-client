#ifndef _JOYPLUS_TRANSITIONSCENES_H_
#define _JOYPLUS_TRANSITIONSCENES_H_
#include "cocos2d.h"
#include "cocos-ext.h"
#include "Log.h"
USING_NS_CC;
USING_NS_CC_EXT;
class CC_DLL TransitionSceneSaleFadeOut : public CCTransitionScene{
public:
	virtual ~TransitionSceneSaleFadeOut();
	virtual void onEnter();
	virtual void onExit();
	static TransitionSceneSaleFadeOut* create(float t, CCScene* scene);
	static void runActionAllChild(CCActionInterval*action,CCNode* node);
};

class CC_DLL TransitionSceneSaleFadeIn : public CCTransitionScene{
public:
	virtual ~TransitionSceneSaleFadeIn();
	virtual void onEnter();
	static TransitionSceneSaleFadeIn* create(float t, CCScene* scene);
};

#endif /* _JOYPLUS_TRANSITIONSCENES_H_ */
