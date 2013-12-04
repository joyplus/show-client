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
		m_selected_button = NULL;

		CCSize winSize = CCDirector::sharedDirector()->getWinSize();

		CCSprite* loading = CCSprite::create("waiting.png");
		loading->setPosition(ccp(winSize.width/2,winSize.height/2));
		loading->setTag(250);
		loading->runAction(CCRepeatForever::create(CCRotateBy::create(0.1f,36.0f)));
		addChild(loading);

		CCSprite* menu_back = CCSprite::create("menu.png");
		menu_back->setPosition(ccp(winSize.width/2,menu_back->getContentSize().height/2));
		menu_back->setTag(15);
		menu_back->setVisible(false);
		addChild(menu_back);

		CCSprite* navagtor_main = CCSprite::create("nav_home.png");
		navagtor_main->setPosition(ccp(160+navagtor_main->getContentSize().width/2,
				820));
		addChild(navagtor_main);

		CCSprite* divider = CCSprite::create("nav_dot.png");
		divider->setPosition(ccp(navagtor_main->getPosition().x+navagtor_main->getContentSize().width/2+divider->getContentSize().width/2,
				820));
		addChild(divider);

		CCLabelTTF* navagtor_title = CCLabelTTF::create(getStringResouceByKeyJNI("history_title").c_str(), "Arial", 32.0);
		navagtor_title->setPosition(ccp(10+divider->getPosition().x+divider->getContentSize().width/2+navagtor_title->getContentSize().width/2,
				820));
		addChild(navagtor_title);
//		CCSprite* menu_selected_all = CCSprite::create("selected_quanxuan.png");
//		menu_selected_all->setPosition(ccp(160+menu_selected_all->getContentSize().width/2,menu_selected_all->getContentSize().height/2));
//		addChild(menu_selected_all);
//
//		CCSprite* menu_delete = CCSprite::create("selected_delete.png");
//		menu_delete->setPosition(ccp(160+menu_selected_all->getContentSize().width/2,menu_selected_all->getContentSize().height/2));
//		addChild(menu_delete);
//		CCSprite* menu_delete1 = CCSprite::create("unselected_delete.png");
//		menu_delete1->setPosition(ccp(160+menu_selected_all->getContentSize().width/2,menu_selected_all->getContentSize().height/2));
//		addChild(menu_delete1);
//		CCSprite* menu_delete2 = CCSprite::create("unselected_quanxuan.png");
//		menu_delete2->setPosition(ccp(160+menu_selected_all->getContentSize().width/2,menu_selected_all->getContentSize().height/2));
//		addChild(menu_delete2);

		m_button_select_all = joyplus::CCButton::create("unselected_quanxuan.png","selected_quanxuan.png","");
		m_button_select_all->setAnchorPoint(ccp(0.5f,0.5f));
		m_button_select_all->setTag(13);
		m_button_select_all->setVisible(false);
		m_button_select_all->setPosition(ccp(160+m_button_select_all->getContentSize().width/2,m_button_select_all->getContentSize().height/2));
		addChild(m_button_select_all);

		m_button_delete = joyplus::CCButton::create("unselected_delete.png","selected_delete.png","");
		m_button_delete->setAnchorPoint(ccp(0.5f,0.5f));
		m_button_delete->setTag(14);
		m_button_delete->setVisible(false);
		m_button_delete->setPosition(ccp(m_button_select_all->getPosition().x + 100 + m_button_delete->getContentSize().width,m_button_delete->getContentSize().height/2));
		addChild(m_button_delete);

		CCSprite * notice_edit_back = CCSprite::create("tip_quitedit.png");
		notice_edit_back->setPosition(ccp(1760-notice_edit_back->getContentSize().width/2,notice_edit_back->getContentSize().height/2));
		notice_edit_back->setTag(10);
		notice_edit_back->setVisible(false);
		addChild(notice_edit_back);

		CCSprite * notice_back = CCSprite::create("tip_return.png");
		notice_back->setPosition(ccp(1760-notice_back->getContentSize().width/2,notice_back->getContentSize().height/2));
		notice_back->setTag(11);
		notice_back->setVisible(false);
		addChild(notice_back);

		CCSprite * notice_menu = CCSprite::create("tip_more.png");
		notice_menu->setPosition(ccp(160+notice_menu->getContentSize().width/2,notice_menu->getContentSize().height/2));
		notice_menu->setTag(12);
		notice_menu->setVisible(false);
		addChild(notice_menu);

		m_empty_back = CCSprite::create("null.png");
		m_empty_back->setPosition(ccp(200+320+m_empty_back->getContentSize().width/2,winSize.height/2-45));
		m_empty_back->setVisible(false);
		addChild(m_empty_back);
		CCString* str = CCString::createWithFormat(getStringResouceByKeyJNI("history_emptey_notice").c_str(),getOnlineWebUrlJNI().c_str());
		m_empty_notice = CCLabelTTF::create(str->getCString(), "Arial", 30.0);
		m_empty_notice->setPosition(m_empty_back->getPosition());
		m_empty_notice->setVisible(false);
		addChild(m_empty_notice);

		bRet = true;
	} while (0);
	return bRet;
}



void HistoryScnce::keyEnterClicked()
{
	if(m_dates.size()<=0){
			return;
		}
	if(m_selected_button){
		if(m_selected_button->getTag()==13){//All
			for(int i=0; i<m_dates.size(); i++){
				PlayHistoryInfo info = m_dates.at(i);
				info.setEditeStatue(mSelected);
				m_dates[i] = info;
			}
			tableView->reloadData();
			CCLabelTTF *pLabel = (CCLabelTTF*)m_selectedCell->getChildByTag(4);
			pLabel->setDimensions(ccp(270, 150));
			CCTableCellForHistory * pLabelBack = (CCTableCellForHistory*)m_selectedCell->getChildByTag(3);
			pLabelBack->setPosition(ccp(0,540));
		}else if(m_selected_button->getTag()==14){//Delete
			//delete seleted item;
			CSJson::Value root;
			CSJson::Value list;
			for(vector<PlayHistoryInfo>::iterator it=m_dates.begin(); it!=m_dates.end(); )
			{
//				PlayHistoryInfo &info = it;
				if(it->getEditeStatue() == mSelected)
				{
					list.append(it->getId());
					it = m_dates.erase(it); //不能写成arr.erase(it);
				}
				else
				{
					it->setEditeStatue(mNormal);
					++it;
				}
			}
//			for(int i=0; i<m_dates.size(); i++){
//				PlayHistoryInfo info = m_dates.at(i);
//				info.setEditeStatue(mNormal);
//				m_dates[i] = info;
//			}
			root["list"] = list;
			CSJson::FastWriter writer;
			if(m_dates.size()>0){
				tableView->setSelection(tableView->getSelected()+1-list.size());
			}else{
				//。。。。。
				CCSprite* menu_back = (CCSprite*)getChildByTag(15);
				CCSprite* notice_menu = (CCSprite*)getChildByTag(12);
				CCSprite* notice_back = (CCSprite*)getChildByTag(11);

				menu_back->setVisible(false);
				notice_menu->setVisible(false);
				notice_back->setVisible(false);
			}

			LOGD("HistoryScnce","delete --->%s",writer.write(root).c_str());
			deleatePlayHistoryListJNI(writer.write(root).c_str());
			CCSprite* menu_back = (CCSprite*)getChildByTag(15);
			CCSprite* notice_menu = (CCSprite*)getChildByTag(12);
			CCSprite* notice_back = (CCSprite*)getChildByTag(11);
			CCSprite* notice_edit_back = (CCSprite*)getChildByTag(10);
			if(m_dates.size()<=0){
				tableView->reloadData();
				menu_back->setVisible(false);
				notice_menu->setVisible(false);
				notice_back->setVisible(false);
				notice_edit_back->setVisible(false);
				m_empty_back->setVisible(true);
				m_empty_notice->setVisible(true);
				m_button_select_all->setVisible(false);
				m_button_delete->setVisible(false);
				isEditeStatue = false;
			}else{
				keyBackClicked();
			}
		}else{
//。。。
		}
	}else{
		if(tableView->onKeyEnterClicked()) return;
	}
}

void HistoryScnce::keyArrowClicked(int arrow)
{
	if(m_dates.size()<=0){
			return;
		}
	if(isEditeStatue){
		switch(arrow)
		{
			case ccKeypadMSGType(kTypeLeftArrowClicked):
					if(!m_selected_button){
						tableView->onKeyArrowClicked(arrow);
					}else{
						if(m_selected_button->getTag()==14){
							m_selected_button->setSelected(false);
							joyplus::CCButton* button = (joyplus::CCButton*)getChildByTag(13);
							button->setSelected(true);
							m_selected_button=button;
						}
					}
				break;
			case ccKeypadMSGType(kTypeUpArrowClicked):
					if(m_selected_button){
						m_selected_button->setSelected(false);
						m_selected_button = NULL;
						CCTableCellForHistory * pLabelBack = (CCTableCellForHistory*)m_selectedCell->getChildByTag(3);
						pLabelBack->stopAllActions();
						CCLabelTTF *pLabel = (CCLabelTTF*)m_selectedCell->getChildByTag(4);
						CCFiniteTimeAction* actions=CCSequence::create(CCMoveTo::create(0.2f,ccp(0,450)),
										CCCallFuncND::create(this,
												callfuncND_selector(HistoryScnce::callBackAnim),
												pLabel),NULL);
						pLabelBack->runAction(actions);
					}
				break;
			case ccKeypadMSGType(kTypeRightArrowClicked):
					if(m_selected_button){
						if(m_selected_button->getTag()==13){
							m_selected_button->setSelected(false);
							joyplus::CCButton* button = (joyplus::CCButton*)getChildByTag(14);
							button->setSelected(true);
							m_selected_button=button;
						}
					}else{
						tableView->onKeyArrowClicked(arrow);
					}
				break;
			case ccKeypadMSGType(kTypeDownArrowClicked):
					if(!m_selected_button){
						joyplus::CCButton* button = (joyplus::CCButton*)getChildByTag(13);
						button->setSelected(true);
						m_selected_button=button;
						if(m_selectedCell){
						CCTableCellForHistory * sLabelBack = (CCTableCellForHistory*)m_selectedCell->getChildByTag(3);
						sLabelBack->stopAllActions();
						sLabelBack->runAction(CCMoveTo::create(0.2f,ccp(0,540)));
						CCLabelTTF *pLabel = (CCLabelTTF*)m_selectedCell->getChildByTag(4);
						pLabel->setDimensions(ccp(270, 150));
					}
					}
				break;
		}
	}else{
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
	CCSprite *pEdite;
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
		pEdite = CCSprite::create("unselected.png");
		pEdite->setPosition(ccp(pEdite->getContentSize().width/2,405));
		pEdite->setTag(6);
		pCell->addChild(pEdite);
	}
	else
	{
		pImage = (CCImageView*)pCell->getChildByTag(1);
		pSprite = (CCSprite*)pCell->getChildByTag(2);
		pLabelBack = (CCTableCellForHistory*)pCell->getChildByTag(3);
		pLabel = (CCLabelTTF*)pCell->getChildByTag(4);
		pTimeLabel = (CCLabelTTF*)pCell->getChildByTag(5);
		pEdite = (CCSprite*)pCell->getChildByTag(6);
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
	LOGD("HistoryScnce","info--getEditeStatue -->%d",info.getEditeStatue());
	if(info.getEditeStatue()==mNormal){
		pEdite->setVisible(false);
	}else if(info.getEditeStatue()==mEdite){
		pEdite->setVisible(true);
		pEdite->initWithFile("unselected.png");
	}else{
		pEdite->setVisible(true);
		pEdite->initWithFile("selected.png");
	}
	pEdite->setPosition(ccp(15+pEdite->getContentSize().width/2,405-pEdite->getContentSize().height/2));
	pSprite->setAnchorPoint(CCPointZero);
	pSprite->setPosition(ccp(0,405));
	if(idx == table->getSelected()){
		pLabelBack->setPosition(ccp(0,450));
		pLabel->setDimensions(ccp(270, 240));
		m_selectedCell = pCell;
	}else{
		pLabelBack->setPosition(ccp(0,540));
		pLabel->setDimensions(ccp(270, 150));
	}
	if(info.getDuration()-info.getPlaybackTime()<10&&info.getDuration()>10){
		pTimeLabel->setString(getStringResouceByKeyJNI("history_finished").c_str());
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

void HistoryScnce::keyBackClicked() {
	if(isEditeStatue){
		CCSprite* notice_menu = (CCSprite*)getChildByTag(12);
		CCSprite* notice_back = (CCSprite*)getChildByTag(11);
		CCSprite* notice_menu_back = (CCSprite*)getChildByTag(10);

		notice_menu->setVisible(true);
		notice_back->setVisible(true);
		notice_menu_back->setVisible(false);

		m_button_delete->setVisible(false);
		m_button_select_all->setVisible(false);
		if(m_selected_button){
			m_selected_button->setSelected(false);
			m_selected_button=NULL;
		}
		isEditeStatue = false;
		for(int i=0; i<m_dates.size(); i++){
			PlayHistoryInfo info = m_dates.at(i);
			info.setEditeStatue(mNormal);
			m_dates[i] = info;
		}
		tableView->reloadData();
	}else{
		CCScene *prevScene = CCDirector::sharedDirector()->previousScene();
		CCDirector::sharedDirector()->popScene(CCTransitionSlideInL::create(0.2f, prevScene));
	}
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
	if(isEditeStatue){
		PlayHistoryInfo info = m_dates.at(idx);
		if(info.getEditeStatue()==mEdite){
			info.setEditeStatue(mSelected);
		}else if(info.getEditeStatue()==mSelected){
			info.setEditeStatue(mEdite);
		}
		m_dates[idx] = info;
		tableView->reloadData();
	}else{
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
		CCFiniteTimeAction* actions=CCSequence::create(CCMoveTo::create(0.2f,ccp(0,450)),
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
	CCSprite* loading = (CCSprite*)getChildByTag(250);
	if(loading->isVisible()){
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
				info.setEditeStatue(mNormal);
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
		CCSprite* menu_back = (CCSprite*)getChildByTag(15);
		CCSprite* notice_menu = (CCSprite*)getChildByTag(12);
		CCSprite* notice_back = (CCSprite*)getChildByTag(11);
		if(m_dates.size()>0){
			menu_back->setVisible(true);
			notice_menu->setVisible(true);
			notice_back->setVisible(true);
			m_empty_back->setVisible(false);
			m_empty_notice->setVisible(false);
		}else{
			m_empty_back->setVisible(true);
			m_empty_notice->setVisible(true);
			menu_back->setVisible(false);
			notice_menu->setVisible(false);
			notice_back->setVisible(false);
		}
		CCSize winSize = CCDirector::sharedDirector()->getWinSize();
		tableView = CCListView::create(this,CCSizeMake(winSize.width, 608),NULL,160.0f,0.0f,160.0f,0.0f);
		tableView->setAnchorPoint(ccp(0,1));
		tableView->setPosition(0,188);
		tableView->setDelegate(this);
		tableView->setDirection(kCCScrollViewDirectionHorizontal);
		tableView->setVerticalFillOrder(kCCListViewFillTopDown);
		tableView->setSelection(0);
		this->addChild(tableView);
	}else{

	}
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

void HistoryScnce::keyMenuClicked() {
	if(m_dates.size()<=0){
		return;
	}
	if(isEditeStatue){
		keyBackClicked();
	}else{
		isEditeStatue = true;
		CCSprite* notice_menu = (CCSprite*)getChildByTag(12);
		CCSprite* notice_back = (CCSprite*)getChildByTag(11);
		CCSprite* notice_menu_back = (CCSprite*)getChildByTag(10);

		notice_menu->setVisible(false);
		notice_back->setVisible(false);
		notice_menu_back->setVisible(true);

		m_button_delete->setVisible(true);
		m_button_select_all->setVisible(true);

	//	m_button_select_all->setSelected(true);
	//	m_selected_button = m_button_select_all;
		for(int i=0; i<m_dates.size(); i++){
			PlayHistoryInfo info = m_dates.at(i);
			info.setEditeStatue(mEdite);
			m_dates[i] = info;
			LOGD("HistoryScnce","info setEditeStatue %d",info.getEditeStatue());
		}
		tableView->reloadData();
	}

}

void HistoryScnce::callBackAnim(CCNode* sender, CCLabelTTF* pLabel) {
	pLabel->setDimensions(ccp(270, 240));
}


