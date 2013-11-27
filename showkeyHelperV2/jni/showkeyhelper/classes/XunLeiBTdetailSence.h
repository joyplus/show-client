#ifndef _JOYPLUS_XUNLEIBTDETAILSENCE_H_
#define _JOYPLUS_XUNLEIBTDETAILSENCE_H_

#include "cocos2d.h"
#include "cocos-ext.h"
#include "ui/CCImageView.h"
#include "ui/CCTableCellForHistory.h"
#include "ui/CCListView.h"
#include "Log.h"
#include "XunLeiYunSence.h"
#include "platform/android/jni/Java_org_cocos2dx_lib_Cocos2dxHelper.h"
#include "HistoryScnce.h"

USING_NS_CC_EXT;
USING_NS_CC;

class XunLeiBTdetailSence : public CCLayer , public CCListViewDataResouce , public CCListViewDelegate{
public:
	virtual ~XunLeiBTdetailSence();
	virtual bool init();
	void onEnterTransitionDidFinish();
	void onExitTransitionDidStart();
	virtual void keyBackClicked();//Android 返回键
	virtual void keyArrowClicked(int arrow);//方向键
	virtual void keyEnterClicked();//回车键

	virtual void tableCellClicked(CCListView *table, CCTableViewCell* cell, unsigned int idx);

	virtual void tableCellSelected(CCListView *table, CCTableViewCell* cell, unsigned int idx);

	virtual CCSize tableCellSizeForIndex(CCListView *table, unsigned int idx);

	virtual CCTableViewCell* tableCellAtIndex(CCListView *table, unsigned int idx);

	virtual unsigned int numberOfCellsInTableView(CCListView *table);

	static CCScene* scene(XunLeiVideInfo info);

	void callBackAnim(CCNode* sender, CCLabelTTF *pLabel);

	static XunLeiBTdetailSence* create(XunLeiVideInfo xunLeiVideInfo)
		{
		XunLeiBTdetailSence *pRet = new XunLeiBTdetailSence();
		    if (pRet){
		    	pRet->setXunLeiVideInfo(xunLeiVideInfo);
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

	void setXunLeiVideInfo(XunLeiVideInfo xunLeiVideInfo){
		m_xunLeiVideInfo = xunLeiVideInfo;
	}

	void getChilds();
	void onGetChildsComplete(CCNode* node, CCObject* obj);

private :
	XunLeiVideInfo m_xunLeiVideInfo;
	CCListView * tableView;
	CCTableViewCell * m_selectedCell;

	std::vector<XunLeiVideInfo> m_dates;
};

#endif /* _JOYPLUS_XUNLEIBTDETAILSENCE_H_ */
