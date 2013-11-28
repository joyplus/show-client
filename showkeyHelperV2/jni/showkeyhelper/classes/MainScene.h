#ifndef __MAINSCENE_SCENE_H__
#define __MAINSCENE_SCENE_H__
#include "cocos2d.h"
#include "cocos-ext.h"
#include "HttpRequest.h"
#include "HttpClient.h"
#include "PageLayer.h"
#include "HistoryScnce.h"
#include "XunLeiYunSence.h"
#include "BaiduYunSence.h"
#include "SettingSence.h"
#include "TransitionScenes.h"
#include <vector>
#include <string>

#include "platform/android/jni/JniHelper.h"
#include "Log.h"
#include "keypad_dispatcher/CCKeypadDispatcher.h"
#include "ui/CCImageView.h"
#include "platform/android/jni/Java_org_cocos2dx_lib_Cocos2dxHelper.h"

USING_NS_CC_EXT;
USING_NS_CC;
using namespace std;
class MainScene : public cocos2d::CCLayer, public PageLayerItemClickDelegate
{

public:
    // Here's a difference. Method 'init' in cocos2d-x returns bool, instead of returning 'id' in cocos2d-iphone
    virtual bool init();  
    void onEnterTransitionDidFinish();
	void onExitTransitionDidStart();

    virtual void keyBackClicked();//Android 返回键
    //virtual void keyMenuClicked();//Android 菜单键

    virtual void keyArrowClicked(int arrow);//方向键
    virtual void keyEnterClicked();//回车键

    virtual void onPageItemClick(int itemTag);

    // there's no 'id' in cpp, so we recommand to return the exactly class pointer
    static cocos2d::CCScene* scene();
    
    // a selector callback
    void menuCloseCallback(CCObject* pSender);

    // implement the "static node()" method manually
    CREATE_FUNC(MainScene);

    virtual ~MainScene();

    void disPlayPincode();

private :
    PageLayer * m_pageLayer;
};

#endif  // __MAINSCENE_SCENE_H__
