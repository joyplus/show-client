#include "CCTableCellForHistory.h"

CCTableCellForHistory* CCTableCellForHistory::create(const char* path)
{
	CCTableCellForHistory * image_view = new CCTableCellForHistory();
	if(image_view&&image_view->initWithFile(path))
	{
		image_view->autorelease();
		return image_view;
	}
	CC_SAFE_DELETE(image_view);
	return NULL;
}

CCTableCellForHistory::~CCTableCellForHistory() {
	// TODO Auto-generated destructor stub
}

void CCTableCellForHistory::visit()
{
	CCDirector* pDirector = CCDirector::sharedDirector();

	//启动遮罩效果
	glEnable(GL_SCISSOR_TEST);

	CCPoint pos = CCPointZero;
	pos = this->getParent()->convertToWorldSpace(pos);  //获取屏幕绝对位置
	CCRect rect = CCRectMake(pos.x, pos.y, this->getContentSize().width, 405);

	//设置遮罩效果
	float scaleX = pDirector->getOpenGLView()->getFrameSize().width/pDirector->getWinSize().width;
	float scaleY = pDirector->getOpenGLView()->getFrameSize().height/pDirector->getWinSize().height;
	float scale = MIN(scaleX,scaleY);
	glScissor(rect.origin.x*scale, rect.origin.y*scale,
			rect.size.width*scale, rect.size.height*scale);

	CCSprite::visit();

	//关闭遮罩效果
	glDisable(GL_SCISSOR_TEST);
}

