#include "BaiduYunSence.h"

BaiduYunSence::~BaiduYunSence() {
	// TODO Auto-generated destructor stub
}

static void baiduDilogCallbackFunc(bool isBack,void* ctx)
{
	BaiduYunSence* thiz = (BaiduYunSence*)ctx;
	if(isBack){
		thiz->popSence();
	}else{
		thiz->loginXunleiSuccess();
	}
}

bool BaiduYunSence::init() {

	bool bRet = false;
		do
		{
			if(! CCLayer::init())
			{
				LOGD("PageLayer","CCLayer init fail");
				break;
			}

			m_dates.clear();
			m_requset_baidu_index = 0;
			m_hasMore = true;
			m_isRequesting = false;

			CCSize winSize = CCDirector::sharedDirector()->getWinSize();

			if(getBaiduTokenJNI().empty()){
				showBaiduLoginDialog(baiduDilogCallbackFunc, (void*)this);
			}else{
//				getBaiduVideoList(m_requset_baidu_index);
				getBaiduLoginUserInfo();
			}

			this->setKeypadEnabled(true);
			bRet = true;
		} while (0);
		return bRet;
}

void BaiduYunSence::keyBackClicked() {
	popSence();
}

void BaiduYunSence::keyArrowClicked(int arrow) {
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

void BaiduYunSence::keyEnterClicked() {
	if(tableView->onKeyEnterClicked()) return;
}

void BaiduYunSence::popSence() {
	CCScene *prevScene = CCDirector::sharedDirector()->previousScene();
	CCDirector::sharedDirector()->popScene(CCTransitionSlideInL::create(0.2f, prevScene));
}

void BaiduYunSence::loginXunleiSuccess() {
	string access_token = getBaiduTokenJNI();
	LOGD("BaiduYunSence","access_token--> %s",access_token.c_str());
//	getBaiduVideoList(0);
//	getBaiduVideoList(m_requset_baidu_index);
	getBaiduLoginUserInfo();
}

cocos2d::CCScene* BaiduYunSence::scene() {
	CCScene * scene = NULL;
	do
	{
		// 'scene' is an autorelease object
		scene = CCScene::create();
		CC_BREAK_IF(! scene);
		// 'layer' is an autorelease object
		BaiduYunSence *layer = BaiduYunSence::create();
		CC_BREAK_IF(! layer);
		// add layer as a child to scene
		scene->addChild(layer);
	} while (0);

	// return the scene
	return scene;
}

void BaiduYunSence::tableCellClicked(CCListView* table, CCTableViewCell* cell,
		unsigned int idx) {
	LOGD("HistoryScnce","item %u Clicked m_dates size = %d",idx-1, m_dates.size());
	if(idx == 0){
		//注销
	}else{
		BaiduVideoInfo info = m_dates.at(idx-1);
		CSJson::Value root;
		CSJson::Value jsonobj;
		jsonobj["fs_id"] = (double)info.getFsId();
		jsonobj["path"] = info.getPath();
		jsonobj["filename"] = info.getFileName();
		root["date"] = jsonobj;
		root["type"] = 3;
		CSJson::FastWriter writer;
//		LOGD("HistoryScnce","baidu play video ---> %s",root.toStyledString().c_str());
		playVideoJNI(writer.write(root).c_str());
	}
}

void BaiduYunSence::tableCellSelected(CCListView* table, CCTableViewCell* cell,
		unsigned int idx) {
	LOGD("HistoryScnce","item %u Selected",idx);
	if(idx==0){
		if(m_selectedCell)
		{
			CCTableCellForHistory * sLabelBack = (CCTableCellForHistory*)m_selectedCell->getChildByTag(3);
			sLabelBack->stopAllActions();
			sLabelBack->runAction(CCMoveTo::create(0.2f,ccp(0,540)));
		}
		if(cell){
			//、、
		}
		m_selectedCell = NULL;
	}else{
		if(m_selectedCell)
		{
			CCTableCellForHistory * sLabelBack = (CCTableCellForHistory*)m_selectedCell->getChildByTag(3);
			sLabelBack->stopAllActions();
			sLabelBack->runAction(CCMoveTo::create(0.2f,ccp(0,540)));
		}
		if(cell){
			CCTableCellForHistory * pLabelBack = (CCTableCellForHistory*)cell->getChildByTag(3);
			pLabelBack->stopAllActions();
			pLabelBack->runAction(CCMoveTo::create(0.2f,ccp(0,405)));
		}
		m_selectedCell = cell;
	}
	if(idx>(tableView->getDataSource()->numberOfCellsInTableView(tableView)-10)&&m_hasMore&&!m_isRequesting){
		getBaiduVideoList(m_requset_baidu_index);
	}
}

CCSize BaiduYunSence::tableCellSizeForIndex(CCListView* table,
		unsigned int idx) {
	return CCSizeMake(320,table->getViewSize().height);
}

CCTableViewCell* BaiduYunSence::tableCellAtIndex(CCListView* table,
		unsigned int idx) {
	CCTableViewCell *pCell = table->dequeueCell();
	CCImageView *pImage;
	CCSprite *pSprite;
	CCTableCellForHistory *pLabelBack;
	CCLabelTTF *pLabel;
	if (!pCell) {
		pCell = new CCTableViewCell();
		pCell->autorelease();
		pImage = CCImageView::createWithNetUrl(m_dates.at(0).getPicUrl().c_str(),"defulte_avatar.png",ccp(264,140));
		pImage->setPosition(ccp(170,506));
		pImage->setTag(1);
		pCell->addChild(pImage);
		pSprite = CCSprite::create("baidu_thumb.png");
		pSprite->setAnchorPoint(CCPointZero);
		pSprite->setPosition(ccp(0,405));
		pSprite->setTag(2);
		pCell->addChild(pSprite);
		pLabelBack = CCTableCellForHistory::create("baidu_card_activated.png");
		pLabelBack->setAnchorPoint(ccp(0,1));
		pLabelBack->setPosition(ccp(0,540));
		pLabelBack->setTag(3);
		pCell->addChild(pLabelBack);
		pLabel = CCLabelTTF::create(m_dates.at(0).getFileName().c_str(), "Arial", 27.0, CCSizeMake(270, 150), CCTextAlignment(kCCTextAlignmentLeft));
		pLabel->setPosition(ccp(35,300));
		pLabel->setAnchorPoint(ccp(0,1));
		pLabel->setTag(4);
		pCell->addChild(pLabel);
	}
	else
	{
		pImage = (CCImageView*)pCell->getChildByTag(1);
		pSprite = (CCSprite*)pCell->getChildByTag(2);
		pLabelBack = (CCTableCellForHistory*)pCell->getChildByTag(3);
		pLabel = (CCLabelTTF*)pCell->getChildByTag(4);

	}
	if(idx==0){
		pLabelBack->setPosition(ccp(0,540));
		pImage->setVisible(false);
		if(username.empty()){
			pLabel->setString("unkown");
		}else{
			pLabel->setString(username.c_str());
		}
		pSprite->initWithFile("baidu_id.png");
		pSprite->setAnchorPoint(CCPointZero);
		pSprite->setPosition(ccp(0,405));
	}else{
		BaiduVideoInfo info  = m_dates.at(idx-1);
		pLabel->setString(info.getFileName().c_str());
		if(idx == table->getSelected()){
			pLabelBack->setPosition(ccp(0,405));
			m_selectedCell = pCell;
		}else{
			pLabelBack->setPosition(ccp(0,540));
		}
		pSprite->initWithFile("baidu_thumb.png");
		pSprite->setAnchorPoint(CCPointZero);
		pSprite->setPosition(ccp(0,405));
		pImage->setVisible(true);
		pImage->initWithUrl(info.getPicUrl().c_str(),"defulte_avatar.png");
	}
	return pCell;
}

unsigned int BaiduYunSence::numberOfCellsInTableView(CCListView* table) {
	return m_dates.size() + 1;
}

void BaiduYunSence::getBaiduVideoList(int index) {
	if(!m_hasMore){
		return;
	}
	m_isRequesting = true;
	string access_token = getBaiduTokenJNI();
	CCHttpClient* httpClient = CCHttpClient::getInstance();
	CCHttpRequest* httpReq =new CCHttpRequest();
	httpReq->setRequestType(CCHttpRequest::kHttpGet);

	const char* url_p = "https://pcs.baidu.com/rest/2.0/pcs/stream?method=list&type=video&start=%d&limit=%d&access_token=%s";
//	char url[256] = {};
//	sprintf(url,url_p,20*index,20*(index+1),access_token.c_str());

	CCString * url = CCString::createWithFormat(url_p, 20*index,20,access_token.c_str());
	LOGD("XunLeiYunSence","url -- > %s",url->getCString());
	httpReq->setUrl(url->getCString());
	httpReq->setTag("getBaiduVideoList");
	httpReq->setResponseCallback(this,callfuncND_selector(BaiduYunSence::onGetBaiduVideoListComplete));
	httpClient->setTimeoutForConnect(30);
	httpClient->send(httpReq);
	httpReq->release();
	httpReq=NULL;
}

void BaiduYunSence::onGetBaiduVideoListComplete(CCNode* node, CCObject* obj) {
	m_isRequesting = false;
	CCHttpResponse *response = (CCHttpResponse*)obj;
	 if (!response)
	 {
		return;
	 }
	 if(0!=strcmp("getBaiduVideoList",response->getHttpRequest()->getTag())){
		 return;
	 }
	 int statusCode = response->getResponseCode();
	 if (response->isSucceed()){
		 std::vector<char> *buffer = response->getResponseData();
		 string buff(buffer->begin(),buffer->end());
		 LOGD("BaiduYunSence","response---->%s",buff.c_str());
		 CSJson::Value jsonobj;
		 CSJson::Reader reader;
		 const char * pic_p = "https://pcs.baidu.com/rest/2.0/pcs/thumbnail?method=generate&path=%s&width=264&height=140&quality=100&access_token=%s";
		 if(reader.parse(buff,jsonobj))
		 {
			 const CSJson::Value arrayObj = jsonobj["list"];
			 LOGD("BaiduYunSence","history_play_list size =  %d",arrayObj.size());
			 if(arrayObj.size()<20){
				 m_hasMore = false;
			 }else{
				 m_hasMore = true;
			 }
			 m_requset_baidu_index += 1;
			 for (int i=0; i<arrayObj.size(); i++) {
			   BaiduVideoInfo videoInfo;
			   videoInfo.setFsId(arrayObj[i]["fs_id"].asDouble());
			   videoInfo.setPath(arrayObj[i]["path"].asString());
			   LOGD("BaiduYunSence","%d --> Duration = %d",i,videoInfo.getPath().c_str());
			   vector<string> splits = stringSplit(videoInfo.getPath(),"/");
			   videoInfo.setFileName(splits.at(splits.size()-1));
			   LOGD("BaiduYunSence","%d --> FileName = %s",i,videoInfo.getFileName().c_str());
			   videoInfo.setSize(arrayObj[i]["size"].asDouble());
			   LOGD("BaiduYunSence","%d --> Filesize = %d",i,videoInfo.getSize());
//			   videoInfo.setGcid(arrayObj[i]["gcid"].asString());
//			   LOGD("BaiduYunSence","%d --> Gcid = %s",i,videoInfo.getGcid().c_str());
//			   videoInfo.setSrcUrl(arrayObj[i]["src_url"].asString());
//			   if(arrayObj[i]["isdir"]==0){//包含
//				   videoInfo.setIsDir(false);
//			   }else{
//				   videoInfo.setIsDir(true);
//			   }
			   LOGD("BaiduYunSence","%d --> setIsDir",i);
//			   char pic_url[512] = {};
//			   sprintf(pic_url,pic_p,videoInfo.getPath().c_str(),getBaiduTokenJNI().c_str());

			   CCString * pic_url = CCString::createWithFormat(pic_p,videoInfo.getPath().c_str(),getBaiduTokenJNI().c_str());
			   LOGD("BaiduYunSence","%d -->  pic_url = %s",i,pic_url->getCString());
			   videoInfo.setPicUrl(string(pic_url->getCString()));
			   m_dates.push_back(videoInfo);
			 }
			 if(m_requset_baidu_index==1){
				 initTableView();
			 }else{
				 tableView->reloadData();
			 }

		 }else
		 {
			 LOGD("BaiduYunSence","getVideoList json parse Filed %s", response->getErrorBuffer());
		 }


	 }else{
		 LOGD("BaiduYunSence","getVideoList Filed %s", response->getErrorBuffer());
	 }
}

void BaiduYunSence::getBaiduLoginUserInfo() {
	CCString * url = CCString::createWithFormat("https://openapi.baidu.com/rest/2.0/passport/users/getLoggedInUser?access_token=%s",getBaiduTokenJNI().c_str());
	CCHttpClient* httpClient = CCHttpClient::getInstance();
	CCHttpRequest* httpReq =new CCHttpRequest();
	httpReq->setRequestType(CCHttpRequest::kHttpGet);
	LOGD("XunLeiYunSence","getBaiduLoginUserInfo url -- > %s",url->getCString());
	httpReq->setUrl(url->getCString());
	httpReq->setTag("getBaiduLoginUserInfo");
	httpReq->setResponseCallback(this,callfuncND_selector(BaiduYunSence::onBaiduLoginUserComplete));
	httpClient->setTimeoutForConnect(30);
	httpClient->send(httpReq);
	httpReq->release();
	httpReq=NULL;

}

void BaiduYunSence::onBaiduLoginUserComplete(CCNode* node, CCObject* obj) {
	CCHttpResponse *response = (CCHttpResponse*)obj;
	 if (!response)
	 {
		return;
	 }
	 if(0!=strcmp("getBaiduLoginUserInfo",response->getHttpRequest()->getTag())){
		 return;
	 }
	 if (response->isSucceed()){
		 std::vector<char> *buffer = response->getResponseData();
		 string buff(buffer->begin(),buffer->end());
		 LOGD("BaiduYunSence","response---->%s",buff.c_str());
		 CSJson::Value jsonobj;
		 CSJson::Reader reader;
		 if(reader.parse(buff,jsonobj)){
			 username = jsonobj["uname"].asString();
			 getBaiduVideoList(m_requset_baidu_index);
		 }else{
			 LOGD("BaiduYunSence","getBaiduLoginUserInfo json parse Filed");
		 }
	 }else{
		 LOGD("BaiduYunSence","getBaiduLoginUserInfo Filed %s", response->getErrorBuffer());
		 if(response->getResponseCode()==401){
			 string errormsg = string(response->getErrorBuffer());
			 CSJson::Value jsonobj;
			 CSJson::Reader reader;
			 if(reader.parse(errormsg,jsonobj)){
				 int error_code = jsonobj["error_code"].asInt();
				 /*
				  *	HTTP状态码  	错误码		错误信息					备注
				  *		401		110		Access token invalid 	Access token
				  *						or no longer valid		无效或已失效
				  *		401		111		Access token expired	Access token已过期
				  *		401 	112		Session key expired		会话密钥已过期
				  */
				 if(error_code == 110||error_code == 111 || error_code == 112){
					 showBaiduLoginDialog(baiduDilogCallbackFunc,this);
				 }
			 }
		 }
	 }
}

void BaiduYunSence::initTableView() {
	CCSize winSize = CCDirector::sharedDirector()->getWinSize();

	tableView = CCListView::create(this,CCSizeMake(winSize.width, 608),NULL,160.0f,0.0f,160.0f,0.0f);
	tableView->setAnchorPoint(ccp(0,1));
	tableView->setPosition(0,188);
	tableView->setDelegate(this);
	tableView->setDirection(kCCScrollViewDirectionHorizontal);
	tableView->setVerticalFillOrder(kCCListViewFillTopDown);
	tableView->setSelection(0);
	this->addChild(tableView);
}



