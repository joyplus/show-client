#include "CCListView.h"

CCListView::CCListView()
: m_pTouchedCell(NULL)
, m_pIndices(NULL)
, m_pCellsUsed(NULL)
, m_pCellsFreed(NULL)
, m_pDataSource(NULL)
, m_pTableViewDelegate(NULL)
, m_selectedId(0)
, _offsetPoint(CCPointZero)
,_paddingLeft(0.0f)
,_paddingTop(0.0f)
,_paddingRight(0.0f)
,_paddingBottom(0.0f)
, m_eOldDirection(kCCScrollViewDirectionNone)
{

}

CCListView::~CCListView()
{
	// TODO Auto-generated destructor stub
}

CCListView* CCListView::create(CCListViewDataResouce* dataSource,
		CCSize size)
{
	return CCListView::create(dataSource, size, NULL);
}

CCListView* CCListView::create(CCListViewDataResouce* dataSource, CCSize size,
		CCNode* container,
		float paddingLeft,
		float paddingTop,
		float paddingRight,
		float paddingBottom)
{
	CCListView *listView = new CCListView();
	listView->_paddingLeft = paddingLeft;
	listView->_paddingTop = paddingTop;
	listView->_paddingRight = paddingRight;
	listView->_paddingBottom = paddingBottom;
	listView->initWithViewSize(size, container);
	listView->autorelease();
	listView->setDataSource(dataSource);
	listView->_updateCellPositions();
	listView->_updateContentSize();

	return listView;
}

CCListView* CCListView::create(CCListViewDataResouce* dataSource, CCSize size,
		CCNode* container)
{
	return CCListView::create(dataSource, size, NULL,0.0f,0.0f,0.0f,0.0f);
}

void CCListView::setVerticalFillOrder(CCListViewVerticalFillOrder order)
{
	if (m_eVordering != order) {
		m_eVordering = order;
		if (m_pCellsUsed->count() > 0)
		{
			this->reloadData();
		}
	}
}

CCListViewVerticalFillOrder CCListView::getVerticalFillOrder()
{
	return m_eVordering;
}

bool CCListView::initWithViewSize(CCSize size, CCNode* container)
{
	if (CCScrollView::initWithViewSize(size,container))
	{
		m_pCellsUsed      = new CCArrayForObjectSorting();
		m_pCellsFreed     = new CCArrayForObjectSorting();
		m_pIndices        = new std::set<unsigned int>();
		m_eVordering      = kCCListViewFillBottomUp;
		this->setDirection(kCCScrollViewDirectionVertical);

		CCScrollView::setDelegate(this);
		return true;
	}
	return false;

}

void CCListView::updateCellAtIndex(unsigned int idx)
{
	if (idx == CC_INVALID_INDEX)
	    {
	        return;
	    }
	unsigned int uCountOfItems = m_pDataSource->numberOfCellsInTableView(this);
	if (0 == uCountOfItems || idx > uCountOfItems-1)
	{
		return;
	}

	CCTableViewCell* cell = this->cellAtIndex(idx);
	if (cell)
	{
		this->_moveCellOutOfSight(cell);
	}
	cell = m_pDataSource->tableCellAtIndex(this, idx);
	this->_setIndexForCell(idx, cell);
	this->_addCellIfNecessary(cell);

}

void CCListView::insertCellAtIndex(unsigned int idx)
{
	if (idx == CC_INVALID_INDEX)
	{
		return;
	}

	unsigned int uCountOfItems = m_pDataSource->numberOfCellsInTableView(this);
	if (0 == uCountOfItems || idx > uCountOfItems-1)
	{
		return;
	}

	CCTableViewCell* cell = NULL;
	int newIdx = 0;

	cell = (CCTableViewCell*)m_pCellsUsed->objectWithObjectID(idx);
	if (cell)
	{
		newIdx = m_pCellsUsed->indexOfSortedObject(cell);
		for (unsigned int i=newIdx; i<m_pCellsUsed->count(); i++)
		{
			cell = (CCTableViewCell*)m_pCellsUsed->objectAtIndex(i);
			this->_setIndexForCell(cell->getIdx()+1, cell);
		}
	}

 //   [m_pIndices shiftIndexesStartingAtIndex:idx by:1];

	//insert a new cell
	cell = m_pDataSource->tableCellAtIndex(this, idx);
	this->_setIndexForCell(idx, cell);
	this->_addCellIfNecessary(cell);

	this->_updateCellPositions();
	this->_updateContentSize();
}

void CCListView::removeCellAtIndex(unsigned int idx)
{
	if (idx == CC_INVALID_INDEX)
	{
		return;
	}

	unsigned int uCountOfItems = m_pDataSource->numberOfCellsInTableView(this);
	if (0 == uCountOfItems || idx > uCountOfItems-1)
	{
		return;
	}

	unsigned int newIdx = 0;

	CCTableViewCell* cell = this->cellAtIndex(idx);
	if (!cell)
	{
		return;
	}

	newIdx = m_pCellsUsed->indexOfSortedObject(cell);

	//remove first
	this->_moveCellOutOfSight(cell);

	m_pIndices->erase(idx);
	this->_updateCellPositions();
//    [m_pIndices shiftIndexesStartingAtIndex:idx+1 by:-1];
	for (unsigned int i=m_pCellsUsed->count()-1; i > newIdx; i--)
	{
		cell = (CCTableViewCell*)m_pCellsUsed->objectAtIndex(i);
		this->_setIndexForCell(cell->getIdx()-1, cell);
	}
}

void CCListView::reloadData()
{
	m_eOldDirection = kCCScrollViewDirectionNone;
	CCObject* pObj = NULL;
	CCARRAY_FOREACH(m_pCellsUsed, pObj)
	{
		CCTableViewCell* cell = (CCTableViewCell*)pObj;

		if(m_pTableViewDelegate != NULL) {
			m_pTableViewDelegate->tableCellWillRecycle(this, cell);
		}

		m_pCellsFreed->addObject(cell);
		cell->reset();
		if (cell->getParent() == this->getContainer())
		{
			this->getContainer()->removeChild(cell, true);
		}
	}

	m_pIndices->clear();
	m_pCellsUsed->release();
	m_pCellsUsed = new CCArrayForObjectSorting();

	this->_updateCellPositions();
	this->_updateContentSize();
	if (m_pDataSource->numberOfCellsInTableView(this) > 0)
	{
		this->scrollViewDidScroll(this);
	}
}

CCTableViewCell* CCListView::dequeueCell()
{
	CCTableViewCell *cell;

	if (m_pCellsFreed->count() == 0) {
		cell = NULL;
	} else {
		cell = (CCTableViewCell*)m_pCellsFreed->objectAtIndex(0);
		cell->retain();
		m_pCellsFreed->removeObjectAtIndex(0);
		cell->autorelease();
	}
	return cell;
}

CCTableViewCell* CCListView::cellAtIndex(unsigned int idx)
{
	CCTableViewCell *found = NULL;

	if (m_pIndices->find(idx) != m_pIndices->end())
	{
		found = (CCTableViewCell *)m_pCellsUsed->objectWithObjectID(idx);
	}

	return found;
}

void CCListView::scrollViewDidScroll(CCScrollView* view)
{
	unsigned int uCountOfItems = m_pDataSource->numberOfCellsInTableView(this);
	if (0 == uCountOfItems)
	{
		return;
	}

	if(m_pTableViewDelegate != NULL) {
		//m_pTableViewDelegate->scrollViewDidScroll(this);
	}

	unsigned int startIdx = 0, endIdx = 0, idx = 0, maxIdx = 0;
	CCPoint offset = ccpMult(this->getContentOffset(), -1);
	maxIdx = MAX(uCountOfItems-1, 0);

	if (m_eVordering == kCCListViewFillTopDown)
	{
		offset.y = offset.y + m_tViewSize.height/this->getContainer()->getScaleY();
	}

	int left = offset.x - m_tViewSize.width/this->getContainer()->getScaleX();

	offset.x = MAX(0,left);

	startIdx = this->_indexFromOffset(offset);
	if (startIdx == CC_INVALID_INDEX)
	{
		startIdx = uCountOfItems - 1;
	}

//	LOGD("ListView","start id = %ud",startIdx);

	CCPoint offset_1 = ccpMult(this->getContentOffset(), -1);
	if (m_eVordering == kCCListViewFillTopDown)
	{
		offset_1.y -= m_tViewSize.height/this->getContainer()->getScaleY();
	}
	else
	{
		offset_1.y += m_tViewSize.height/this->getContainer()->getScaleY();
	}

	offset_1.x += ((m_tViewSize.width/this->getContainer()->getScaleX())*2);

	endIdx   = this->_indexFromOffset(offset_1);
	if (endIdx == CC_INVALID_INDEX)
	{
		endIdx = uCountOfItems - 1;
	}
//	LOGD("ListView","end id = %ud",endIdx);

#if 0 // For Testing.
	CCObject* pObj;
	int i = 0;
	CCARRAY_FOREACH(m_pCellsUsed, pObj)
	{
		CCTableViewCell* pCell = (CCTableViewCell*)pObj;
		CCLog("cells Used index %d, value = %d", i, pCell->getIdx());
		i++;
	}
	CCLog("---------------------------------------");
	i = 0;
	CCARRAY_FOREACH(m_pCellsFreed, pObj)
	{
		CCTableViewCell* pCell = (CCTableViewCell*)pObj;
		CCLog("cells freed index %d, value = %d", i, pCell->getIdx());
		i++;
	}
	CCLog("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
#endif

	if (m_pCellsUsed->count() > 0)
	{
		CCTableViewCell* cell = (CCTableViewCell*)m_pCellsUsed->objectAtIndex(0);

		idx = cell->getIdx();
		while(idx <startIdx)
		{
			this->_moveCellOutOfSight(cell);
			if (m_pCellsUsed->count() > 0)
			{
				cell = (CCTableViewCell*)m_pCellsUsed->objectAtIndex(0);
				idx = cell->getIdx();
			}
			else
			{
				break;
			}
		}
	}
	if (m_pCellsUsed->count() > 0)
	{
		CCTableViewCell *cell = (CCTableViewCell*)m_pCellsUsed->lastObject();
		idx = cell->getIdx();

		while(idx <= maxIdx && idx > endIdx)
		{
			this->_moveCellOutOfSight(cell);
			if (m_pCellsUsed->count() > 0)
			{
				cell = (CCTableViewCell*)m_pCellsUsed->lastObject();
				idx = cell->getIdx();

			}
			else
			{
				break;
			}
		}
	}

	for (unsigned int i=startIdx; i <= endIdx; i++)
	{
		//if ([m_pIndices containsIndex:i])
		if (m_pIndices->find(i) != m_pIndices->end())
		{
			continue;
		}
		this->updateCellAtIndex(i);
	}
}

bool CCListView::ccTouchBegan(CCTouch* pTouch, CCEvent* pEvent) {
}

void CCListView::ccTouchMoved(CCTouch* pTouch, CCEvent* pEvent) {
}

void CCListView::ccTouchEnded(CCTouch* pTouch, CCEvent* pEvent) {
}

void CCListView::ccTouchCancelled(CCTouch* pTouch, CCEvent* pEvent) {
}

int CCListView::__indexFromOffset(CCPoint offset)
{
	int low = 0;
	int high = m_pDataSource->numberOfCellsInTableView(this) - 1;
	float search;
	switch (this->getDirection())
	{
		case kCCScrollViewDirectionHorizontal:
			search = offset.x;
			break;
		default:
			search = offset.y;
			break;
	}

	while (high >= low)
	{
		int index = low + (high - low) / 2;
		float cellStart = m_vCellsPositions[index];
		float cellEnd = m_vCellsPositions[index + 1];

		if (search >= cellStart && search <= cellEnd)
		{
			return index;
		}
		else if (search < cellStart)
		{
			high = index - 1;
		}
		else
		{
			low = index + 1;
		}
	}

	if (low <= 0) {
		return 0;
	}

	return -1;
}

unsigned int CCListView::_indexFromOffset(CCPoint offset)
{
	int index = 0;
	const int maxIdx = m_pDataSource->numberOfCellsInTableView(this)-1;

	if (m_eVordering == kCCListViewFillTopDown)
	{
		offset.y = this->getContainer()->getContentSize().height - offset.y;
	}
	index = this->__indexFromOffset(offset);
	if (index != -1)
	{
		index = MAX(0, index);
		if (index > maxIdx)
		{
			index = CC_INVALID_INDEX;
		}
	}

	return index;
}

CCPoint CCListView::__offsetFromIndex(unsigned int index)
{
	CCPoint offset;
	CCSize  cellSize;

	switch (this->getDirection())
	{
		case kCCScrollViewDirectionHorizontal:
			offset = ccp(m_vCellsPositions[index], 0.0f);
			break;
		default:
			offset = ccp(0.0f, m_vCellsPositions[index]);
			break;
	}

	return offset;
}

CCPoint CCListView::_offsetFromIndex(unsigned int index)
{
	CCPoint offset = this->__offsetFromIndex(index);

	const CCSize cellSize = m_pDataSource->tableCellSizeForIndex(this, index);
	if (m_eVordering == kCCListViewFillTopDown)
	{
		offset.y = this->getContainer()->getContentSize().height - offset.y - cellSize.height;
	}
	return offset;
}

void CCListView::_moveCellOutOfSight(CCTableViewCell* cell)
{
	if(m_pTableViewDelegate != NULL) {
		m_pTableViewDelegate->tableCellWillRecycle(this, cell);
	}

	m_pCellsFreed->addObject(cell);
	m_pCellsUsed->removeSortedObject(cell);
	m_pIndices->erase(cell->getIdx());
	// [m_pIndices removeIndex:cell.idx];
	cell->reset();
	if (cell->getParent() == this->getContainer()) {
		this->getContainer()->removeChild(cell, true);;
	}
}

void CCListView::_setIndexForCell(unsigned int index, CCTableViewCell* cell)
{
	cell->setAnchorPoint(ccp(0.0f, 0.0f));
	cell->setPosition(this->_offsetFromIndex(index));
	cell->setIdx(index);
}

void CCListView::_addCellIfNecessary(CCTableViewCell* cell)
{
	if (cell->getParent() != this->getContainer())
	{
		this->getContainer()->addChild(cell);
	}
	m_pCellsUsed->insertSortedObject(cell);
	m_pIndices->insert(cell->getIdx());
}

void CCListView::_updateCellPositions()
{
	int cellsCount = m_pDataSource->numberOfCellsInTableView(this);
	m_vCellsPositions.resize(cellsCount + 1, 0.0);

	if (cellsCount > 0)
	{
		float currentPos = 0;
		switch (this->getDirection())
		{
			case kCCScrollViewDirectionHorizontal:
				currentPos = _paddingLeft;
				break;
			default:
				currentPos = _paddingTop;
				break;
		}
		CCSize cellSize;
		for (int i=0; i < cellsCount; i++)
		{
			m_vCellsPositions[i] = currentPos;
			cellSize = m_pDataSource->tableCellSizeForIndex(this, i);
			switch (this->getDirection())
			{
				case kCCScrollViewDirectionHorizontal:
					currentPos += cellSize.width;
					break;
				default:
					currentPos += cellSize.height;
					break;
			}
		}
		m_vCellsPositions[cellsCount] = currentPos+_paddingRight;//1 extra value allows us to get right/bottom of the last cell
	}
}

void CCListView::_updateContentSize()
{
	CCSize size = CCSizeZero;
	unsigned int cellsCount = m_pDataSource->numberOfCellsInTableView(this);

	if (cellsCount > 0)
	{
		float maxPosition = m_vCellsPositions[cellsCount];

		switch (this->getDirection())
		{
			case kCCScrollViewDirectionHorizontal:
				size = CCSizeMake(maxPosition + _paddingRight, m_tViewSize.height);
				break;
			default:
				size = CCSizeMake(m_tViewSize.width, maxPosition + _paddingBottom);
				break;
		}
	}

	this->setContentSize(size);

	if (m_eOldDirection != m_eDirection)
	{
		if (m_eDirection == kCCScrollViewDirectionHorizontal)
		{
			//this->setContentOffset(ccp(0,0));
			setContentOffset(_offsetPoint);
		}
		else
		{
			this->setContentOffset(ccp(0,this->minContainerOffset().y));
		}
		m_eOldDirection = m_eDirection;
	}
}

bool CCListView::onKeyArrowClicked(int arrow)
{
	bool flag = false;
	switch (arrow)
	{
		case ccKeypadMSGType(kTypeLeftArrowClicked):
			if(m_eDirection == kCCScrollViewDirectionHorizontal)
			{
				if(m_selectedId>0){
					_setSelectedPre();
					flag = true;
				}
			}
			break;
		case ccKeypadMSGType(kTypeUpArrowClicked):
			if(m_eDirection == kCCScrollViewDirectionVertical)
			{
				if(m_selectedId>0){
					_setSelectedPre();
					flag = true;
				}
			}
			break;
		case ccKeypadMSGType(kTypeRightArrowClicked):
			if(m_eDirection == kCCScrollViewDirectionHorizontal)
			{
				if(m_selectedId<m_pDataSource->numberOfCellsInTableView(this)-1){
					_setSelectedNext();
					flag = true;
				}
			}
			break;
		case ccKeypadMSGType(kTypeDownArrowClicked):
			if(m_eDirection == kCCScrollViewDirectionVertical)
			{
				if(m_selectedId<m_pDataSource->numberOfCellsInTableView(this)-1){
					_setSelectedNext();
					flag = true;
				}
			}
			break;
	}
	return flag;
}

bool CCListView::onKeyEnterClicked()
{
	m_pTableViewDelegate->tableCellClicked(this,cellAtIndex(m_selectedId),m_selectedId);
}

void CCListView::setSelection(int selection)
{
	if(m_pContainer->getActionManager()->numberOfRunningActionsInTarget(m_pContainer)>0){
//		LOGD("ListView","return ------------> %d",m_pContainer->getActionManager()->numberOfRunningActionsInTarget(m_pContainer));
		return;
	}

//	LOGD("ListView","selection = %ud", selection);

	if(selection < 0){
		selection = 0;
	}
	if(selection>=m_pDataSource->numberOfCellsInTableView(this))
	{
		selection = m_pDataSource->numberOfCellsInTableView(this)-1;
	}

//	LOGD("ListView","selection = %ud", selection);

	// reset the view
	m_selectedId = selection;

	if(m_vCellsPositions[m_pDataSource->numberOfCellsInTableView(this)]<=getViewSize().width)//竖列的暂时没有考虑
	{

	}else
	{
		CCPoint offsetPoint = __offsetFromIndex(m_selectedId);
			CCSize cellSize = m_pDataSource->tableCellSizeForIndex(this,m_selectedId);
			CCPoint offsetEndPoint = __offsetFromIndex(m_pDataSource->numberOfCellsInTableView(this));
			CCPoint distancePoint;
			CCSize viewSize = getViewSize();
			switch (this->getDirection()) {
				case kCCScrollViewDirectionHorizontal:
					if(offsetPoint.x<viewSize.width/2)
					{
						distancePoint = CCPointZero;
					}else if(offsetPoint.x>(offsetEndPoint.x-viewSize.width/2))
					{
						distancePoint = ccp(-(offsetEndPoint.x-viewSize.width),0);

					}else{
						distancePoint = ccp(-(offsetPoint.x-viewSize.width/2+cellSize.width/2),0);
					}
					if(_offsetPoint.x!=distancePoint.x||_offsetPoint.y!=distancePoint.y){
						float duration = fabs(_offsetPoint.x-distancePoint.x)/500;
						duration = MIN(0.8f,duration);
						setContentOffsetInDurationByexpIn(distancePoint, duration);
						_offsetPoint = distancePoint;
					}
					break;
				default:
					//竖列暂未实现
					break;
			}
	}

	if(m_pTableViewDelegate){
		m_pTableViewDelegate->tableCellSelected(this,cellAtIndex(m_selectedId),m_selectedId);
	}
}

unsigned int CCListView::getSelected()
{
	return m_selectedId;
}

void CCListView::_setSelectedNext()
{
	unsigned int nextselected = m_selectedId+1;
	setSelection(nextselected);
}

void CCListView::_setSelectedPre()
{
	unsigned int preSelected = m_selectedId-1;
	setSelection(preSelected);
}

void CCListView::unregisterAllScriptHandler()
{
	unregisterScriptHandler(kTableViewScroll);
	unregisterScriptHandler(kTableViewZoom);
	unregisterScriptHandler(kTableCellTouched);
	unregisterScriptHandler(kTableCellHighLight);
	unregisterScriptHandler(kTableCellUnhighLight);
	unregisterScriptHandler(kTableCellWillRecycle);
	unregisterScriptHandler(kTableCellSizeForIndex);
	unregisterScriptHandler(kTableCellSizeAtIndex);
	unregisterScriptHandler(kNumberOfCellsInTableView);
}



