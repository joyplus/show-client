#include "HistoryScnce.h"

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
							bteInfo.setDuration(btepisodesObj[j]["duration"].asInt());
							bteInfo.setPlaybackTime(btepisodesObj[j]["playback_time"].asInt());
							bteInfo.setPicUrl(btepisodesObj[j]["pic_url"].asString());
							btepisodes.push_back(bteInfo);
						}
						info.setBtepisodes(btepisodes);
						info.setIsDir(true);
					}

				}
				m_dates.push_back(info);
			}
		}else{
			LOGD("HistoryScnce", "play list json parse failed");
		}

		tableView = CCListView::create(this,CCSizeMake(winSize.width, 608),NULL,160.0f,0.0f,160.0f,0.0f);
		tableView->setAnchorPoint(ccp(0,1));
		tableView->setPosition(0,188);
		tableView->setDelegate(this);
		tableView->setDirection(kCCScrollViewDirectionHorizontal);
		tableView->setVerticalFillOrder(kCCListViewFillTopDown);
		tableView->setSelection(1);
		this->addChild(tableView);
//		tableView->reloadData();
		this->setKeypadEnabled(true);
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
	if (!pCell) {
		pCell = new CCTableViewCell();
		pCell->autorelease();

		CCImageView *pImage = CCImageView::createWithNetUrl("http://i2.xlpan.kanimg.com/pic/DD397F381C888480FFF462C97E1C90FC5C467848_X168.jpg","defulte_avatar.png",ccp(264,140));
		pImage->setPosition(ccp(170,506));
		pCell->addChild(pImage);

		CCSprite *pSprite = CCSprite::create("push_thumb.png");
		pSprite->setAnchorPoint(CCPointZero);
		pSprite->setPosition(ccp(0,405));
		pCell->addChild(pSprite);

		CCTableCellForHistory *pLabelBack = CCTableCellForHistory::create("push_card_activated.png");
		pLabelBack->setAnchorPoint(ccp(0,1));
		pLabelBack->setPosition(ccp(0,540));
		pLabelBack->setTag(124);
		pCell->addChild(pLabelBack);

		CCLabelTTF *pLabel = CCLabelTTF::create(pString->getCString(), "Arial", 27.0, CCSizeMake(270, 150), CCTextAlignment(kCCTextAlignmentLeft));
		pLabel->setPosition(ccp(35,300));
		pLabel->setAnchorPoint(ccp(0,1));
		pLabel->setTag(123);
		pCell->addChild(pLabel);
	}
	else
	{
		CCLabelTTF *pLabel = (CCLabelTTF*)pCell->getChildByTag(123);
		pLabel->setString(pString->getCString());
		CCTableCellForHistory *pLabelBack = (CCTableCellForHistory*)pCell->getChildByTag(124);
		pLabelBack->setPosition(ccp(0,540));
	}

//	if(idx==0||idx==numberOfCellsInTableView(table)-1){
//		pCell->setVisible(false);
//	}else{
//		pCell->setVisible(true);
//	}

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
	m_selectedCell = NULL;
	tableView->reloadData();
	tableView->setSelection(tableView->getSelected());

}

void HistoryScnce::tableCellSelected(CCListView* table, CCTableViewCell* cell,
		unsigned int idx) {
	LOGD("HistoryScnce","item %u Selected",idx);
	if(m_selectedCell)
	{
		CCTableCellForHistory * sLabelBack = (CCTableCellForHistory*)m_selectedCell->getChildByTag(124);
		sLabelBack->stopAllActions();
		sLabelBack->runAction(CCMoveTo::create(0.2f,ccp(0,540)));
	}
	if(cell){
		CCTableCellForHistory * pLabelBack = (CCTableCellForHistory*)cell->getChildByTag(124);
		pLabelBack->stopAllActions();
		pLabelBack->runAction(CCMoveTo::create(0.2f,ccp(0,405)));
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

