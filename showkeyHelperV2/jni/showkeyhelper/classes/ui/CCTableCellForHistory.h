#ifndef _JOYPLUS_CCTABLECELLFORHISTORY_H_
#define _JOYPLUS_CCTABLECELLFORHISTORY_H_
#include "cocos2d.h"
#include "cocos-ext.h"

USING_NS_CC_EXT;
USING_NS_CC;

class CCTableCellForHistory : public CCSprite{

public:
	static CCTableCellForHistory *create(const char * fileName);
	void visit();
	virtual ~CCTableCellForHistory();
};

#endif /* _JOYPLUS_CCTABLECELLFORHISTORY_H_ */
