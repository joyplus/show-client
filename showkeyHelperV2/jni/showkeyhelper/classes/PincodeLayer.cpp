#include "PincodeLayer.h"

bool PincodeLayer::init()
{
	bool bRet = false;
		do
		{
			if(! CCLayer::init())
			{
				LOGD("PincodeLayer","CCLayer init fail");
				break;
			}

			m_background = CCSprite::create("selected1_user.png");
			m_background->setPosition(ccp(522,274));
			m_background->setOpacity(0);
			this->addChild(m_background);

			CCImageView * imag_touxiang = CCImageView::createWithNetUrl("http://avatar.cavatar.11111.jpg","defaultphoto.png",CCSizeMake(195,195));
			imag_touxiang->setPosition(ccp(160,357));
			addChild(imag_touxiang);


			CCSprite * sprite  = CCSprite::create("back1_user.png");
			sprite->setAnchorPoint(ccp(0,0));
			sprite->setPosition(ccp(0,0));
			addChild(sprite);

			CCSprite * light = CCSprite::create("light.png");
			light->setPosition(ccp(160,357));
			addChild(light);
			light->runAction(CCRepeatForever::create(CCRotateBy::create(0.1f,36.0f)));

			CCSprite * sprite_notice  = CCSprite::create("back1_banner.png");
			sprite_notice->setAnchorPoint(ccp(0,0));
			sprite_notice->setPosition(ccp(sprite->getContentSize().width,0));
			addChild(sprite_notice);

			setContentSize(CCSizeMake(sprite->getContentSize().width+sprite_notice->getContentSize().width, sprite->getContentSize().height));

			m_uiLayer = UILayer::create();
			m_uiLayer->scheduleUpdate();
			addChild(m_uiLayer);



//			UILabel *label_notice_pushed = UILabel::create();
//			label_notice_pushed->setFocused(false);
//			label_notice_pushed->setText(getStringResouceByKeyJNI("main_pushed").c_str());
////			label_notice_pushed->setAnchorPoint(ccp(0,0));
//			label_notice_pushed->setFontSize(18);
//			label_notice_pushed->setPosition(ccp(60,55));
//			m_uiLayer->addWidget(label_notice_pushed);
//
//			UILabel *label_notice_see = UILabel::create();
//			label_notice_see->setFocused(false);
//			label_notice_see->setText(getStringResouceByKeyJNI("main_see").c_str());
////			label_notice_see->setAnchorPoint(ccp(0,0));
//			label_notice_see->setFontSize(18);
//			label_notice_see->setPosition(ccp(150,55));
//			m_uiLayer->addWidget(label_notice_see);
//
//
//			UILabel *label_pushed_count = UILabel::create();
//			label_pushed_count->setFocused(false);
//			label_pushed_count->setText("123");
////			label_pushed_count->setAnchorPoint(ccp(0.5,0.5));
//			label_pushed_count->setFontSize(18);
//			label_pushed_count->setPosition(ccp(60,25));
//			m_uiLayer->addWidget(label_pushed_count);
//
//
//			UILabel *label_see_count = UILabel::create();
//			label_see_count->setFocused(false);
//			label_see_count->setText("0");
//			label_see_count->setFontSize(18);
////			label_see_count->setAnchorPoint(ccp(0,0));
//			label_see_count->setPosition(ccp(150,25));
//			m_uiLayer->addWidget(label_see_count);
//
//			UILabel *label_divider = UILabel::create();
//			label_divider->setFocused(false);
//			label_divider->setText("l");
//			label_divider->setFontSize(36);
////			label_divider->setAnchorPoint(ccp(0,0));
//			label_divider->setPosition(ccp(115,40));
//			m_uiLayer->addWidget(label_divider);


			UILabel *label_pincode = UILabel::create();
			label_pincode->setFocused(false);
			label_pincode->setText(("PIN:"+getStringForKeyJNI("pincode","")).c_str());
			label_pincode->setFontSize(27);
			label_pincode->setPosition(ccp(155,105));
			m_uiLayer->addWidget(label_pincode);


			UILabel *label_name = UILabel::create();
			label_name->setFocused(false);
			label_name->setText("QQ");
			label_name->setFontSize(30);
//			label_divider->setAnchorPoint(ccp(0,0));
			label_name->setPosition(ccp(155,195));
			m_uiLayer->addWidget(label_name);
			bRet = true;
		} while (0);
		return bRet;
}

void PincodeLayer::setSelected(bool isSelected) {
//	m_background->setVisible(isSelected);
	unscheduleAllSelectors();
	m_background->stopAllActions();
	if(isSelected){
		CCActionInterval* fadeIn = CCFadeIn::create(0.4f);
		m_background->runAction(fadeIn);
		schedule(schedule_selector(PincodeLayer::runBreath),8.0f);
	}else{
		CCActionInterval* fadeOut = CCFadeOut::create(0.4f);
		m_background->runAction(fadeOut);
	}
}
void PincodeLayer::runBreath(CCTime dt) {
//	LOGD("MainItemLayer","runBreath");
	CCActionInterval* fadeTo = CCFadeTo::create(0.5f,127);
	CCActionInterval* fadeToBack = CCFadeTo::create(0.5f,255);
//	CCActionInterval* fadeIn = fadeTo->reverse();
	m_background->runAction(CCSequence::create(fadeTo,fadeToBack,NULL));
}

