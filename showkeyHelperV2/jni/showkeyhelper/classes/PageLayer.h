#ifndef __JOYPLUS_PAGELAYER_H__
#define __JOYPLUS_PAGELAYER_H__

#include "cocos2d.h"
#include "cocos-ext.h"
#include "Log.h"
#include "PincodeLayer.h"
#include "MainItemLayer.h"
USING_NS_CC_EXT;
USING_NS_CC;

enum SPRITE_TAG
{
	TAG_PINCODE =0,
	TAG_JINGXI ,
	TAG_HISTORY,
	TAG_XUNLEI ,
	TAG_BAIDU
};

#define PADING_LEFT 160

class PageLayerItemClickDelegate
{
public:
	virtual void onPageItemClick(int itemTag) = 0;
};

class PageLayer : public CCLayer
{
public:
	virtual bool init();
	void setItemClickDelegate(PageLayerItemClickDelegate * itemClickDelegate);
	bool onKeyArrowClicked(int arrow);//方向键
	bool onKeyEnterClicked();//回车键
	void setPincode(const char* pincode);
	virtual ~PageLayer();
	CREATE_FUNC(PageLayer);
private:
	int m_seletedTag;
	PageLayerItemClickDelegate * itemClickDelegate;
};

#endif
