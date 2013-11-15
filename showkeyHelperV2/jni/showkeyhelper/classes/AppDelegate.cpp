#include <vector>
#include <string>
#include "cocos2d.h"
#include "CCEGLView.h"
#include "AppDelegate.h"
#include "MainScene.h"
#include "AppMacros.h"
#include "Log.h"
using namespace std;

USING_NS_CC;

AppDelegate::AppDelegate()
{
}

AppDelegate::~AppDelegate()
{
//    SimpleAudioEngine::end();
}

bool AppDelegate::applicationDidFinishLaunching()
{
	CCDirector* pDirector = CCDirector::sharedDirector();
	    CCEGLView* pEGLView = CCEGLView::sharedOpenGLView();

	    pDirector->setOpenGLView(pEGLView);

	    // Set the design resolution
	    pEGLView->setDesignResolutionSize(designResolutionSize.width, designResolutionSize.height, kResolutionNoBorder);
//	    pEGLView->setDesignResolutionSize(480, 320, kResolutionNoBorder);

		CCSize frameSize = pEGLView->getFrameSize();

	    vector<string> searchPath;

	    LOGD("AppDelegate","fram size -> width = %f", frameSize.width);
	    LOGD("AppDelegate","fram size -> height = %f", frameSize.height);
	    // In this demo, we select resource according to the frame's height.
	    // If the resource size is different from design resolution size, you need to set contentScaleFactor.
	    // We use the ratio of resource's height to the height of design resolution,
	    // this can make sure that the resource's height could fit for the height of design resolution.

	    // if the frame's height is larger than the height of medium resource size, select large resource.
//		if (frameSize.width > boxResouce.size.width)
//		{
//	        searchPath.push_back(largeResource.directory);
//
//	        pDirector->setContentScaleFactor(MIN(largeResource.size.height/designResolutionSize.height, largeResource.size.width/designResolutionSize.width));
//		}
//	    // if the frame's height is larger than the height of small resource size, select medium resource.
//	    else if (frameSize.width > mediumResource.size.width)
//	    {
//	        searchPath.push_back(boxResouce.directory);
//
//	        pDirector->setContentScaleFactor(MIN(boxResouce.size.height/designResolutionSize.height, boxResouce.size.width/designResolutionSize.width));
//	    }
//	    // if the frame's height is smaller than the height of medium resource size, select small resource.
//		else if(frameSize.width > smallResource.size.width)
//	    {
//	        searchPath.push_back(mediumResource.directory);
//
//	        pDirector->setContentScaleFactor(MIN(mediumResource.size.height/designResolutionSize.height, mediumResource.size.width/designResolutionSize.width));
//	    }
//		else
//		{
//			searchPath.push_back(smallResource.directory);
//
//				        pDirector->setContentScaleFactor(MIN(smallResource.size.height/designResolutionSize.height, smallResource.size.width/designResolutionSize.width));
//		}

		LOGD("AppDelegate", "ScaleFactor ->  %f", pDirector->getContentScaleFactor());

	    // set searching path
//	    CCFileUtils::sharedFileUtils()->setSearchPaths(searchPath);

	    // turn on display FPS
	    pDirector->setDisplayStats(true);

	    // set FPS. the default value is 1.0/60 if you don't call this
	    pDirector->setAnimationInterval(1.0 / 60);

    //glClearColor(1.0, 1.0, 1.0, 255.0);
    CCScene *pScene = MainScene::scene();
//    CCScene *pScene = LoadingLayer::scene();
    CCDirector::sharedDirector()->setDepthTest(false);
    CCDirector::sharedDirector()->setProjection(kCCDirectorProjection2D);
    // run
    LOGD("AppDelegte","hello world will run");
	pDirector->runWithScene(pScene);
	return true;
}

// This function will be called when the app is inactive. When comes a phone call,it's be invoked too
void AppDelegate::applicationDidEnterBackground()
{
    CCDirector::sharedDirector()->stopAnimation();

//    SimpleAudioEngine::sharedEngine()->pauseBackgroundMusic();
}

// this function will be called when the app is active again
void AppDelegate::applicationWillEnterForeground()
{
    CCDirector::sharedDirector()->startAnimation();

//    SimpleAudioEngine::sharedEngine()->resumeBackgroundMusic();
}
