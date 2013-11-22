#ifndef _JOYPLUS_XUNLEIYUNSENCE_H_
#define _JOYPLUS_XUNLEIYUNSENCE_H_

#include "cocos2d.h"
#include "cocos-ext.h"
#include "Log.h"
#include "platform/android/jni/Java_org_cocos2dx_lib_Cocos2dxHelper.h"
#include "ui/CCTableCellForHistory.h"
#include "ui/CCListView.h"
#include "ui/CCImageView.h"
USING_NS_CC_EXT;
USING_NS_CC;
using namespace std;

class XunleiUserInfo {
private:
	string name;
	int vipLevel;

public:
	string getName() const {
		return name;
	}

	void setName(string name) {
		this->name = name;
	}

	int getVipLevel() const {
		return vipLevel;
	}

	void setVipLevel(int vipLevel) {
		this->vipLevel = vipLevel;
	}
};

class XunLeiVideInfo : public CCObject{

private:

	string createTime;
	int duration;
	string file_name;
	double filesize;
	string gcid;
	string cid;
	bool isDir;
	string src_url;
	string userid;
	string pic_url;

public:

	XunLeiVideInfo(){
		createTime = "";
		file_name = "";
		duration = 0;
		filesize = 0;
		gcid = "";
		isDir = false;
		src_url = "";
		userid = "";
		pic_url = "";
	}

	string getCreateTime() const {
		return createTime;
	}

	void setCreateTime(string createTime) {
		this->createTime = createTime;
	}

	string getFileName() const {
		return file_name;
	}

	void setFileName(string fileName) {
		file_name = fileName;
	}

	string getGcid() const {
		return gcid;
	}

	void setGcid(string gcid) {
		this->gcid = gcid;
	}

	bool isIsDir() const {
		return isDir;
	}

	void setIsDir(bool isDir = false) {
		this->isDir = isDir;
	}

	string getPicUrl() const {
		return pic_url;
	}

	void setPicUrl(string picUrl) {
		pic_url = picUrl;
	}

	string getSrcUrl() const {
		return src_url;
	}

	void setSrcUrl(string srcUrl) {
		src_url = srcUrl;
	}

	string getUserid() const {
		return userid;
	}

	void setUserid(string userid) {
		this->userid = userid;
	}

	int getDuration() const {
		return duration;
	}

	void setDuration(int duration) {
		this->duration = duration;
	}

	double getFilesize() const {
		return filesize;
	}

	void setFilesize(double filesize) {
		this->filesize = filesize;
	}

};

class XunLeiYunSence : public cocos2d::CCLayer ,public CCListViewDataResouce , public CCListViewDelegate{
public:
	virtual ~XunLeiYunSence();

	virtual bool init();

	virtual void keyBackClicked();//Android 返回键
	//virtual void keyMenuClicked();//Android 菜单键

	virtual void keyArrowClicked(int arrow);//方向键
	virtual void keyEnterClicked();//回车键

	void getXunleiVideoList(int index);

	void onGetXunleiVideoListComplete(CCNode* node,CCObject* obj);

	void popSence();
	void loginXunleiSuccess();
	static cocos2d::CCScene* scene();
	CREATE_FUNC(XunLeiYunSence);

	virtual void tableCellClicked(CCListView *table, CCTableViewCell* cell, unsigned int idx);

	virtual void tableCellSelected(CCListView *table, CCTableViewCell* cell, unsigned int idx);

	virtual CCSize tableCellSizeForIndex(CCListView *table, unsigned int idx);

	virtual CCTableViewCell* tableCellAtIndex(CCListView *table, unsigned int idx);

	virtual unsigned int numberOfCellsInTableView(CCListView *table);

private :

	std::vector<XunLeiVideInfo> m_dates;

	CCListView * tableView;
	CCTableViewCell * m_selectedCell;
	XunleiUserInfo m_userInfo;
	void initTableView();
};

#endif /* _JOYPLUS_XUNLEIYUNSENCE_H_ */
