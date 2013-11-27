#ifndef _JOYPLUS_HISTORYSENCE_H_
#define _JOYPLUS_HISTORYSENCE_H_

#include "cocos2d.h"
#include "cocos-ext.h"
#include "ui/CCImageView.h"
#include "ui/CCTableCellForHistory.h"
#include "ui/CCListView.h"
#include "Log.h"
#include "platform/android/jni/Java_org_cocos2dx_lib_Cocos2dxHelper.h"

USING_NS_CC_EXT;
USING_NS_CC;

class BTEpisode{
private:
	string name;
	string pic_url;
	int playback_time;
	int duration;

public:
	int getDuration() const
	{
		return duration;
	}

	void setDuration(int duration) {
		this->duration = duration;
	}

	string getName() const {
		return name;
	}

	void setName(string name) {
		this->name = name;
	}

	int getPlaybackTime() const {
		return playback_time;
	}

	void setPlaybackTime(int playbackTime) {
		playback_time = playbackTime;
	}

	string getPicUrl() const {
		return pic_url;
	}

	void setPicUrl(string picUrl) {
		pic_url = picUrl;
	}
};

class PlayHistoryInfo
{
private :
	string name;
	string push_url;
	string pic_url;
	int playback_time;
	int duration;
	int id;
	bool isDir;
	int type;
	std::vector<BTEpisode> btepisodes;
public:

	int getDuration() const
	{
		return duration;
	}

	void setDuration(int duration) {
		this->duration = duration;
	}

	int getId() const {
		return id;
	}

	void setId(int id) {
		this->id = id;
	}

	string getName() const {
		return name;
	}

	void setName(string name) {
		this->name = name;
	}

	string getPicUrl() const {
		return pic_url;
	}

	void setPicUrl(string picUrl) {
		pic_url = picUrl;
	}

	int getPlaybackTime() const {
		return playback_time;
	}

	void setPlaybackTime(int playbackTime) {
		playback_time = playbackTime;
	}

	string getPushUrl() const {
		return push_url;
	}

	void setPushUrl(string pushUrl) {
		push_url = pushUrl;
	}

	std::vector<BTEpisode> getBtepisodes() const {
		return btepisodes;
	}

	void setBtepisodes(std::vector<BTEpisode> btepisodes) {
		this->btepisodes = btepisodes;
	}

	bool isIsDir() const {
		return isDir;
	}

	void setIsDir(bool isDir) {
		this->isDir = isDir;
	}

	int getType() const {
		return type;
	}

	void setType(int type) {
		this->type = type;
	}
};

class HistoryScnce : public CCLayer , public CCListViewDataResouce , public CCListViewDelegate
{
public:
	virtual bool init();
	virtual void keyBackClicked();//Android 返回键
	virtual void keyArrowClicked(int arrow);//方向键
	virtual void keyEnterClicked();//回车键

	static CCScene* scene();

	CREATE_FUNC(HistoryScnce);

	void onEnter();
	void onEnterTransitionDidFinish();
	void onExit();
	void onExitTransitionDidStart();

	virtual ~HistoryScnce();


	virtual void tableCellClicked(CCListView *table, CCTableViewCell* cell, unsigned int idx);

	virtual void tableCellSelected(CCListView *table, CCTableViewCell* cell, unsigned int idx);

	virtual CCSize tableCellSizeForIndex(CCListView *table, unsigned int idx);

	virtual CCTableViewCell* tableCellAtIndex(CCListView *table, unsigned int idx);

	virtual unsigned int numberOfCellsInTableView(CCListView *table);

	void callBackAnim(CCNode* sender, CCLabelTTF *pLabel);

private :
	CCListView * tableView;
	CCTableViewCell * m_selectedCell;

	std::vector<PlayHistoryInfo> m_dates;
};

#endif //_JOYPLUS_HISTORYSENCE_H_
