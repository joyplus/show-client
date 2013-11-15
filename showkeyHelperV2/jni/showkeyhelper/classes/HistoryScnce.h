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

class HistoryScnce : public CCLayer , public CCListViewDataResouce , public CCListViewDelegate
{
public:
	virtual bool init();
	virtual void keyBackClicked();//Android 返回键
	virtual void keyArrowClicked(int arrow);//方向键
	virtual void keyEnterClicked();//回车键

	static CCScene* scene();

	CREATE_FUNC(HistoryScnce);

	virtual ~HistoryScnce();


	virtual void tableCellClicked(CCListView *table, CCTableViewCell* cell, unsigned int idx);

	virtual void tableCellSelected(CCListView *table, CCTableViewCell* cell, unsigned int idx);

	virtual CCSize tableCellSizeForIndex(CCListView *table, unsigned int idx);

	virtual CCTableViewCell* tableCellAtIndex(CCListView *table, unsigned int idx);

	virtual unsigned int numberOfCellsInTableView(CCListView *table);

private :
	CCListView * tableView;
	CCTableViewCell * m_selectedCell;
};

#endif //_JOYPLUS_HISTORYSENCE_H_
