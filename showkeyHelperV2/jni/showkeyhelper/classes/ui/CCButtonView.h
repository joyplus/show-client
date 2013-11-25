#ifndef _JOYPLUS_CCBUTTONVIEW_H_
#define _JOYPLUS_CCBUTTONVIEW_H_
#include "cocos2d.h"
#include "cocos-ext.h"
#include "Log.h"
#include <string.h>
USING_NS_CC_EXT;
USING_NS_CC;
using namespace std;
class CCButtonView : public CCLayer{
public:
	virtual ~CCButtonView();
	void setSelected(bool selected);
	static CCButtonView* create(const char* path, const char* highlight_path, const char* label,float paddingL,float paddingT,float paddingR,float paddingB);
	bool init(const char* path, const char* label, const char* highlight_path, float paddingL,float paddingT,float paddingR,float paddingB);
private :
	CCSprite* m_background;
	void runBreath(CCTime dt);
};

#endif /* _JOYPLUS_CCBUTTONVIEW_H_ */
