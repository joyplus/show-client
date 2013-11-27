#include "XunLeiYunSence.h"
#include "XunLeiBTdetailSence.h"

XunLeiYunSence::~XunLeiYunSence()
{
	// TODO Auto-generated destructor stub
}

static void xunLeiDilogCallbackFunc(bool isBack,void* ctx)
{
	XunLeiYunSence* thiz = (XunLeiYunSence*)ctx;
	if(isBack){
		thiz->popSence();
	}else{
		thiz->loginXunleiSuccess();
	}
}

bool XunLeiYunSence::init()
{
	bool bRet = false;
	do
	{
		if(! CCLayer::init())
		{
			LOGD("PageLayer","CCLayer init fail");
			break;
		}

		m_dates.clear();
		m_req_index = 0;
		m_hasMore = true;
		m_isRequesting = false;

		CCSize winSize = CCDirector::sharedDirector()->getWinSize();

		CCSprite* loading = CCSprite::create("waiting.png");
		loading->setPosition(ccp(winSize.width/2,winSize.height/2));
		loading->setTag(250);
		loading->runAction(CCRepeatForever::create(CCRotateBy::create(0.1f,36.0f)));
		addChild(loading);
//		CCEditBox *m_pEditName = CCEditBox::create(CCSizeMake(1000,50),CCScale9Sprite::create("green_edit.png"));
//		m_pEditName->setPosition(ccp(winSize.width/2,winSize.height/2));
//
//		m_pEditName->setFontSize(5);
//		m_pEditName->setFontColor(ccRED);
//		m_pEditName->setPlaceHolder("Name:");
//		m_pEditName->setPlaceholderFontColor(ccWHITE);
//		m_pEditName->setMaxLength(18);
//
//		this->addChild(m_pEditName);
//
//		usrNmaeEditView = CCTextFieldTTF::textFieldWithPlaceHolder("密码", "Arial", 34);
//		usrNmaeEditView->setPosition(ccp(winSize.width/2,winSize.height/3+230));
//		usrNmaeEditView->setColor(ccc3(0, 255, 0));
//		usrNmaeEditView->setTag(TAG_XUNLEI_ACCOUNT);
//		usrNmaeEditView->setDelegate(this);
//		usrNmaeEditView->attachWithIME();
//
//		m_selectedTag = TAG_XUNLEI_ACCOUNT;
//
//		this->addChild(usrNmaeEditView);
//
//		passWordEditView = CCTextFieldTTF::textFieldWithPlaceHolder("名称", "Arial", 34);
//		passWordEditView->setPosition(ccp(winSize.width/2,winSize.height/3+330));
//		passWordEditView->setColor(ccc3(0, 255, 0));
//		passWordEditView->setDelegate(this);
//		passWordEditView->setTag(TAG_XUNLEI_PASSWORD);
//		this->addChild(passWordEditView);

//		this->setKeypadEnabled(true);
		bRet = true;
	} while (0);
	return bRet;
}

void XunLeiYunSence::keyBackClicked()
{
//	if(usrNmaeEditView->)
//	{
//		usrNmaeEditView->detachWithIME();
//	}
	LOGD("XunLeiYunSence","----keyBackClicked-----");
	popSence();
}



void XunLeiYunSence::keyArrowClicked(int arrow)
{
	if(tableView->onKeyArrowClicked(arrow)) return;
	switch (arrow)
		{
			case ccKeypadMSGType(kTypeLeftArrowClicked):
				break;
			case ccKeypadMSGType(kTypeUpArrowClicked):
				if(tableView->getSelected()>0){
					tableView->setSelection(tableView->getSelected()-5);
				}else{
					if(m_selectedButton->getTag()==7){
						m_selectedButton->setSelected(false);
						CCButtonView * button = (CCButtonView *)m_selectedCell->getChildByTag(6);
						button->setSelected(true);
						m_selectedButton = button;
					}
				}
				break;
			case ccKeypadMSGType(kTypeRightArrowClicked):
				break;
			case ccKeypadMSGType(kTypeDownArrowClicked):
				if(tableView->getSelected()>0){
					tableView->setSelection(tableView->getSelected()+5);
				}else{
					if(m_selectedButton->getTag()==6){
						m_selectedButton->setSelected(false);
						CCButtonView * button = (CCButtonView *)m_selectedCell->getChildByTag(7);
						button->setSelected(true);
						m_selectedButton = button;
					}
				}
				break;
		}
}

void XunLeiYunSence::keyEnterClicked()
{
	if(tableView->onKeyEnterClicked()) return;
}

void XunLeiYunSence::loginXunleiSuccess()
{
//	CCSprite* loading = (CCSprite*)getChildByTag(250);
//	loading->setVisible(true);
//	loading->runAction(CCRepeatForever::create(CCRotateBy::create(0.1f,36.0f)));
	LOGD("XunLeiYunSence","loginXunlei ->loginXunleiSuccess");
	string userString = getXunLeiUserInfoJNI();
	CSJson::Value jsonobj;
	CSJson::Reader reader;
	reader.parse(userString,jsonobj);
	string nickname = jsonobj["nickname"].asString();
	string username = jsonobj["usrname"].asString();
	int level = jsonobj["level"].asInt();
	LOGD("XunLeiYunSence","nickname -> %s",nickname.c_str());
	LOGD("XunLeiYunSence","username -> %s",username.c_str());
	m_userInfo.setName(nickname);
	m_userInfo.setVipLevel(level);
	m_req_index = 0;
	m_hasMore = true;
	getXunleiVideoList(m_req_index);
}

void XunLeiYunSence::getXunleiVideoList(int index) {
	if(!m_hasMore){
		return;
	}
	m_isRequesting = true;
	string cookies = getXunleiCookiesJNI();
	CCHttpClient* httpClient = CCHttpClient::getInstance();
	CCHttpRequest* httpReq =new CCHttpRequest();
	httpReq->setRequestType(CCHttpRequest::kHttpGet);
//	String listUrl = "http://i.vod.xunlei.com/req_history_play_list/req_num/"
//						+ cacheNum + "/req_offset/" + cacheNum * (pageIndex - 1);

	const char* url_p = "http://i.vod.xunlei.com/req_history_play_list/req_num/%d/req_offset/%d?type=all&order=create&t=%s";
//	char url[128] = {};
//	sprintf(url,url_p,30,30*(index),getCurrentTimeJNI().c_str());
	CCString * url = CCString::createWithFormat(url_p,30,30*(index),getCurrentTimeJNI().c_str());
	LOGD("XunLeiYunSence","url -- > %s",url->getCString());
	httpReq->setUrl(url->getCString());
	std::vector	<std::string> pHeaders;
	string key_cookies = "cookie: "+cookies;
//	string key_type = "type: all";
//	string key_order = "order: create";
//	string key_time = "t: " + getCurrentTimeJNI();
//	const char* requsetDate_p = "";
//	char requsetDate[64] = {};
//	sprintf(requsetDate,requsetDate_p,getCurrentTimeJNI().c_str());
	pHeaders.push_back(key_cookies);
	httpReq->setHeaders(pHeaders);
	//httpReq->setRequestData(requsetDate,strlen(requsetDate));
	httpReq->setResponseCallback(this,callfuncND_selector(XunLeiYunSence::onGetXunleiVideoListComplete));
	httpClient->setTimeoutForConnect(30);
	httpClient->send(httpReq);
	httpReq->release();
	httpReq=NULL;
}

void XunLeiYunSence::onGetXunleiVideoListComplete(CCNode* node, CCObject* obj) {
	m_isRequesting = false;
	CCHttpResponse *response = (CCHttpResponse*)obj;
	 if (!response)
	 {
		return;
	 }
	 int statusCode = response->getResponseCode();
	 if (response->isSucceed()){
		 std::vector<char> *buffer = response->getResponseData();
		 string buff(buffer->begin(),buffer->end());
		 LOGD("XunLeiYunSence","response---->%s",buff.c_str());
		 CSJson::Value jsonobj;
		 CSJson::Reader reader;
		 const char * pic_p = "http://i0.xlpan.kanimg.com/pic/%s_X168.jpg";
		 if(reader.parse(buff,jsonobj))
		 {
			 const CSJson::Value arrayObj = jsonobj["resp"]["history_play_list"];
			 if(jsonobj["resp"]["ret"].asInt()==1){
				showXunLeiLoginDialog(xunLeiDilogCallbackFunc,this);
				return;
			 }
			 if(arrayObj.size()<30){
				 m_hasMore = false;
			 }else{
				 m_hasMore = true;
			 }
			 m_req_index += 1;
			 LOGD("XunLeiYunSence","history_play_list size =  %d",arrayObj.size());
			 for (int i=0; i<arrayObj.size(); i++) {
			   XunLeiVideInfo videoInfo;
			   videoInfo.setCreateTime(arrayObj[i]["createtime"].asString());
			   LOGD("XunLeiYunSence","%d --> create time = %s",i,videoInfo.getCreateTime().c_str());
			   videoInfo.setDuration(arrayObj[i]["duration"].asInt());
			   LOGD("XunLeiYunSence","%d --> Duration = %d",i,videoInfo.getDuration());
			   videoInfo.setFileName(getDecodeStringFromJNI(arrayObj[i]["file_name"].asString().c_str()));
			   LOGD("XunLeiYunSence","%d --> FileName = %s",i,videoInfo.getFileName().c_str());
			   videoInfo.setFilesize(arrayObj[i]["file_size"].asDouble());
			   LOGD("XunLeiYunSence","%d --> Filesize = %d",i,videoInfo.getFilesize());
			   videoInfo.setGcid(arrayObj[i]["gcid"].asString());
			   LOGD("XunLeiYunSence","%d --> Gcid = %s",i,videoInfo.getGcid().c_str());
			   videoInfo.setSrcUrl(getDecodeStringFromJNI(arrayObj[i]["src_url"].asString().c_str()));
			   LOGD("XunLeiYunSence","%d --> src_url = %s",i,getDecodeStringFromJNI(videoInfo.getSrcUrl().c_str()).c_str());
			   if(videoInfo.getSrcUrl().find(string("bt://"))<videoInfo.getSrcUrl().length()){//包含
				   videoInfo.setIsDir(true);
			   }else{
				   videoInfo.setIsDir(false);
			   }
			   LOGD("XunLeiYunSence","%d --> setIsDir = %s",i, videoInfo.isIsDir()?"true":"false");
			   char pic_url[128] = {};
			   sprintf(pic_url,pic_p,videoInfo.getGcid().c_str());
			   LOGD("XunLeiYunSence","%d -->  pic_url = %s",i,pic_url);
			   videoInfo.setPicUrl(string(pic_url));

			   m_dates.push_back(videoInfo);
			 }
			 if(m_req_index==1){
				 initTableView();
			 }else{
				 tableView->reloadData();
			 }
		 }else
		 {
			 LOGD("XunLeiYunSence","json parse Filed");
		 }


	 }else{
		 LOGD("XunLeiYunSence","getVideoList Filed");
	 }
}

cocos2d::CCScene* XunLeiYunSence::scene()
{
	CCScene * scene = NULL;
	do
	{
		// 'scene' is an autorelease object
		scene = CCScene::create();
		CC_BREAK_IF(! scene);
		// 'layer' is an autorelease object
		XunLeiYunSence *layer = XunLeiYunSence::create();
		CC_BREAK_IF(! layer);
		// add layer as a child to scene
		scene->addChild(layer);
	} while (0);

	// return the scene
	return scene;
}

void XunLeiYunSence::popSence() {
	CCScene *prevScene = CCDirector::sharedDirector()->previousScene();
	CCDirector::sharedDirector()->popScene(CCTransitionSlideInL::create(0.2f, prevScene));
}

void XunLeiYunSence::tableCellClicked(CCListView* table, CCTableViewCell* cell,
		unsigned int idx) {
	if(idx == 0){
		if(m_selectedButton->getTag()==6){//刷新
			m_hasMore = true;
			m_dates.clear();
			m_req_index = 0;
			getXunleiVideoList(m_req_index);
			CCSprite* loading = (CCSprite*)getChildByTag(250);
			loading->setZOrder(2);
			loading->setVisible(true);
			loading->runAction(CCRepeatForever::create(CCRotateBy::create(0.1f,36.0f)));
		}else{//注销
			m_dates.clear();
			tableView->setVisible(false);
	//		tableView->reloadData();
			CCSprite* loading = (CCSprite*)getChildByTag(250);
			loading->setVisible(true);
			loading->runAction(CCRepeatForever::create(CCRotateBy::create(0.1f,36.0f)));
			showXunLeiLoginDialog(xunLeiDilogCallbackFunc,this);
		}
	}else{
		XunLeiVideInfo info = m_dates.at(idx-1);
		if(info.isIsDir()){
			CCDirector::sharedDirector()->pushScene(CCTransitionSlideInR::create(0.2f,XunLeiBTdetailSence::scene(info)));
		}else{
			CSJson::Value root;
			CSJson::Value jsonobj;
			jsonobj["createTime"] = info.getCreateTime();
			jsonobj["duration"] = (double)info.getDuration();
			jsonobj["file_name"] = info.getFileName();
			jsonobj["filesize"] = info.getFilesize();
			jsonobj["gcid"] = info.getGcid();
			jsonobj["src_url"] = info.getSrcUrl();
			jsonobj["userid"] = info.getUserid();
			jsonobj["isDir"] = 0;
			root["date"] = jsonobj;
			root["type"] = 2;
			CSJson::FastWriter writer;
	//		LOGD("HistoryScnce","baidu play video ---> %s",root.toStyledString().c_str());
			playVideoJNI(writer.write(root).c_str());
		}
	}
}

void XunLeiYunSence::tableCellSelected(CCListView* table, CCTableViewCell* cell,
		unsigned int idx) {
	LOGD("HistoryScnce","item %u Selected",idx);
	if(idx==0){
		if(m_selectedCell)
		{
			CCTableCellForHistory * sLabelBack = (CCTableCellForHistory*)m_selectedCell->getChildByTag(3);
			sLabelBack->stopAllActions();
			sLabelBack->runAction(CCMoveTo::create(0.2f,ccp(0,540)));
			CCLabelTTF *pLabel = (CCLabelTTF*)m_selectedCell->getChildByTag(4);
			pLabel->setDimensions(ccp(270, 150));
		}
		if(cell){
			//、、
			CCButtonView *button = (CCButtonView *)cell->getChildByTag(6);
			button->setSelected(true);
			m_selectedButton = button;
		}
	}else{
		if(m_selected_id==0&&idx==1){
			if(m_selectedButton){
				m_selectedButton->setSelected(false);
			}
			if(cell){
				CCTableCellForHistory * pLabelBack = (CCTableCellForHistory*)cell->getChildByTag(3);
				pLabelBack->stopAllActions();
	//			pLabelBack->runAction(CCMoveTo::create(0.2f,ccp(0,405)));
				CCLabelTTF *pLabel = (CCLabelTTF*)cell->getChildByTag(4);
				CCFiniteTimeAction* actions=CCSequence::create(CCMoveTo::create(0.2f,ccp(0,450)),
								CCCallFuncND::create(this,
										callfuncND_selector(XunLeiYunSence::callBackAnim),
										pLabel),NULL);
				pLabelBack->runAction(actions);
			}
		}else{
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
				CCLabelTTF *pLabel = (CCLabelTTF*)cell->getChildByTag(4);
				CCFiniteTimeAction* actions=CCSequence::create(CCMoveTo::create(0.2f,ccp(0,450)),
								CCCallFuncND::create(this,
										callfuncND_selector(XunLeiYunSence::callBackAnim),
										pLabel),NULL);
				pLabelBack->runAction(actions);
	//			pLabelBack->runAction(CCMoveTo::create(0.2f,ccp(0,405)));
			}
		}
		m_selectedButton = NULL;
	}
	m_selectedCell = cell;
	m_selected_id = idx;
	if(idx>(tableView->getDataSource()->numberOfCellsInTableView(tableView)-10)&&m_hasMore&&!m_isRequesting){
		getXunleiVideoList(m_req_index);
	}
}

CCSize XunLeiYunSence::tableCellSizeForIndex(CCListView* table,
		unsigned int idx) {
	return CCSizeMake(320,table->getViewSize().height);
}

CCTableViewCell* XunLeiYunSence::tableCellAtIndex(CCListView* table,
		unsigned int idx) {


//	CCString *pString = CCString::createWithFormat("%s", info.getName().c_str());
	CCTableViewCell *pCell = table->dequeueCell();
	CCImageView *pImage;
	CCSprite *pSprite;
	CCTableCellForHistory *pLabelBack;
	CCLabelTTF *pLabel;
	CCLabelTTF *pTimeLabel;
	CCButtonView * pButton;
	CCButtonView * pRefreshButton;
	if (!pCell) {
		pCell = new CCTableViewCell();
		pCell->autorelease();
		pImage = new CCImageView();
		pImage->setPosition(ccp(165,506));
		pImage->setTag(1);
		pImage->autorelease();
		pCell->addChild(pImage);
		pSprite = CCSprite::create("xunlei_thumb.png");
		pSprite->setAnchorPoint(CCPointZero);
		pSprite->setPosition(ccp(0,405));
		pSprite->setTag(2);
		pCell->addChild(pSprite);
		pLabelBack = CCTableCellForHistory::create("xunlei_card_activated.png");
		pLabelBack->setAnchorPoint(ccp(0,1));
		pLabelBack->setPosition(ccp(0,540));
		pLabelBack->setTag(3);
		pCell->addChild(pLabelBack);
		pLabel = CCLabelTTF::create("", "Arial", 27.0, CCSizeMake(270, 150), CCTextAlignment(kCCTextAlignmentLeft));
		pLabel->setPosition(ccp(35,330));
//		pLabel->setPosition(ccp(35,300));
		pLabel->setAnchorPoint(ccp(0,1));
		pLabel->setTag(4);
		pCell->addChild(pLabel);
		pTimeLabel = CCLabelTTF::create("","Arial", 27.0);
		pTimeLabel->setPosition(ccp(170,350));
		pTimeLabel->setTag(5);
		pCell->addChild(pTimeLabel);

		pButton = CCButtonView::create("xunlei_button_refresh.png","xunlei_selected_button.png","刷  新",70,0,0,0);
		pButton->setAnchorPoint(ccp(0.5,0.5));
		pButton->setPosition(ccp(165,178));
		pButton->setTag(6);
		pCell->addChild(pButton);

		pRefreshButton = CCButtonView::create("xunlei_button_logout.png","xunlei_selected_button.png","注  销",70,0,0,0);
		pRefreshButton->setAnchorPoint(ccp(0.5,0.5));
		pRefreshButton->setPosition(ccp(165,89));
		pRefreshButton->setTag(7);
		pCell->addChild(pRefreshButton);
	}
	else
	{
		pImage = (CCImageView*)pCell->getChildByTag(1);
		pSprite = (CCSprite*)pCell->getChildByTag(2);
		pLabelBack = (CCTableCellForHistory*)pCell->getChildByTag(3);
		pLabel = (CCLabelTTF*)pCell->getChildByTag(4);
		pTimeLabel = (CCLabelTTF*)pCell->getChildByTag(5);
		pButton = (CCButtonView*)pCell->getChildByTag(6);
		pRefreshButton= (CCButtonView*)pCell->getChildByTag(7);
	}
	if(idx == 0){
		pSprite->initWithFile("xunlei_id.png");
		pSprite->setAnchorPoint(CCPointZero);
		pSprite->setPosition(ccp(0,405));
//		pLabelBack->setPosition(ccp(0,540));
		pLabelBack->setPosition(ccp(0,632));
		pImage->setVisible(false);
		pLabel->setHorizontalAlignment(CCTextAlignment(kCCTextAlignmentCenter));
		pLabel->setString(m_userInfo.getName().c_str());
		pButton->setVisible(true);
		pRefreshButton->setVisible(true);
	}else{
		XunLeiVideInfo info = m_dates.at(idx-1);
		pLabel->setString(info.getFileName().c_str());
		pLabel->setHorizontalAlignment(CCTextAlignment(kCCTextAlignmentLeft));
		if(info.isIsDir()){
			pSprite->initWithFile("xunlei_thumb_folder.png");
		}else{
			pSprite->initWithFile("xunlei_thumb.png");
		}
		pSprite->setAnchorPoint(CCPointZero);
		pSprite->setPosition(ccp(0,405));
		if(idx == table->getSelected()){
			pLabelBack->setPosition(ccp(0,450));
			m_selectedCell = pCell;
		}else{
			pLabelBack->setPosition(ccp(0,540));
		}
		pImage->setVisible(true);
		pImage->initWithUrl(info.getPicUrl().c_str(),"default_video_photo.png");
		pImage->setBoundSize(ccp(264,145));
		pButton->setVisible(false);
		pRefreshButton->setVisible(false);
	}
	return pCell;
}

unsigned int XunLeiYunSence::numberOfCellsInTableView(CCListView* table) {
	return m_dates.size()+1;
//	return 2;
}

void XunLeiYunSence::callBackAnim(CCNode* sender, CCLabelTTF* pLabel) {
	pLabel->setDimensions(ccp(270, 240));
}

void XunLeiYunSence::onEnterTransitionDidFinish() {
	CCLayer::onEnterTransitionDidFinish();
	LOGD("XunLeiYunSence","----------onEnterTransitionDidFinish----------");
	CCSprite* loading = (CCSprite*)getChildByTag(250);
//	loading->setVisible(false);
	if(loading->isVisible()){
		if(getXunleiCookiesJNI().empty()){
			showXunLeiLoginDialog(xunLeiDilogCallbackFunc, (void*)this);
		}else{
			loginXunleiSuccess();
		}
	}
	this->setKeypadEnabled(true);

}

void XunLeiYunSence::onExitTransitionDidStart() {
	CCLayer::onExitTransitionDidStart();
	LOGD("XunLeiYunSence","----------onExitTransitionDidStart----------");
	this->setKeypadEnabled(false);

}

void XunLeiYunSence::initTableView() {
	CCSprite* loading = (CCSprite*)getChildByTag(250);
	loading->stopAllActions();
	loading->setVisible(false);
	CCSize winSize = CCDirector::sharedDirector()->getWinSize();
	if(tableView){
		tableView->setVisible(true);
		tableView->reloadData();
		tableView->setSelection(1);
	}else{
		tableView = CCListView::create(this,CCSizeMake(winSize.width, 608),NULL,160.0f,0.0f,160.0f,0.0f);
		tableView->setAnchorPoint(ccp(0,1));
		tableView->setPosition(0,188);
		tableView->setDelegate(this);
		tableView->setDirection(kCCScrollViewDirectionHorizontal);
		tableView->setVerticalFillOrder(kCCListViewFillTopDown);
		tableView->setSelection(1);
		this->addChild(tableView);
	}
}




