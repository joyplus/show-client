#include "TransitionScenes.h"

const unsigned int kSceneFade = 0xFADEFADE;

TransitionSceneSaleFadeOut::~TransitionSceneSaleFadeOut() {
}

void TransitionSceneSaleFadeOut::onEnter() {
	CCTransitionScene::onEnter();
//	ccColor4F t;
//	t.a = 0;
//	t.b = 0;
//	t.g = 0;
//	t.r = 0;
//	CCLayerColor* l = CCLayerColor::create(ccc4(0x00, 0x00, 0x00, 0x00));
//	addChild(l, 0, kSceneFade);
////	l->setVisible(false);
//	CCNode* f = getChildByTag(kSceneFade);

	CCActionInterval* sale = CCScaleTo::create(m_fDuration,2.0f);
	CCEaseExponentialOut  *eSale = CCEaseExponentialOut ::create(sale);
	CCActionInterval* out = CCFadeOut::create(m_fDuration);
//	CCActionInterval* in = CCFadeIn::create(m_fDuration);
	m_pInScene->setVisible(false);

//	TransitionSceneSaleFadeOut::runActionAllChild(out,m_pOutScene);
//	f->runAction(out);
//	m_pOutScene->setCascadeOpacityEnabled(true);
	m_pOutScene->runAction
	(
		CCSequence::create
		(
			CCSpawn::create
					(
						out,
						eSale,
						NULL
					),
			CCCallFunc::create(this, callfunc_selector(CCTransitionScene::finish)),
			NULL
		)
	);
}

void TransitionSceneSaleFadeOut::runActionAllChild(CCActionInterval*action,CCNode* node){
	CCArray* array = node->getChildren();
	if(array&&array->data&&array->data->num){
		CCObject* obj = NULL;
		CCARRAY_FOREACH(array,obj){
			CCNode* child = (CCNode*)obj;
			if(child){
				runActionAllChild(action,child);
			}
		}
	}else{
		action->retain();
		node->runAction(action);
	}
}

void TransitionSceneSaleFadeOut::onExit() {
	CCTransitionScene::onExit();
//	this->removeChildByTag(kSceneFade, false);
}

TransitionSceneSaleFadeOut* TransitionSceneSaleFadeOut::create(float t,
		CCScene* scene) {
	TransitionSceneSaleFadeOut* pScene = new TransitionSceneSaleFadeOut();
	if(pScene && pScene->initWithDuration(t, scene))
	{
		pScene->autorelease();
		return pScene;
	}
	CC_SAFE_DELETE(pScene);
	return NULL;
}

TransitionSceneSaleFadeIn::~TransitionSceneSaleFadeIn() {
}

void TransitionSceneSaleFadeIn::onEnter() {
	CCTransitionScene::onEnter();
	m_pInScene->setScale(1.5f);
	CCActionInterval* sale = CCScaleTo::create(m_fDuration,1.0f);
	CCActionInterval* out = CCFadeOut::create(m_fDuration);
	CCActionInterval* in = CCFadeIn::create(m_fDuration);
	m_pOutScene->runAction(out);
	m_pInScene->runAction
	(
		CCSequence::create
		(
			sale,
			in,
			CCCallFunc::create(this, callfunc_selector(CCTransitionScene::finish)),
			NULL
		)
	);
}

TransitionSceneSaleFadeIn* TransitionSceneSaleFadeIn::create(float t,
		CCScene* scene) {
	TransitionSceneSaleFadeIn* pScene = new TransitionSceneSaleFadeIn();
	if(pScene && pScene->initWithDuration(t, scene))
	{
		pScene->autorelease();
		return pScene;
	}
	CC_SAFE_DELETE(pScene);
	return NULL;
}


