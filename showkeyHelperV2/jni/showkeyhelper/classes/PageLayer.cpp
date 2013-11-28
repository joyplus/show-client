#include "PageLayer.h"

bool PageLayer::init()
{
	bool bRet = false;
	    do
	    {
	        if(! CCLayer::init())
			{
	        	LOGD("PageLayer","CCLayer init fail");
	        	break;
			}
	        m_seletedTag = TAG_BANNER;
	        CCSize size = CCDirector::sharedDirector()->getWinSize();

	        PincodeLayer * pincode = PincodeLayer::create();
			pincode->ignoreAnchorPointForPosition(true);
//			pincode->setAnchorPoint(ccp(0.5,0.5));
			pincode->setPosition(ccp(PADING_LEFT, size.height/2-pincode->getContentSize().height/2));
			pincode->setTag(TAG_PINCODE);
//			pincode->setSelected(true);
			pincode->setZOrder(1);
			this->addChild(pincode);

			CCButtonView * settingButton = CCButtonView::create("button_setting.png","selected6_button.png","设置",50,0,0,0);
			settingButton->setAnchorPoint(ccp(0.5,0.5));
			settingButton->setTag(TAG_BUTTON_SETTING);
			settingButton->setPosition(ccp(PADING_LEFT+settingButton->getContentSize().width/2,pincode->getPositionY()+settingButton->getContentSize().height/2));
			this->addChild(settingButton);

			CCButtonView * refreshButton = CCButtonView::create("button_refresh.png","selected6_button.png","刷新PIN码",50,0,0,0);
			refreshButton->setAnchorPoint(ccp(0.5,0.5));
			refreshButton->setTag(TAG_BUTTON_REFRESH);
			refreshButton->setPosition(ccp(PADING_LEFT+settingButton->getContentSize().width/2,-14+pincode->getPositionY()+(refreshButton->getContentSize().height+settingButton->getContentSize().height/2)));
			this->addChild(refreshButton);

//	        CCSprite * jingxi = CCSprite::create("back3_surprise.png");
//	        jingxi->setTag(TAG_JINGXI);
//	        jingxi->setPosition(ccp(PADING_LEFT+10+pincode->getContentSize().width+jingxi->getContentSize().width/2, size.height/2));
//	        this->addChild(jingxi);

			BannerLayer * banner = BannerLayer::create();
			banner->setPosition(ccp(PADING_LEFT+pincode->getContentSize().width+banner->getContentSize().width/2, size.height/2));
			banner->setTag(TAG_BANNER);
			banner->setSelected(true);
			this->addChild(banner);

	        MainItemLayer * jingxi = MainItemLayer::create("selected3_surprise.png","back3_surprise.png","icon3_surprise.png");
//	        jingxi->setAnchorPoint(CCPointZero);
	        jingxi->setPosition(ccp(PADING_LEFT+pincode->getContentSize().width + banner->getContentSize().width +jingxi->getContentSize().width/2, size.height/2));
	        jingxi->setTag(TAG_JINGXI);
	        this->addChild(jingxi);

//	        CCSprite * history = CCSprite::create("back2_history.png");
	        MainItemLayer * history = MainItemLayer::create("selected2_history.png","back2_history.png","icon2_history.png");
	        history->setTag(TAG_HISTORY);
	        history->setPosition(ccp(jingxi->getPositionX()+history->getContentSize().width, size.height/2));
	        this->addChild(history);

//	        CCSprite * xunlei = CCSprite::create("back4_xunlei.png");
	        MainItemLayer * xunlei = MainItemLayer::create("selected4_xunlei.png","back4_xunlei.png","icon4_xunlei.png");
	        xunlei->setTag(TAG_XUNLEI);
	        xunlei->setPosition(ccp(history->getPositionX()+xunlei->getContentSize().width, size.height/2));
	        this->addChild(xunlei);

//	        CCSprite * baidu = CCSprite::create("back5_baidu.png");
	        MainItemLayer * baidu = MainItemLayer::create("selected5_baidu.png","back5_baidu.png","icon5_baidu.png");
	        baidu->setTag(TAG_BAIDU);
	        baidu->setPosition(ccp(xunlei->getPositionX()+baidu->getContentSize().width, size.height/2));
	        this->addChild(baidu);

//	        CCScaleTo* scaleBig = CCScaleTo::create(0.3f,
//	        						(pincode->getContentSize().width+20)/pincode->getContentSize().width,
//	        						(pincode->getContentSize().height+20)/pincode->getContentSize().height);
//	        pincode->runAction(scaleBig);
//	        pincode->setZOrder(1);
	        bRet = true;
	    } while (0);
	    return bRet;
}

bool PageLayer::onKeyArrowClicked(int arrow)
{
	bool flag = false;
	MainItemLayer *baidu = (MainItemLayer *)getChildByTag(TAG_BAIDU);
	MainItemLayer *xunlei = (MainItemLayer *)getChildByTag(TAG_XUNLEI);
	MainItemLayer *jingxi = (MainItemLayer *)getChildByTag(TAG_JINGXI);
	BannerLayer *banner = (BannerLayer *)getChildByTag(TAG_BANNER);
	PincodeLayer *pincode = (PincodeLayer *)getChildByTag(TAG_PINCODE);
	MainItemLayer *history = (MainItemLayer *)getChildByTag(TAG_HISTORY);
	CCButtonView * refreshBtn = (CCButtonView *)getChildByTag(TAG_BUTTON_REFRESH);
	CCButtonView * settingBtn = (CCButtonView *)getChildByTag(TAG_BUTTON_SETTING);
//
//	CCScaleTo* scaleBig = CCScaleTo::create(0.2f,
//								(xunlei->getContentSize().width+20)/xunlei->getContentSize().width,
//								(xunlei->getContentSize().height+20)/xunlei->getContentSize().height);
//
//	CCScaleTo* scaleBig_pincode = CCScaleTo::create(0.2f,
//		        						(pincode->getContentSize().width+20)/pincode->getContentSize().width,
//		        						(pincode->getContentSize().height+20)/pincode->getContentSize().height);
//	CCScaleTo* scaleSmall = CCScaleTo::create(0.1f,
//									1.0f,
//									1.0f);
	CCSize size = CCDirector::sharedDirector()->getWinSize();
//
	CCActionInterval* actionMove;

	switch(arrow)
	{
		case ccKeypadMSGType(kTypeLeftArrowClicked):
			switch(m_seletedTag)
			{
				case TAG_PINCODE:
				case TAG_BUTTON_REFRESH:
				case TAG_BUTTON_SETTING:
					break;
				case TAG_BANNER:
					banner->setSelected(false);
					refreshBtn->setSelected(true);
					banner->setZOrder(0);
					refreshBtn->setZOrder(1);
					m_seletedTag = TAG_BUTTON_REFRESH;
//					actionMove = CCMoveBy::create(0.2f,
//											CCPointMake(jingxi->getPosition().x-size.width/2, 0));
//					this->runAction(actionMove);
					flag = true;
					break;
				case TAG_JINGXI:
					jingxi->setSelected(false);
					banner->setSelected(true);
					jingxi->setZOrder(0);
					banner->setZOrder(1);
					m_seletedTag = TAG_BANNER;
					actionMove = CCMoveBy::create(0.2f,
											CCPointMake(jingxi->getPosition().x-size.width/2, 0));
					this->runAction(actionMove);
					flag = true;
					break;
				case TAG_HISTORY:
					history->setSelected(false);
					jingxi->setSelected(true);
					history->setZOrder(0);
					jingxi->setZOrder(1);
					m_seletedTag = TAG_JINGXI;
					actionMove = CCMoveBy::create(0.2f,
											CCPointMake(jingxi->getContentSize().width, 0));
					this->runAction(actionMove);
					flag = true;
					break;
				case TAG_XUNLEI:
					xunlei->setSelected(false);
					history->setSelected(true);
					xunlei->setZOrder(0);
					history->setZOrder(1);
					m_seletedTag = TAG_HISTORY;
					flag = true;
					break;
				case TAG_BAIDU:
					baidu->setSelected(false);
					xunlei->setSelected(true);
					baidu->setZOrder(0);
					xunlei->setZOrder(1);
					m_seletedTag = TAG_XUNLEI;
					flag = true;
					break;
			}
			break;
		case ccKeypadMSGType(kTypeUpArrowClicked):
				switch(m_seletedTag)
				{
					case TAG_BUTTON_SETTING:
						refreshBtn->setSelected(true);
						refreshBtn->setZOrder(1);
						settingBtn->setSelected(false);
						settingBtn->setZOrder(0);
						m_seletedTag = TAG_BUTTON_REFRESH;
						flag = true;
						break;
				}
			break;
		case ccKeypadMSGType(kTypeRightArrowClicked):
				switch(m_seletedTag)
					{
						case TAG_PINCODE:
						case TAG_BUTTON_REFRESH:
							refreshBtn->setSelected(false);
							banner->setSelected(true);
							refreshBtn->setZOrder(0);
							banner->setZOrder(1);
//							actionMove = CCMoveBy::create(0.2f,
//													CCPointMake(-(jingxi->getPosition().x-size.width/2), 0));
//							this->runAction(actionMove);
							m_seletedTag = TAG_BANNER;
							flag = true;
							break;
						case TAG_BUTTON_SETTING:
							settingBtn->setSelected(false);
							banner->setSelected(true);
							settingBtn->setZOrder(0);
							banner->setZOrder(1);
//							actionMove = CCMoveBy::create(0.2f,
//													CCPointMake(-(jingxi->getPosition().x-size.width/2), 0));
//							this->runAction(actionMove);
							m_seletedTag = TAG_BANNER;
							flag = true;
							break;
						case TAG_BANNER:
							banner->setSelected(false);
							jingxi->setSelected(true);
							banner->setZOrder(0);
							jingxi->setZOrder(1);
							actionMove = CCMoveBy::create(0.2f,
													CCPointMake(-(jingxi->getPosition().x-size.width/2), 0));
							this->runAction(actionMove);
							m_seletedTag = TAG_JINGXI;
							flag = true;
							break;
						case TAG_JINGXI:
							jingxi->setSelected(false);
							history->setSelected(true);
							jingxi->setZOrder(0);
							history->setZOrder(1);
							actionMove = CCMoveBy::create(0.2f,
													CCPointMake(-jingxi->getContentSize().width, 0));
							this->runAction(actionMove);
							m_seletedTag = TAG_HISTORY;
							flag = true;
							break;
						case TAG_HISTORY:
							history->setSelected(false);
							xunlei->setSelected(true);
							history->setZOrder(0);
							xunlei->setZOrder(1);
							m_seletedTag = TAG_XUNLEI;
							flag = true;
							break;
						case TAG_XUNLEI:
							xunlei->setSelected(false);
							baidu->setSelected(true);
							xunlei->setZOrder(0);
							baidu->setZOrder(1);
							m_seletedTag = TAG_BAIDU;
							flag = true;
							break;
						case TAG_BAIDU:
							flag = true;
							break;
					}
			break;
		case ccKeypadMSGType(kTypeDownArrowClicked):
				switch(m_seletedTag)
					{
						case TAG_BUTTON_REFRESH:
							settingBtn->setSelected(true);
							settingBtn->setZOrder(1);
							refreshBtn->setSelected(false);
							refreshBtn->setZOrder(0);
							m_seletedTag = TAG_BUTTON_SETTING;
							flag = true;
							break;
					}
			break;
	}
	return flag;
}

bool PageLayer::onKeyEnterClicked()
{
	bool flag = false;
	if(itemClickDelegate!=NULL)
	{
		itemClickDelegate->onPageItemClick(m_seletedTag);
	}
	return flag;
}

void PageLayer::setItemClickDelegate(PageLayerItemClickDelegate *itemClickDelagate)
{
	this->itemClickDelegate = itemClickDelagate;
}

void PageLayer::setPincode(const char* pincode) {
	PincodeLayer* pincode_layer = (PincodeLayer *)getChildByTag(TAG_PINCODE);
	pincode_layer->setPincode(pincode);
}

void PageLayer::displayErWeiMa(const char* str) {
	BannerLayer* banner = (BannerLayer*)getChildByTag(TAG_BANNER);
	banner->displayErWeiMa(str);
}

void PageLayer::updateQQDisplay(const char* name, const char* url) {
	PincodeLayer *pincode = (PincodeLayer *)getChildByTag(TAG_PINCODE);
	pincode->updateQQDisplay(name,url);
}

PageLayer::~PageLayer()
{

}

