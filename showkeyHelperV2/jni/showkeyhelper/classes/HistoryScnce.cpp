#include "HistoryScnce.h"
#include "HistoryBtDetailsSence.h"

CCScene* HistoryScnce::scene()
{
	 CCScene * scene = NULL;
	do
	{
		// 'scene' is an autorelease object
		scene = CCScene::create();
		CC_BREAK_IF(! scene);
		// 'layer' is an autorelease object
		HistoryScnce *layer = HistoryScnce::create();
		CC_BREAK_IF(! layer);
		// add layer as a child to scene
		scene->addChild(layer);
	} while (0);

	// return the scene
	return scene;

}
bool HistoryScnce::init()
{
	bool bRet = false;
	do
	{
		if(! CCLayer::init())
		{
			LOGD("PageLayer","CCLayer init fail");
			break;
		}
		m_selectedCell = NULL;
		m_dates.clear();

		CCSize winSize = CCDirector::sharedDirector()->getWinSize();

		CCSprite* loading = CCSprite::create("waiting.png");
		loading->setPosition(ccp(winSize.width/2,winSize.height/2));
		loading->setTag(250);
		loading->runAction(CCRepeatForever::create(CCRotateBy::create(0.1f,36.0f)));
		addChild(loading);

//		tableView->reloadData();
		bRet = true;
	} while (0);
	return bRet;
}



void HistoryScnce::keyEnterClicked()
{
	if(tableView->onKeyEnterClicked()) return;
}

void HistoryScnce::keyArrowClicked(int arrow)
{
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

cocos2d::extension::CCTableViewCell* HistoryScnce::tableCellAtIndex(
		CCListView* table, unsigned int idx)
{
	PlayHistoryInfo info = m_dates.at(idx);
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
	if(info.getName().empty()){
		pLabel->setString(info.getPushUrl().c_str());
	}else{
		pLabel->setString(info.getName().c_str());
	}
	if(info.isIsDir()){
		pSprite->initWithFile("push_thumb_folder.png");
	}else{
		pSprite->initWithFile("push_thumb.png");
	}
	pSprite->setAnchorPoint(CCPointZero);
	pSprite->setPosition(ccp(0,405));
	if(idx == table->getSelected()){
		pLabelBack->setPosition(ccp(0,405));
		m_selectedCell = pCell;
	}else{
		pLabelBack->setPosition(ccp(0,540));
	}
	if(info.getDuration()-info.getPlaybackTime()<10&&info.getDuration()>10){
		pTimeLabel->setString("已看完");
	}else{
		CCString* time = CCString::createWithFormat("%s/%s",fomartTime(info.getPlaybackTime()).c_str(),
										fomartTime(info.getDuration()).c_str());
		pTimeLabel->setString(time->getCString());
	}
	pSprite->setAnchorPoint(CCPointZero);
	pSprite->setPosition(ccp(0,405));
	pImage->setVisible(true);
	pImage->initWithUrl(info.getPicUrl().c_str(),"default_video_photo.png");
	pImage->setBoundSize(ccp(264,145));
	return pCell;
}

void HistoryScnce::keyBackClicked() {
	CCScene *prevScene = CCDirector::sharedDirector()->previousScene();
	CCDirector::sharedDirector()->popScene(CCTransitionSlideInL::create(0.2f, prevScene));
//	CCDirector::sharedDirector()->popScene();
}

unsigned int HistoryScnce::numberOfCellsInTableView(CCListView* table)
{
//	LOGD(TAG_HistoryScnce,"return number of cells %d",20);
//	LOGD(TAG_HistoryScnce,"view size %f",table->getViewSize().width);
	return m_dates.size();
}

void HistoryScnce::tableCellClicked(CCListView* table, CCTableViewCell* cell,
		unsigned int idx) {
	LOGD("HistoryScnce","item %u Clicked",idx);
	PlayHistoryInfo info = m_dates.at(idx);
	if(info.isIsDir()){//分集
//		HistoryBtDetailsSence* sence = HistoryBtDetailsSence::scene(info);
		CCDirector::sharedDirector()->pushScene(CCTransitionSlideInR::create(0.2f,HistoryBtDetailsSence::scene(info)));
	}else{
		CSJson::Value root;
		CSJson::Value jsonobj;
		CSJson::FastWriter writer;
		if(info.getType() == 2){
			jsonobj["_id"] = info.getId();
			root["date"] = jsonobj;
			root["type"] = 1;
		}else{
			jsonobj["_id"] = info.getId();
			jsonobj["isDir"] = 0;
			root["date"] = jsonobj;
			root["type"] = 0;
		}
		playVideoJNI(writer.write(root).c_str());
	}
}

void HistoryScnce::tableCellSelected(CCListView* table, CCTableViewCell* cell,
		unsigned int idx) {
	LOGD("HistoryScnce","item %u Selected",idx);
	if(m_selectedCell)
	{
		CCTableCellForHistory * sLabelBack = (CCTableCellForHistory*)m_selectedCell->getChildByTag(3);
		sLabelBack->stopAllActions();
		sLabelBack->runAction(CCMoveTo::create(0.2f,ccp(0,540)));
		CCLabelTTF *pLabel = (CCLabelTTF*)m_selectedCell->getChildByTag(4);
		pLabel->setDimensions(ccp(270, 150));
//		CCFiniteTimeAction* actions=CCSequence::create(CCMoveTo::create(0.2f,ccp(0,540)),
//				CCCallFuncND::create(this,
//						callfuncND_selector(HistoryScnce::callBackAnim),
//						pLabel),NULL);
//		sLabelBack->runAction(actions);
	}
	if(cell){
		CCTableCellForHistory * pLabelBack = (CCTableCellForHistory*)cell->getChildByTag(3);
		pLabelBack->stopAllActions();
//		pLabelBack->runAction(CCMoveTo::create(0.2f,ccp(0,405)));
		CCLabelTTF *pLabel = (CCLabelTTF*)cell->getChildByTag(4);
		CCFiniteTimeAction* actions=CCSequence::create(CCMoveTo::create(0.2f,ccp(0,405)),
						CCCallFuncND::create(this,
								callfuncND_selector(HistoryScnce::callBackAnim),
								pLabel),NULL);
		pLabelBack->runAction(actions);

//		pLabel->setDimensions(ccp(270, 240));
	}
	m_selectedCell = cell;
}

CCSize HistoryScnce::tableCellSizeForIndex(CCListView* list,
		unsigned int idx)
{
//	if(idx==0||idx==100)
//	{
//		return CCSizeMake(150,list->getViewSize().height);
//	}
//	else
//	{
		return CCSizeMake(320,list->getViewSize().height);
//	}
}


HistoryScnce::~HistoryScnce() {
	// TODO Auto-generated destructor stub
}

void HistoryScnce::onEnter() {
	CCLayer::onEnter();
	LOGD("HistoryScnce","----------onEnter----------");
}

void HistoryScnce::onEnterTransitionDidFinish() {
	LOGD("HistoryScnce","----------onEnterTransitionDidFinish----------");
	CCLayer::onEnterTransitionDidFinish();
	string playList = getPlayHistoryListJNI();
	LOGD("HistoryScnce","play list ---> %s", playList.c_str());
	CSJson::Value root;
	CSJson::Reader reader;
	if(reader.parse(playList,root)){
		const CSJson::Value arrayObj = root["list"];
		for(int i=0; i< arrayObj.size(); i++){
			LOGD("HistoryScnce","play list %d , name : %s", i , arrayObj[i]["name"].asString().c_str());
			PlayHistoryInfo info;
			info.setId(arrayObj[i]["_id"].asInt());
			info.setName(arrayObj[i]["name"].asString());
			info.setDuration(arrayObj[i]["duration"].asInt());
			info.setPlaybackTime(arrayObj[i]["playback_time"].asInt());
			info.setPushUrl(arrayObj[i]["push_url"].asString());
			info.setPicUrl(arrayObj[i]["pic_url"].asString());
			info.setType(arrayObj[i]["type"].asInt());
			string btes = arrayObj[i]["episodes"].asString();
			info.setIsDir(false);
			if(!btes.empty()){
				CSJson::Value btepisodesObj;
				CSJson::Reader btepisodesreader;
				if(btepisodesreader.parse(btes,btepisodesObj)){
					std::vector<BTEpisode> btepisodes;
					for(int j=0; j<btepisodesObj.size(); j++){
						BTEpisode bteInfo;
						bteInfo.setName(btepisodesObj[j]["name"].asString());
						LOGD("HistoryScnce","%s bt name--> %s", info.getName().c_str(), bteInfo.getName().c_str());
						bteInfo.setDuration(btepisodesObj[j]["duration"].asInt());
						bteInfo.setPlaybackTime(btepisodesObj[j]["playback_time"].asInt());
						bteInfo.setPicUrl(btepisodesObj[j]["pic_url"].asString());
						btepisodes.push_back(bteInfo);
					}
					info.setBtepisodes(btepisodes);
					info.setIsDir(true);
				}else{

				}
			}
			m_dates.push_back(info);
		}
	}else{
		LOGD("HistoryScnce", "play list json parse failed");
	}
	CCSprite* loading = (CCSprite*)getChildByTag(250);
	loading->stopAllActions();
	loading->setVisible(false);
	CCSize winSize = CCDirector::sharedDirector()->getWinSize();
	tableView = CCListView::create(this,CCSizeMake(winSize.width, 608),NULL,160.0f,0.0f,160.0f,0.0f);
	tableView->setAnchorPoint(ccp(0,1));
	tableView->setPosition(0,188);
	tableView->setDelegate(this);
	tableView->setDirection(kCCScrollViewDirectionHorizontal);
	tableView->setVerticalFillOrder(kCCListViewFillTopDown);
	tableView->setSelection(0);
	this->addChild(tableView);
	this->setKeypadEnabled(true);
}

void HistoryScnce::onExit() {
	CCLayer::onExit();
	LOGD("HistoryScnce","----------onExit----------");
}

void HistoryScnce::onExitTransitionDidStart() {
	CCLayer::onExitTransitionDidStart();
	this->setKeypadEnabled(false);
	LOGD("HistoryScnce","----------onExitTransitionDidStart----------");
}

void HistoryScnce::callBackAnim(CCNode* sender, CCLabelTTF* pLabel) {
	pLabel->setDimensions(ccp(270, 240));
}


