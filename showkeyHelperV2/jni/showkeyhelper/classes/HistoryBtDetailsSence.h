#ifndef _JOYPLUS_HISTORYBTDETAILSSENCE_H_
#define _JOYPLUS_HISTORYBTDETAILSSENCE_H_

#include "cocos2d.h"
#include "cocos-ext.h"
#include "ui/CCImageView.h"
#include "ui/CCTableCellForHistory.h"
#include "ui/CCListView.h"
#include "Log.h"
#include "platform/android/jni/Java_org_cocos2dx_lib_Cocos2dxHelper.h"
#include "HistoryScnce.h"

USING_NS_CC_EXT;
USING_NS_CC;

class HistoryBtDetailsSence : public CCLayer , public CCListViewDataResouce , public CCListViewDelegate{
public:
	virtual ~HistoryBtDetailsSence();
	virtual bool init();
	virtual void keyBackClicked();//Android 返回键
	virtual void keyArrowClicked(int arrow);//方向键
	virtual void keyEnterClicked();//回车键

	virtual void tableCellClicked(CCListView *table, CCTableViewCell* cell, unsigned int idx);

	virtual void tableCellSelected(CCListView *table, CCTableViewCell* cell, unsigned int idx);

	virtual CCSize tableCellSizeForIndex(CCListView *table, unsigned int idx);

	virtual CCTableViewCell* tableCellAtIndex(CCListView *table, unsigned int idx);

	virtual unsigned int numberOfCellsInTableView(CCListView *table);

	static CCScene* scene(PlayHistoryInfo info);

	void callBackAnim(CCNode* sender, CCLabelTTF *pLabel);

	static HistoryBtDetailsSence* create(PlayHistoryInfo playHistoryInfo)
	{
	    HistoryBtDetailsSence *pRet = new HistoryBtDetailsSence();
	    if (pRet){
	    	pRet->setPlayHistoryInfo(playHistoryInfo);
	    	if(pRet->init()){
	    		 pRet->autorelease();
	    		 return pRet;
	    	}else{
	    		delete pRet;
				pRet = 0;
				return 0;
	    	}
	    }
	    else
	    {
	        delete pRet;
	        pRet = 0;
	        return 0;
	    }
	}

	void setPlayHistoryInfo(PlayHistoryInfo playHistoryInfo){
		m_playHistoryInfo = playHistoryInfo;
	}

private:
	PlayHistoryInfo m_playHistoryInfo;
	CCListView * tableView;
	CCTableViewCell * m_selectedCell;
};

#endif /* _JOYPLUS_HISTORYBTDETAILSSENCE_H_ */
