#include "SettingSence.h"

static void settingDilogCallbackFunc(bool isBack,void* ctx)
{
	SettingSence* thiz = (SettingSence*)ctx;
	if(isBack){
		thiz->popSence();
	}else{
//		thiz->loginXunleiSuccess();
	}
}

SettingSence::~SettingSence() {
	// TODO Auto-generated destructor stub
}

void SettingSence::popSence() {
	CCSprite* loading = (CCSprite*)getChildByTag(250);
	loading->stopAllActions();
	loading->setVisible(false);
	CCScene *prevScene = CCDirector::sharedDirector()->previousScene();
	CCDirector::sharedDirector()->popScene(CCTransitionSlideInL::create(0.2f, prevScene));
	hideTitleJni(1);
}

bool SettingSence::init() {
	bool bRet = false;
	do
	{
		if(! CCLayer::init())
		{
			LOGD("PageLayer","CCLayer init fail");
			break;
		}
		CCSize winSize = CCDirector::sharedDirector()->getWinSize();
		CCSprite* loading = CCSprite::create("waiting.png");
		loading->setPosition(ccp(winSize.width/2,winSize.height/2));
		loading->setTag(250);
		loading->runAction(CCRepeatForever::create(CCRotateBy::create(0.1f,36.0f)));
		addChild(loading);

//		CCSprite* navagtor_main = CCSprite::create("nav_home.png");
//		navagtor_main->setPosition(ccp(160+navagtor_main->getContentSize().width/2,
//				820));
//		addChild(navagtor_main);
//
//		CCSprite* divider = CCSprite::create("nav_dot.png");
//		divider->setPosition(ccp(navagtor_main->getPosition().x+navagtor_main->getContentSize().width/2+divider->getContentSize().width/2,
//				820));
//		addChild(divider);
//
//		CCLabelTTF* navagtor_title = CCLabelTTF::create("设置", "Arial", 32.0);
//		navagtor_title->setPosition(ccp(10+divider->getPosition().x+divider->getContentSize().width/2+navagtor_title->getContentSize().width/2,
//				820));
//		addChild(navagtor_title);
		showTitleJni(1,getStringResouceByKeyJNI("setting_title").c_str());
		bRet = true;
	} while (0);
	return bRet;
}

cocos2d::CCScene* SettingSence::scene() {
	CCScene * scene = NULL;
	do
	{
		// 'scene' is an autorelease object
		scene = CCScene::create();
		CC_BREAK_IF(! scene);
		// 'layer' is an autorelease object
		SettingSence *layer = SettingSence::create();
		CC_BREAK_IF(! layer);
		// add layer as a child to scene
		scene->addChild(layer);
	} while (0);

	// return the scene
	return scene;
}

void SettingSence::onEnterTransitionDidFinish() {
	startSetting(settingDilogCallbackFunc,this);
}

void SettingSence::onExitTransitionDidStart() {
}



