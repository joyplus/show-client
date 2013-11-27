#include "CCButtonView.h"

CCButtonView::~CCButtonView() {
	// TODO Auto-generated destructor stub
}

CCButtonView* CCButtonView::create(const char* path, const char* highlight_path,const char* label,float paddingL,float paddingT,float paddingR,float paddingB) {
	CCButtonView *pRet = new CCButtonView();
	if (pRet && pRet->init(path,highlight_path,label,paddingL,paddingT,paddingR,paddingB))
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

bool CCButtonView::init(const char* path, const char* highlight_path, const char* label,float paddingL,float paddingT,float paddingR,float paddingB) {
	CCSprite* backgound =  CCSprite::create(path);
	backgound->setPosition(CCPointZero);
	backgound->setTag(0);
	addChild(backgound);

	m_background =  CCSprite::create(highlight_path);
	m_background->setPosition(CCPointZero);
	m_background->setOpacity(0);
	addChild(m_background);

	CCLabelTTF* label_name = CCLabelTTF::create(label, "Arial", 30.0);
	label_name->setPosition(ccp((paddingL-paddingR)/2,(paddingB-paddingT)/2));
	label_name->setAnchorPoint(ccp(0.5,0.5));
	label_name->setTag(1);
	this->addChild(label_name);
	backgound->setOpacity(76);
	label_name->setOpacity(76);
	setContentSize(backgound->getContentSize());
	return true;
}

void CCButtonView::setSelected(bool isSelected) {
//	m_background->setVisible(isSelected);
	unscheduleAllSelectors();
	m_background->stopAllActions();
	CCSprite* backgound = (CCSprite*)getChildByTag(0);
	CCLabelTTF* label_name = (CCLabelTTF*)getChildByTag(1);
	if(isSelected){
		CCActionInterval* fadeIn = CCFadeIn::create(0.4f);
		m_background->runAction(fadeIn);
		this->schedule(schedule_selector(CCButtonView::runBreath),8.0f);
		backgound->setOpacity(255);
		label_name->setOpacity(255);
	}else{
		CCActionInterval* fadeOut = CCFadeOut::create(0.4f);
		m_background->runAction(fadeOut);
		backgound->setOpacity(76);
		label_name->setOpacity(76);
	}
}

void CCButtonView::runBreath(CCTime dt) {
//	LOGD("MainItemLayer","runBreath");
	CCActionInterval* fadeTo = CCFadeTo::create(0.5f,127);
	CCActionInterval* fadeToBack = CCFadeTo::create(0.5f,255);
//	CCActionInterval* fadeIn = fadeTo->reverse();
	m_background->runAction(CCSequence::create(fadeTo,fadeToBack,NULL));
}


