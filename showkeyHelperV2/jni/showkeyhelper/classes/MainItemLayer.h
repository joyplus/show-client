#ifndef _JOYPLUS_MAINITEMLAYER_H_
#define _JOYPLUS_MAINITEMLAYER_H_

#include "cocos2d.h"
#include "cocos-ext.h"
#include "Log.h"
USING_NS_CC_EXT;
USING_NS_CC;

class MainItemLayer : public CCLayer{

public:
	virtual ~MainItemLayer();
	virtual void setSelected(bool isSelected);
	static MainItemLayer* create(const char* path_back,const char *path_img,const char *path_logo);

private:
	virtual bool init(const char* path_back,const char *path_img,const char *path_logo);
	void runBreath(CCTime dt);
	void endOfBreath(CCTime dt);
	CCSprite * m_background;
};

#endif /* _JOYPLUS_MAINITEMLAYER_H_ */
