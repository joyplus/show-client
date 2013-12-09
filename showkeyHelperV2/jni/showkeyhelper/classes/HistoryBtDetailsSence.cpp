#include "HistoryBtDetailsSence.h"

HistoryBtDetailsSence::~HistoryBtDetailsSence() {
	// TODO Auto-generated destructor stub
}

bool HistoryBtDetailsSence::init() {
	bool bRet = false;
		do
		{
			if(! CCLayer::init())
			{
				LOGD("HistoryBtDetailsSence","CCLayer init fail");
				break;
			}
			m_selectedCell = NULL;

			CCSize winSize = CCDirector::sharedDirector()->getWinSize();
			tableView = CCListView::create(this,CCSizeMake(winSize.width, 608),NULL,160.0f,0.0f,160.0f,0.0f);
			tableView->setAnchorPoint(ccp(0,1));
			tableView->setPosition(0,188);
			tableView->setDelegate(this);
			tableView->setDirection(kCCScrollViewDirectionHorizontal);
			tableView->setVerticalFillOrder(kCCListViewFillTopDown);
			tableView->setSelection(0);
			this->addChild(tableView);
//			CCSprite* navagtor_main = CCSprite::create("nav_home.png");
//			navagtor_main->setPosition(ccp(160+navagtor_main->getContentSize().width/2,
//					820));
//			addChild(navagtor_main);
//
//			CCSprite* divider = CCSprite::create("nav_dot.png");
//			divider->setPosition(ccp(navagtor_main->getPosition().x+navagtor_main->getContentSize().width/2+divider->getContentSize().width/2,
//					820));
//			addChild(divider);
//
//			CCLabelTTF* navagtor_title = CCLabelTTF::create("推送历史", "Arial", 27.0);
//			navagtor_title->setPosition(ccp(10+divider->getPosition().x+divider->getContentSize().width/2+navagtor_title->getContentSize().width/2,
//					820));
//			addChild(navagtor_title);
//
//			CCSprite* divider1 = CCSprite::create("nav_dot.png");
//			divider1->setPosition(ccp(10+ navagtor_title->getPosition().x+navagtor_title->getContentSize().width/2+divider1->getContentSize().width/2,
//					820));
//			addChild(divider1);
//
//			CCLabelTTF* label = CCLabelTTF::create(m_playHistoryInfo.getName().c_str(), "Arial", 32.0, CCSizeMake(800, 50), CCTextAlignment(kCCTextAlignmentLeft));
//			label->setAnchorPoint(ccp(0,0.5));
//			label->setPosition(ccp(10+divider1->getPosition().x+divider1->getContentSize().width/2,
//					820));
//			label->setVerticalAlignment(CCVerticalTextAlignment(kCCVerticalTextAlignmentCenter));
//			addChild(label);

			showTitleJni(2,m_playHistoryInfo.getName().c_str());

//			this->setKeypadEnabled(true);
			bRet = true;
		} while (0);
		return bRet;
}

void HistoryBtDetailsSence::keyBackClicked() {
	CCScene *prevScene = CCDirector::sharedDirector()->previousScene();
	CCDirector::sharedDirector()->popScene(CCTransitionSlideInL::create(0.2f, prevScene));
	hideTitleJni(2);
}

void HistoryBtDetailsSence::keyArrowClicked(int arrow) {
	if(tableView->onKeyArrowClicked(arrow)) return;
	switch (arrow)
	{
		case ccKeypadMSGType(kTypeLeftArrowClicked):
			break;
		case ccKeypadMSGType(kTypeUpArrowClicked):
			tableView->setSelection(tableView->getSelected()-5);
			break;
		case ccKeypadMSGType(kTypeRightArrowClicked):
			break;
		case ccKeypadMSGType(kTypeDownArrowClicked):
			tableView->setSelection(tableView->getSelected()+5);
			break;
	}
}

void HistoryBtDetailsSence::keyEnterClicked() {
	if(tableView->onKeyEnterClicked()) return;
}

void HistoryBtDetailsSence::tableCellClicked(CCListView* table,
		CCTableViewCell* cell, unsigned int idx) {
	BTEpisode info = m_playHistoryInfo.getBtepisodes().at(idx);
	CSJson::Value root;
	CSJson::Value jsonobj;
	CSJson::FastWriter writer;
	jsonobj["_id"] = m_playHistoryInfo.getId();
	jsonobj["isDir"] = 1;
	jsonobj["sub_name"] = info.getName();
	root["date"] = jsonobj;
	root["type"] = 0;
	playVideoJNI(writer.write(root).c_str());
}

void HistoryBtDetailsSence::tableCellSelected(CCListView* table,
		CCTableViewCell* cell, unsigned int idx) {
	LOGD("HistoryBtDetailsSence","item %u Selected",idx);
		if(m_selectedCell)
		{
			CCTableCellForHistory * sLabelBack = (CCTableCellForHistory*)m_selectedCell->getChildByTag(3);
			sLabelBack->stopAllActions();
			sLabelBack->runAction(CCMoveTo::create(0.2f,ccp(0,540)));
			CCLabelTTF *pLabel = (CCLabelTTF*)m_selectedCell->getChildByTag(4);
			pLabel->setDimensions(ccp(270, 150));
		}
		if(cell){
			CCTableCellForHistory * pLabelBack = (CCTableCellForHistory*)cell->getChildByTag(3);
			pLabelBack->stopAllActions();
	//		pLabelBack->runAction(CCMoveTo::create(0.2f,ccp(0,405)));
			CCLabelTTF *pLabel = (CCLabelTTF*)cell->getChildByTag(4);
			CCFiniteTimeAction* actions=CCSequence::create(CCMoveTo::create(0.2f,ccp(0,450)),
							CCCallFuncND::create(this,
									callfuncND_selector(HistoryBtDetailsSence::callBackAnim),
									pLabel),NULL);
			pLabelBack->runAction(actions);
		}
		m_selectedCell = cell;
}

CCSize HistoryBtDetailsSence::tableCellSizeForIndex(CCListView* table,
		unsigned int idx) {
	return CCSizeMake(320,table->getViewSize().height);
}

CCTableViewCell* HistoryBtDetailsSence::tableCellAtIndex(CCListView* table,
		unsigned int idx) {
	BTEpisode info = m_playHistoryInfo.getBtepisodes().at(idx);
	CCString *pString = CCString::createWithFormat("%s", info.getName().c_str());
	CCTableViewCell *pCell = table->dequeueCell();
	CCImageView *pImage;
	CCSprite *pSprite;
	CCTableCellForHistory *pLabelBack;
	CCLabelTTF *pLabel;
	CCLabelTTF *pTimeLabel;
	if (!pCell) {
		pCell = new CCTableViewCell();
		pCell->autorelease();
		pImage = new CCImageView();
		pImage->setPosition(ccp(165,506));
		pImage->setTag(1);
		pImage->autorelease();
		pCell->addChild(pImage);
		pSprite = CCSprite::create("push_thumb.png");
		pSprite->setAnchorPoint(CCPointZero);
		pSprite->setPosition(ccp(0,405));
		pSprite->setTag(2);
		pCell->addChild(pSprite);
		pLabelBack = CCTableCellForHistory::create("push_card_activated.png");
		pLabelBack->setAnchorPoint(ccp(0,1));
		pLabelBack->setPosition(ccp(0,540));
		pLabelBack->setTag(3);
		pCell->addChild(pLabelBack);
		pLabel = CCLabelTTF::create("", "Arial", 27.0, CCSizeMake(270, 150), CCTextAlignment(kCCTextAlignmentLeft));
		pLabel->setPosition(ccp(35,300));
		pLabel->setAnchorPoint(ccp(0,1));
		pLabel->setTag(4);
		pCell->addChild(pLabel);
		pTimeLabel = CCLabelTTF::create("","Arial", 27.0);
		pTimeLabel->setPosition(ccp(170,350));
		pTimeLabel->setTag(5);
		pCell->addChild(pTimeLabel);
	}
	else
	{
		pImage = (CCImageView*)pCell->getChildByTag(1);
		pSprite = (CCSprite*)pCell->getChildByTag(2);
		pLabelBack = (CCTableCellForHistory*)pCell->getChildByTag(3);
		pLabel = (CCLabelTTF*)pCell->getChildByTag(4);
		pTimeLabel = (CCLabelTTF*)pCell->getChildByTag(5);
	}
	pLabel->setString(info.getName().c_str());
	if(idx == table->getSelected()){
		pLabelBack->setPosition(ccp(0,450));
		pLabel->setDimensions(ccp(270, 240));
		m_selectedCell = pCell;
	}else{
		pLabelBack->setPosition(ccp(0,540));
		pLabel->setDimensions(ccp(270, 150));
	}
	if(info.getDuration()-info.getPlaybackTime()<10&&info.getDuration()>10){
		pTimeLabel->setString("已看完");
	}else{
		if(info.getDuration()==0&&info.getPlaybackTime()==0){
			pTimeLabel->setString("");
		}else{
			CCString* time = CCString::createWithFormat("%s/%s",fomartTime(info.getPlaybackTime()).c_str(),
													fomartTime(info.getDuration()).c_str());
			pTimeLabel->setString(time->getCString());
		}

	}
	pSprite->setAnchorPoint(CCPointZero);
	pSprite->setPosition(ccp(0,405));
	pImage->setVisible(true);
	pImage->initWithUrl(info.getPicUrl().c_str(),"default_video_photo.png",true);
	pImage->setBoundSize(ccp(264,145));
	return pCell;
}

unsigned int HistoryBtDetailsSence::numberOfCellsInTableView(
		CCListView* table) {
//	LOGD("HistoryBtDetailsSence","size -->%d name -> %s",m_playHistoryInfo.getBtepisodes().size(),m_playHistoryInfo.getName().c_str());
	return m_playHistoryInfo.getBtepisodes().size();
}

CCScene* HistoryBtDetailsSence::scene(PlayHistoryInfo info) {
	CCScene * scene = NULL;
	do
	{
		// 'scene' is an autorelease object
		scene = CCScene::create();
		CC_BREAK_IF(! scene);
		// 'layer' is an autorelease object
		HistoryBtDetailsSence* layer = HistoryBtDetailsSence::create(info);
		CC_BREAK_IF(! layer);
		// add layer as a child to scene
		scene->addChild(layer);
		LOGD("HistoryBtDetailsSence","size -->%d name -> %s",info.getBtepisodes().size(),info.getName().c_str());
	} while (0);

	// return the scene
	return scene;
}

void HistoryBtDetailsSence::onEnterTransitionDidFinish() {
	CCLayer::onEnterTransitionDidFinish();
	this->setKeypadEnabled(true);
	LOGD("HistoryBtDetailsSence","----------onEnterTransitionDidFinish----------");
}

void HistoryBtDetailsSence::onExitTransitionDidStart() {
	CCLayer::onExitTransitionDidStart();
	this->setKeypadEnabled(false);
	LOGD("HistoryBtDetailsSence","----------onExitTransitionDidStart----------");
}

void HistoryBtDetailsSence::callBackAnim(CCNode* sender, CCLabelTTF* pLabel) {
	pLabel->setDimensions(ccp(270, 240));
}



