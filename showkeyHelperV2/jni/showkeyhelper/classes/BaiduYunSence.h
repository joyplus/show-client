#ifndef _JOYPLUS_BAIDUYUNSENCE_H_
#define _JOYPLUS_BAIDUYUNSENCE_H_

#include "cocos2d.h"
#include "cocos-ext.h"
#include "Log.h"
#include "platform/android/jni/Java_org_cocos2dx_lib_Cocos2dxHelper.h"
#include "ui/CCTableCellForHistory.h"
#include "ui/CCListView.h"
#include "ui/CCImageView.h"
#include "ui/CCButtonView.h"
USING_NS_CC_EXT;
USING_NS_CC;
using namespace std;

class BaiduVideoInfo{
private:
	long fs_id;
	string path;
	long size;
	string pic_url;
	string fileName;

public:
	long getFsId() const
	{
		return fs_id;
	}

	void setFsId(long fsId) {
		fs_id = fsId;
	}

	string getPath() const {
		return path;
	}

	void setPath(string path) {
		this->path = path;
	}

	string getPicUrl() const {
		return pic_url;
	}

	void setPicUrl(string picUrl) {
		pic_url = picUrl;
	}

	long getSize() const {
		return size;
	}

	void setSize(long size) {
		this->size = size;
	}

	string getFileName() const {
		return fileName;
	}

	void setFileName(string fileName) {
		this->fileName = fileName;
	}
};

class BaiduYunSence :  public cocos2d::CCLayer ,public CCListViewDataResouce , public CCListViewDelegate{
public:
	virtual ~BaiduYunSence();

	virtual bool init();
	void onEnterTransitionDidFinish();
	void onExitTransitionDidStart();

	virtual void keyBackClicked();//Android 返回键
	//virtual void keyMenuClicked();//Android 菜单键

	virtual void keyArrowClicked(int arrow);//方向键
	virtual void keyEnterClicked();//回车键

	void popSence();

	void loginBaiduSuccess();
	static cocos2d::CCScene* scene();
	CREATE_FUNC(BaiduYunSence);


	virtual void tableCellClicked(CCListView *table, CCTableViewCell* cell, unsigned int idx);

	virtual void tableCellSelected(CCListView *table, CCTableViewCell* cell, unsigned int idx);

	virtual CCSize tableCellSizeForIndex(CCListView *table, unsigned int idx);

	virtual CCTableViewCell* tableCellAtIndex(CCListView *table, unsigned int idx);

	virtual unsigned int numberOfCellsInTableView(CCListView *table);

	void getBaiduVideoList(int index);
	void getBaiduLoginUserInfo();
	void onBaiduLoginUserComplete(CCNode* node,CCObject* obj);
	void onGetBaiduVideoListComplete(CCNode* node,CCObject* obj);
	void callBackAnim(CCNode* sender, CCLabelTTF *pLabel);

private:
	std::vector<BaiduVideoInfo> m_dates;

	CCListView * tableView;
	CCTableViewCell * m_selectedCell;
	unsigned int m_selected_id;
	CCButtonView* m_selectedButton;
	CCSprite* m_empty_back;

	string username;

	unsigned int m_requset_baidu_index;
	bool m_hasMore;
	bool m_isRequesting;
	void initTableView();
};

#endif /* _JOYPLUS_BAIDUYUNSENCE_H_ */
