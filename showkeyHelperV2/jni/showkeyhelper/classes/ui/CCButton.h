#ifndef _JOYPLUS_CCBUTTON_H_
#define _JOYPLUS_CCBUTTON_H_
#include "cocos2d.h"
USING_NS_CC;
namespace joyplus {

class CCButton : public CCLayer{
public:
	virtual ~CCButton();
	void setSelected(bool selected);
	static CCButton* create(const char* path, const char* highlight_path, const char* label,float paddingL,float paddingT,float paddingR,float paddingB);
	static CCButton* create(const char* path, const char* highlight_path, const char* label);
	bool init(const char* path, const char* highlight_path, const char* label, float paddingL,float paddingT,float paddingR,float paddingB);

private:
	CCSprite* m_selected_back;
	CCSprite* m_normal_back;
	CCLabelTTF* label_name;
};

} /* namespace joyplus */
#endif /* _JOYPLUS_CCBUTTON_H_ */
