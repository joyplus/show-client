#include "CCButton.h"

namespace joyplus {

CCButton::~CCButton() {
	// TODO Auto-generated destructor stub
}
void CCButton::setSelected(bool selected) {
	if(selected){
		m_normal_back->setVisible(false);
		m_selected_back->setVisible(true);
	}else{
		m_normal_back->setVisible(true);
		m_selected_back->setVisible(false);
	}
}

CCButton* CCButton::create(const char* path, const char* highlight_path,
		const char* label, float paddingL, float paddingT, float paddingR,
		float paddingB) {
	CCButton *pRet = new CCButton();
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

CCButton* CCButton::create(const char* path, const char* highlight_path,
		const char* label) {
	return create(path,highlight_path,label,0,0,0,0);
}

bool CCButton::init(const char* path, const char* highlight_path,
		const char* label, float paddingL, float paddingT,
		float paddingR, float paddingB) {
	m_normal_back = CCSprite::create(path);
	m_normal_back->setPosition(CCPointZero);
	m_normal_back->setVisible(true);
	addChild (m_normal_back);
	m_selected_back = CCSprite::create(highlight_path);
	m_selected_back->setPosition(CCPointZero);
	m_selected_back->setVisible(false);
	addChild (m_selected_back);
	label_name = CCLabelTTF::create(label, "Arial", 30.0);
	label_name->setPosition(
			ccp((paddingL - paddingR) / 2, (paddingB - paddingT) / 2));
	label_name->setAnchorPoint(ccp(0.5, 0.5));
	this->addChild(label_name);
	float width = MAX(m_normal_back->getContentSize().width,
			m_selected_back->getContentSize().width);
	float height = MAX(m_normal_back->getContentSize().height,
			m_selected_back->getContentSize().height);
	setContentSize(ccp(width, height));
	return true;
}
/* namespace joyplus */

}

