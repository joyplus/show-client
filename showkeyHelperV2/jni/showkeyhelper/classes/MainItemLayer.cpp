#include "MainItemLayer.h"

MainItemLayer::~MainItemLayer() {
	// TODO Auto-generated destructor stub
}

bool MainItemLayer::init(const char* path_back,const char *path_img,const char *path_logo)
{
	m_background = CCSprite::create(path_back);
	m_background->setPosition(CCPointZero);
	this->addChild(m_background);

	CCSprite * image = CCSprite::create(path_img);
	image->setPosition(CCPointZero);
	this->addChild(image);

	CCSprite * logo = CCSprite::create(path_logo);
	logo->setPosition(ccp(0,50));
	this->addChild(logo);

//	m_background->setVisible(false);
	m_background->setOpacity(0);
	setContentSize(image->getContentSize());
}

void MainItemLayer::setSelected(bool isSelected) {
//	m_background->setVisible(isSelected);
	unscheduleAllSelectors();
	m_background->stopAllActions();
	if(isSelected){
		CCActionInterval* fadeIn = CCFadeIn::create(0.4f);
		m_background->runAction(fadeIn);
		this->schedule(schedule_selector(MainItemLayer::runBreath),8.0f);
	}else{
		CCActionInterval* fadeOut = CCFadeOut::create(0.4f);
		m_background->runAction(fadeOut);
	}
}

MainItemLayer* MainItemLayer::create(const char* path_back,const char *path_img,const char *path_logo)
{
		MainItemLayer *pRet = new MainItemLayer();
		if (pRet && pRet->init(path_back,path_img,path_logo))
		{
			pRet->autorelease();
			return pRet;
		}
		else
		{
			delete pRet;
			pRet = 0;
			return 0;
		}
}

void MainItemLayer::runBreath(CCTime dt) {
//	LOGD("MainItemLayer","runBreath");
	CCActionInterval* fadeTo = CCFadeTo::create(0.5f,127);
	CCActionInterval* fadeToBack = CCFadeTo::create(0.5f,255);
//	CCActionInterval* fadeIn = fadeTo->reverse();
	m_background->runAction(CCSequence::create(fadeTo,fadeToBack,NULL));
}

void MainItemLayer::endOfBreath(CCTime dt) {
}





