#include "XunLeiBTdetailSence.h"

XunLeiBTdetailSence::~XunLeiBTdetailSence() {
	// TODO Auto-generated destructor stub
}

bool XunLeiBTdetailSence::init() {
	bool bRet = false;
		do
		{
			if(! CCLayer::init())
			{
				LOGD("XunLeiBTdetailSence","CCLayer init fail");
				break;
			}
			m_selectedCell = NULL;

			CCSize winSize = CCDirector::sharedDirector()->getWinSize();
			CCSprite* loading = CCSprite::create("waiting.png");
			loading->setPosition(ccp(winSize.width/2,winSize.height/2));
			loading->setTag(250);
			loading->runAction(CCRepeatForever::create(CCRotateBy::create(0.1f,36.0f)));
			addChild(loading);
			tableView = CCListView::create(this,CCSizeMake(winSize.width, 608),NULL,160.0f,0.0f,160.0f,0.0f);
			tableView->setAnchorPoint(ccp(0,1));
			tableView->setPosition(0,188);
			tableView->setDelegate(this);
			tableView->setDirection(kCCScrollViewDirectionHorizontal);
			tableView->setVerticalFillOrder(kCCListViewFillTopDown);
			tableView->setSelection(0);
			this->addChild(tableView);
//			this->setKeypadEnabled(true);
			bRet = true;
		} while (0);
		return bRet;
}

void XunLeiBTdetailSence::keyBackClicked() {
	CCScene *prevScene = CCDirector::sharedDirector()->previousScene();
	CCDirector::sharedDirector()->popScene(CCTransitionSlideInL::create(0.2f, prevScene));
}

void XunLeiBTdetailSence::keyArrowClicked(int arrow) {
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

void XunLeiBTdetailSence::keyEnterClicked() {
	if(tableView->onKeyEnterClicked()) return;
}

void XunLeiBTdetailSence::tableCellClicked(CCListView* table,
		CCTableViewCell* cell, unsigned int idx) {
	LOGD("XunLeiBTdetailSence","item %u Clicked",idx);
	if(idx>m_dates.size()||idx<0){
		return;
	}
	XunLeiVideInfo info = m_dates.at(idx);
	CSJson::Value root;
	CSJson::Value jsonobj;
	CSJson::Value arrayObj;
	CSJson::FastWriter writer;
	jsonobj["createTime"] = m_xunLeiVideInfo.getCreateTime();
	jsonobj["duration"] = (double)m_xunLeiVideInfo.getDuration();
	jsonobj["file_name"] = m_xunLeiVideInfo.getFileName();
	jsonobj["filesize"] = m_xunLeiVideInfo.getFilesize();
	jsonobj["gcid"] = m_xunLeiVideInfo.getGcid();
	jsonobj["src_url"] = m_xunLeiVideInfo.getSrcUrl();
	jsonobj["userid"] = m_xunLeiVideInfo.getUserid();
	for(int i=0; i<m_dates.size();i++){
		CSJson::Value item;
		XunLeiVideInfo info_item = m_dates.at(i);
		item["createTime"] = m_xunLeiVideInfo.getCreateTime();
		item["duration"] = (double)info_item.getDuration();
		item["file_name"] = info_item.getFileName();
		item["filesize"] = info_item.getFilesize();
		item["gcid"] = info_item.getGcid();
		item["src_url"] = info_item.getSrcUrl();
		item["userid"] = m_xunLeiVideInfo.getUserid();
		arrayObj.append(item);
	}
	jsonobj["item_list"] = arrayObj;
	jsonobj["sub_name"] = info.getFileName();
	jsonobj["isDir"] = 1;
	root["date"] = jsonobj;
	root["type"] = 2;
	playVideoJNI(writer.write(root).c_str());

}

void XunLeiBTdetailSence::tableCellSelected(CCListView* table,
		CCTableViewCell* cell, unsigned int idx) {
	LOGD("XunLeiBTdetailSence","item %u Selected",idx);
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
									callfuncND_selector(XunLeiBTdetailSence::callBackAnim),
									pLabel),NULL);
			pLabelBack->runAction(actions);
		}
		m_selectedCell = cell;
}

CCSize XunLeiBTdetailSence::tableCellSizeForIndex(CCListView* table,
		unsigned int idx) {
	return CCSizeMake(320,table->getViewSize().height);
}

CCTableViewCell* XunLeiBTdetailSence::tableCellAtIndex(CCListView* table,
		unsigned int idx) {
	XunLeiVideInfo info = m_dates.at(idx);
	CCString *pString = CCString::createWithFormat("%s", info.getFileName().c_str());
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
	}
	else
	{
		pImage = (CCImageView*)pCell->getChildByTag(1);
		pSprite = (CCSprite*)pCell->getChildByTag(2);
		pLabelBack = (CCTableCellForHistory*)pCell->getChildByTag(3);
		pLabel = (CCLabelTTF*)pCell->getChildByTag(4);
		pTimeLabel = (CCLabelTTF*)pCell->getChildByTag(5);
	}
	pLabel->setString(info.getFileName().c_str());
	if(idx == table->getSelected()){
		pLabelBack->setPosition(ccp(0,450));
		pLabel->setDimensions(ccp(270, 240));
		m_selectedCell = pCell;
	}else{
		pLabelBack->setPosition(ccp(0,540));
		pLabel->setDimensions(ccp(270, 150));
	}
//	if(info.getDuration()-info.getPlaybackTime()<10&&info.getDuration()>10){
//		pTimeLabel->setString("已看完");
//	}else{
//		CCString* time = CCString::createWithFormat("%s/%s",fomartTime(info.getPlaybackTime()).c_str(),
//										fomartTime(info.getDuration()).c_str());
//		pTimeLabel->setString(time->getCString());
//	}
	pImage->setVisible(true);
	pImage->initWithUrl(info.getPicUrl().c_str(),"default_video_photo.png");
	pImage->setBoundSize(ccp(264,145));
	return pCell;
}

unsigned int XunLeiBTdetailSence::numberOfCellsInTableView(
		CCListView* table) {
//	LOGD("XunLeiBTdetailSence","size -->%d name -> %s",m_playHistoryInfo.getBtepisodes().size(),m_playHistoryInfo.getName().c_str());
	return m_dates.size();
}

CCScene* XunLeiBTdetailSence::scene(XunLeiVideInfo info) {
	CCScene * scene = NULL;
	do
	{
		// 'scene' is an autorelease object
		scene = CCScene::create();
		CC_BREAK_IF(! scene);
		// 'layer' is an autorelease object
		XunLeiBTdetailSence* layer = XunLeiBTdetailSence::create(info);
		CC_BREAK_IF(! layer);
		// add layer as a child to scene
		scene->addChild(layer);
	} while (0);

	// return the scene
	return scene;
}

void XunLeiBTdetailSence::callBackAnim(CCNode* sender, CCLabelTTF* pLabel) {
	pLabel->setDimensions(ccp(270, 240));
}

void XunLeiBTdetailSence::getChilds() {
	string cookies = getXunleiCookiesJNI();
		CCHttpClient* httpClient = CCHttpClient::getInstance();
		CCHttpRequest* httpReq =new CCHttpRequest();
		httpReq->setRequestType(CCHttpRequest::kHttpGet);
		string str = m_xunLeiVideInfo.getSrcUrl();
		LOGD("XunLeiBTdetailSence","bt src -- >%s",str.c_str());
		string str_sub;
		if(str.length()>5){
			str_sub = str.substr(5);
		}
		LOGD("XunLeiBTdetailSence","sub bt src -- >%s",str_sub.c_str());
		const char* url_p = "http://i.vod.xunlei.com/req_subBT/info_hash/%s/req_num/1000/req_offset/0";
		CCString * url = CCString::createWithFormat(url_p,str_sub.c_str());
		LOGD("XunLeiYunSence","url -- > %s",url);
		httpReq->setUrl(url->getCString());
		std::vector	<std::string> pHeaders;
		string key_cookies = "cookie: "+cookies;
		pHeaders.push_back(key_cookies);
		httpReq->setHeaders(pHeaders);
		//httpReq->setRequestData(requsetDate,strlen(requsetDate));
		httpReq->setResponseCallback(this,callfuncND_selector(XunLeiBTdetailSence::onGetChildsComplete));
		httpClient->setTimeoutForConnect(30);
		httpClient->send(httpReq);
		httpReq->release();
		httpReq=NULL;
}

void XunLeiBTdetailSence::onEnterTransitionDidFinish() {
	CCLayer::onEnterTransitionDidFinish();
	this->setKeypadEnabled(true);
	getChilds();
	LOGD("XunLeiBTdetailSence","----------onEnterTransitionDidFinish----------");
}

void XunLeiBTdetailSence::onExitTransitionDidStart() {
	CCLayer::onExitTransitionDidStart();
	this->setKeypadEnabled(false);
	LOGD("XunLeiBTdetailSence","----------onExitTransitionDidStart----------");
}

void XunLeiBTdetailSence::onGetChildsComplete(CCNode* node, CCObject* obj) {
	CCHttpResponse *response = (CCHttpResponse*)obj;
	if (!response)
	 {
		return;
	 }
	 int statusCode = response->getResponseCode();
	 if (response->isSucceed()){
		 std::vector<char> *buffer = response->getResponseData();
		 string buff(buffer->begin(),buffer->end());
		 LOGD("XunLeiBTdetailSence","response---->%s",buff.c_str());
		 CSJson::Value root;
		 CSJson::Reader reader;
		 const char * pic_p = "http://i0.xlpan.kanimg.com/pic/%s_X168.jpg";
		 if(reader.parse(buff,root))
		 {
			 CSJson::Value jsonobj = root["resp"]["subfile_list"];
			 for(int i = 0; i<jsonobj.size(); i++){
				 XunLeiVideInfo info;
				 info.setCreateTime(m_xunLeiVideInfo.getCreateTime());
				 info.setFileName(getDecodeStringFromJNI(jsonobj[i]["name"].asString().c_str()));
				 info.setGcid(jsonobj[i]["gcid"].asString());
				 int index = jsonobj[i]["index"].asInt();
				 CCString* src_url = CCString::createWithFormat("%s/%d",m_xunLeiVideInfo.getSrcUrl().c_str(),index);
				 info.setSrcUrl(string(src_url->getCString()));
				 CCString* pic_url = CCString::createWithFormat(pic_p,info.getGcid().c_str());
				 info.setPicUrl(string(pic_url->getCString()));
				 info.setFilesize(jsonobj[i]["file_size"].asDouble());
				 info.setUserid(m_xunLeiVideInfo.getUserid());
				 m_dates.push_back(info);
			 }
			 CCSprite* loading = (CCSprite*)getChildByTag(250);
			 loading->stopAllActions();
			 loading->setVisible(false);
			 tableView->reloadData();
			 tableView->setSelection(0);
		 }else{
			 LOGD("XunLeiBTdetailSence","json parse Filed");
		 }
	 }else{
		 LOGD("XunLeiBTdetailSence","getVideoList Filed");
	 }
}




