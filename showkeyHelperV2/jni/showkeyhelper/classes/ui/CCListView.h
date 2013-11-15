#ifndef _JOYPLUS_CCLISTVIEW_H_
#define _JOYPLUS_CCLISTVIEW_H_

#include "cocos2d.h"
#include "cocos-ext.h"
#include "CCScrollView.h"
#include "CCTableViewCell.h"
#include "Log.h"
#include <set>
#include <vector>

USING_NS_CC_EXT;
USING_NS_CC;

class CCListView;

typedef enum {
    kCCListViewFillTopDown,
    kCCListViewFillBottomUp
} CCListViewVerticalFillOrder;


class CCListViewDelegate
{
public:
	virtual void tableCellClicked(CCListView *table, CCTableViewCell* cell, unsigned int idx) = 0;

	virtual void tableCellSelected(CCListView *table, CCTableViewCell* cell, unsigned int idx) = 0;

	virtual void tableCellWillRecycle(CCListView* table, CCTableViewCell* cell){};
};

class CCListViewDataResouce
{
public:
	virtual CCSize tableCellSizeForIndex(CCListView *table, unsigned int idx) = 0;

	virtual CCTableViewCell* tableCellAtIndex(CCListView *table, unsigned int idx) = 0;

	virtual unsigned int numberOfCellsInTableView(CCListView *table) = 0;
};

class CCListView : public CCScrollView , public CCScrollViewDelegate
{
public:

	CCListView();
	virtual ~CCListView();

	bool onKeyArrowClicked(int arrow);//方向键
	bool onKeyEnterClicked();//回车键

	void setSelection(int selection);
	unsigned int getSelected();

	/**
	 * An intialized table view object
	 *
	 * @param dataSource data source
	 * @param size view size
	 * @return table view
	 */
	static CCListView* create(CCListViewDataResouce* dataSource, CCSize size);
	/**
	 * An initialized table view object
	 *
	 * @param dataSource data source;
	 * @param size view size
	 * @param container parent object for cells
	 * @return table view
	 */
	static CCListView* create(CCListViewDataResouce* dataSource, CCSize size, CCNode *container);
	static CCListView* create(CCListViewDataResouce* dataSource, CCSize size, CCNode *container,
						float padingLeft, float paddingTop, float paddingRight, float paddingBottom);

	/**
	 * data source
	 * @js NA
	 */
	CCListViewDataResouce* getDataSource() { return m_pDataSource; }
	void setDataSource(CCListViewDataResouce* source) { m_pDataSource = source; }
	/**
	 * delegate
	 * @js NA
	 */
	CCListViewDelegate* getDelegate() { return m_pTableViewDelegate; }
	void setDelegate(CCListViewDelegate* pDelegate) { m_pTableViewDelegate = pDelegate; }

	/**
	 * determines how cell is ordered and filled in the view.
	 */
	void setVerticalFillOrder(CCListViewVerticalFillOrder order);
	CCListViewVerticalFillOrder getVerticalFillOrder();


	bool initWithViewSize(CCSize size, CCNode* container = NULL);
	/**
	 * Updates the content of the cell at a given index.
	 *
	 * @param idx index to find a cell
	 */
	void updateCellAtIndex(unsigned int idx);
	/**
	 * Inserts a new cell at a given index
	 *
	 * @param idx location to insert
	 */
	void insertCellAtIndex(unsigned int idx);
	/**
	 * Removes a cell at a given index
	 *
	 * @param idx index to find a cell
	 */
	void removeCellAtIndex(unsigned int idx);
	/**
	 * reloads data from data source.  the view will be refreshed.
	 */
	void reloadData();
	/**
	 * Dequeues a free cell if available. nil if not.
	 *
	 * @return free cell
	 */
	CCTableViewCell *dequeueCell();

	/**
	 * Returns an existing cell at a given index. Returns nil if a cell is nonexistent at the moment of query.
	 *
	 * @param idx index
	 * @return a cell at a given index
	 */
	CCTableViewCell *cellAtIndex(unsigned int idx);


	virtual void scrollViewDidScroll(CCScrollView* view);
	virtual void scrollViewDidZoom(CCScrollView* view) {}

	virtual bool ccTouchBegan(CCTouch *pTouch, CCEvent *pEvent);
	virtual void ccTouchMoved(CCTouch *pTouch, CCEvent *pEvent);
	virtual void ccTouchEnded(CCTouch *pTouch, CCEvent *pEvent);
	virtual void ccTouchCancelled(CCTouch *pTouch, CCEvent *pEvent);

protected:

	unsigned int m_selectedId;

	float _paddingLeft,_paddingTop,_paddingRight,_paddingBottom;

	CCPoint _offsetPoint;

	CCTableViewCell *m_pTouchedCell;
	/**
	 * vertical direction of cell filling
	 */
	CCListViewVerticalFillOrder m_eVordering;

	/**
	 * index set to query the indexes of the cells used.
	 */
	std::set<unsigned int>* m_pIndices;

	/**
	 * vector with all cell positions
	 */
	std::vector<float> m_vCellsPositions;
	//NSMutableIndexSet *indices_;
	/**
	 * cells that are currently in the table
	 */
	CCArrayForObjectSorting* m_pCellsUsed;
	/**
	 * free list of cells
	 */
	CCArrayForObjectSorting* m_pCellsFreed;
	/**
	 * weak link to the data source object
	 */
	CCListViewDataResouce* m_pDataSource;
	/**
	 * weak link to the delegate object
	 */
	CCListViewDelegate* m_pTableViewDelegate;

	CCScrollViewDirection m_eOldDirection;

	int __indexFromOffset(CCPoint offset);
	unsigned int _indexFromOffset(CCPoint offset);
	CCPoint __offsetFromIndex(unsigned int index);
	CCPoint _offsetFromIndex(unsigned int index);

	void _moveCellOutOfSight(CCTableViewCell *cell);
	void _setIndexForCell(unsigned int index, CCTableViewCell *cell);
	void _addCellIfNecessary(CCTableViewCell * cell);

	void _setSelectedNext();
	void _setSelectedPre();

	void _updateCellPositions();
public:
	void _updateContentSize();

	enum TableViewScriptEventType
	{
		kTableViewScroll   = 0,
		kTableViewZoom,
		kTableCellTouched,
		kTableCellHighLight,
		kTableCellUnhighLight,
		kTableCellWillRecycle,
		kTableCellSizeForIndex,
		kTableCellSizeAtIndex,
		kNumberOfCellsInTableView,
	};
	void unregisterAllScriptHandler();
};

#endif /* _JOYPLUS_CCLISTVIEW_H_ */
