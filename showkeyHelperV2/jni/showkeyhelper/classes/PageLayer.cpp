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
	        m_seletedTag = TAG_PINCODE;
	        CCSize size = CCDirector::sharedDirector()->getWinSize();

	        PincodeLayer * pincode = PincodeLayer::create();
			pincode->ignoreAnchorPointForPosition(true);
			pincode->setAnchorPoint(ccp(0.5,0.5));
			pincode->setPosition(ccp(PADING_LEFT, size.height/2-pincode->getContentSize().height/2));
			pincode->setTag(TAG_PINCODE);
			pincode->setSelected(true);
			pincode->setZOrder(1);
			this->addChild(pincode);

//	        CCSprite * jingxi = CCSprite::create("back3_surprise.png");
//	        jingxi->setTag(TAG_JINGXI);
//	        jingxi->setPosition(ccp(PADING_LEFT+10+pincode->getContentSize().width+jingxi->getContentSize().width/2, size.height/2));
//	        this->addChild(jingxi);

	        MainItemLayer * jingxi = MainItemLayer::create("selected3_surprise.png","back3_surprise.png","icon3_surprise.png");
//	        jingxi->setAnchorPoint(CCPointZero);
	        jingxi->setPosition(ccp(PADING_LEFT+pincode->getContentSize().width+jingxi->getContentSize().width/2, size.height/2));
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
	PincodeLayer *pincode = (PincodeLayer *)getChildByTag(TAG_PINCODE);
	MainItemLayer *history = (MainItemLayer *)getChildByTag(TAG_HISTORY);
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
					break;
				case TAG_JINGXI:
					jingxi->setSelected(false);
					pincode->setSelected(true);
					jingxi->setZOrder(0);
					pincode->setZOrder(1);
					m_seletedTag = TAG_PINCODE;
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
			break;
		case ccKeypadMSGType(kTypeRightArrowClicked):
				switch(m_seletedTag)
					{
						case TAG_PINCODE:
							pincode->setSelected(false);
							jingxi->setSelected(true);
							pincode->setZOrder(0);
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

PageLayer::~PageLayer()
{

}

