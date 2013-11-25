#include "MainScene.h"

static void pincodeCallbackFunc(bool isSuccess,void* ctx)
{
	MainScene* thiz = (MainScene*)ctx;
	if(isSuccess){
		thiz->disPlayPincode();
	}else{

	}
}

CCScene* MainScene::scene()
{
    CCScene * scene = NULL;
    do 
    {
        // 'scene' is an autorelease object
        scene = CCScene::create();
        CC_BREAK_IF(! scene);

        // 'layer' is an autorelease object
        MainScene *layer = MainScene::create();
        CC_BREAK_IF(! layer);
        // add layer as a child to scene
        scene->addChild(layer);
    } while (0);

    // return the scene
    return scene;
}

// on "init" you need to initialize your instance
bool MainScene::init()
{
    bool bRet = false;
    do 
    {
        if(! CCLayer::init())
		{
        	LOGD("MainScene","CCLayer init fail");
        	break;
		}
        CCSize size = CCDirector::sharedDirector()->getWinSize();
//        CCSprite* background = CCSprite::create("back.png");
//        CC_BREAK_IF(! background);
//        background->setPosition(ccp(size.width/2, size.height/2));
//        this->addChild(background, 0);

        m_pageLayer = PageLayer::create();
        m_pageLayer->setPosition(CCSizeZero);
        m_pageLayer->setItemClickDelegate(this);
        this->addChild(m_pageLayer);

        string pincode = getPincodeJNI();
        if(!pincode.empty()){
        	disPlayPincode();
        }else{
        	generatePincode(pincodeCallbackFunc,this);
        }

//        layout_historyLayer = HistoryLayer::create();
//        layout_historyLayer->setPosition(CCSizeZero);
//        layout_historyLayer->setVisible(false);
//        this->addChild(layout_historyLayer);

       // this->scheduleUpdate();
        this->setKeypadEnabled(true);
        bRet = true;
    } while (0);
    return bRet;
}

void MainScene::menuCloseCallback(CCObject* pSender)
{
    // "close" menu item clicked
    CCDirector::sharedDirector()->end();
}

void MainScene::keyBackClicked()
{
	 CCDirector::sharedDirector()->end();
}

void MainScene::keyEnterClicked()
{
	if(m_pageLayer->isVisible())
	{
		if(m_pageLayer->onKeyEnterClicked()) return;
	}
//	if(layout_historyLayer->isVisible())
//	{
//		if(layout_historyLayer->onKeyEnterClicked()) return;
//	}
//	LOGD("MainScene" ,"KEY--ENTER");
//	if(layout_pageLayer->onKeyEnterClicked()) return;
}

void MainScene::keyArrowClicked(int arrow)
{
	if(m_pageLayer->isVisible())
	{
		if(m_pageLayer->onKeyArrowClicked(arrow)) return;
	}
//	if(layout_historyLayer->isVisible())
//	{
//		if(layout_historyLayer->onKeyArrowClicked(arrow)) return;
//	}
//	switch (arrow) {
//		case ccKeypadMSGType(kTypeLeftArrowClicked):
//			{
//				LOGD("hello world" ,"KEY--left");
//				CCActionInterval* actionMovieLeft = CCMoveBy::create(1.0f,
//						CCPointMake(-layout->getContentSize().width, 0));
//				layout->runAction(actionMovieLeft);
//				break;
//			}
//
//		case ccKeypadMSGType(kTypeUpArrowClicked):
//			LOGD("hello world" ,"KEY--up");
//			break;
//		case ccKeypadMSGType(kTypeRightArrowClicked):
//			{
//				LOGD("hello world" ,"KEY--right");
//				CCActionInterval* actionMovieRight = CCMoveBy::create(1.0f,
//										CCPointMake(layout->getContentSize().width, 0));
//				layout->runAction(actionMovieRight);
//				break;
//			}
//
//		case ccKeypadMSGType(kTypeDownArrowClicked):
//			LOGD("hello world" ,"KEY--down");
//			break;
//		default:
//			break;
//	}
}

//void MainScene::onGetFinished(CCNode* node,CCObject* obj)
//{
//	CCHttpResponse* response = (CCHttpResponse*)obj;
//	//    判断是否响应成功
//	    if (!response->isSucceed())
//	    {
//	        LOGD("onGetFinished","Receive Error! %s\n",response->getErrorBuffer());
//	        return ;
//	    }
//
//	    const char* tag = response->getHttpRequest()->getTag();
//	    if (0 == strcmp("getPin",tag))
//	    {
//	        vector<char> *data = response->getResponseData();
////	        LOGD("MainScene","response ->%s",res.c_str());
////	        CSJson::Value jsonobj;
////	        CSJson::Reader reader;
////	        reader.parse(res,jsonobj);
//	    }
//}

MainScene::~MainScene()
{
	//CC_SAFE_DELETE(layout_historyLayer);
	//CC_SAFE_DELETE(layout_pageLayer);
}

void MainScene::onPageItemClick(int page_Tag)
{
	switch(page_Tag)
	{
	case SPRITE_TAG(TAG_PINCODE):
		LOGD("MainScene","pincode click");
		break;
	case SPRITE_TAG(TAG_JINGXI):
		LOGD("MainScene","jingxi click");
		startSetting();
		break;
	case SPRITE_TAG(TAG_HISTORY):
		LOGD("MainScene","history click");
		CCDirector::sharedDirector()->pushScene(CCTransitionSlideInR::create(0.2f,HistoryScnce::scene()));
		break;
	case SPRITE_TAG(TAG_XUNLEI):
		LOGD("MainScene","xunlei click");
		CCDirector::sharedDirector()->pushScene(CCTransitionSlideInR::create(0.2f,XunLeiYunSence::scene()));
		break;
	case SPRITE_TAG(TAG_BAIDU):
		LOGD("MainScene","baidu click");
		CCDirector::sharedDirector()->pushScene(CCTransitionSlideInR::create(0.2f,BaiduYunSence::scene()));
		break;

	}
}

//void MainScene::getPincode() {
//	CCString * url = CCString::createWithFormat("https://openapi.baidu.com/rest/2.0/passport/users/getLoggedInUser?access_token=%s",getBaiduTokenJNI().c_str());
//	CCHttpClient* httpClient = CCHttpClient::getInstance();
//	CCHttpRequest* httpReq =new CCHttpRequest();
//	httpReq->setRequestType(CCHttpRequest::kHttpGet);
//	LOGD("XunLeiYunSence","getBaiduLoginUserInfo url -- > %s",url->getCString());
//	httpReq->setUrl(url->getCString());
//	httpReq->setTag("getPin");
//	httpReq->setResponseCallback(this,callfuncND_selector(MainScene::onGetFinished));
//	httpClient->setTimeoutForConnect(30);
//	httpClient->send(httpReq);
//	httpReq->release();
//	httpReq=NULL;
//}


void MainScene::disPlayPincode() {
	m_pageLayer->setPincode(getPincodeJNI().c_str());
	startFayeService();
	unsigned int size = 0;
	void* date = getErweimaDateJNI("http://www.baidu.com/", 250, &size);
	if(date){
		CCImage * image = new CCImage();
		image->initWithImageData(date,size);
		free(date);
		CCTexture2D* texture = new cocos2d::CCTexture2D();
		bool isImg = texture->initWithImage(image);
		image->release();
		if(isImg){
			CCSprite *s = CCSprite::createWithTexture(texture);
			s->setPosition(ccp(500,300));
			addChild(s);
		}
		texture->release();
	}
}




