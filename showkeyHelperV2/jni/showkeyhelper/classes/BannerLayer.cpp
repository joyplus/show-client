#include "BannerLayer.h"

BannerLayer::~BannerLayer() {
	// TODO Auto-generated destructor stub
}

bool BannerLayer::init() {
	bool res = false;
	do
	{
		m_background = CCSprite::create("selected1_user.png");
		m_background->setPosition(CCPointZero);
		m_background->setOpacity(0);
		this->addChild(m_background);

		CCSprite * image = CCSprite::create("back1_banner.png");
		image->setPosition(CCPointZero);
		this->addChild(image);

		m_background->setOpacity(0);
		setContentSize(image->getContentSize());
		res = true;
	} while (0);

	// return the scene
	return res;
}

void BannerLayer::setSelected(bool isSelected) {
	unscheduleAllSelectors();
	m_background->stopAllActions();
	if(isSelected){
		CCActionInterval* fadeIn = CCFadeIn::create(0.4f);
		m_background->runAction(fadeIn);
		this->schedule(schedule_selector(BannerLayer::runBreath),8.0f);
	}else{
		CCActionInterval* fadeOut = CCFadeOut::create(0.4f);
		m_background->runAction(fadeOut);
	}
}

void BannerLayer::displayErWeiMa(const char* str) {
	removeChildByTag(111);
	unsigned int size = 0;
	void* date = getErweimaDateJNI(str, 320, &size);
	if(date){
		CCImage * image = new CCImage();
		image->initWithImageData(date,size);
		free(date);
		CCTexture2D* texture = new cocos2d::CCTexture2D();
		bool isImg = texture->initWithImage(image);
		image->release();
		if(isImg){
			CCSprite *s = CCSprite::createWithTexture(texture);
			s->setTag(111);
			s->setPosition(ccp(40,-3));
			addChild(s);
		}
		texture->release();
	}
}

void BannerLayer::runBreath(CCTime dt) {
	CCActionInterval* fadeTo = CCFadeTo::create(0.5f,127);
	CCActionInterval* fadeToBack = CCFadeTo::create(0.5f,255);
//	CCActionInterval* fadeIn = fadeTo->reverse();
	m_background->runAction(CCSequence::create(fadeTo,fadeToBack,NULL));
}


