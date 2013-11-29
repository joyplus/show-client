#include "PincodeLayer.h"

bool PincodeLayer::init()
{
	bool bRet = false;
		do
		{
			if(! CCLayer::init())
			{
				LOGD("PincodeLayer","CCLayer init fail");
				break;
			}

//			m_background = CCSprite::create("selected1_user.png");
//			m_background->setPosition(ccp(522,274));
//			m_background->setOpacity(0);
//			this->addChild(m_background);

			imag_touxiang = CCImageView::createWithNetUrl("","defaultphoto.png",CCSizeMake(105,105),false, this);
			imag_touxiang->setPosition(ccp(164,510));


			CCSprite * sprite  = CCSprite::create("back1_user.png");
			sprite->setAnchorPoint(ccp(0,0));
			sprite->setPosition(ccp(0,176));
			sprite->setZOrder(1);
			addChild(sprite);

			CCSprite * light = CCSprite::create("light.png");
			light->setPosition(ccp(164,510));
			light->setZOrder(1);
			addChild(light);
			light->runAction(CCRepeatForever::create(CCRotateBy::create(0.1f,36.0f)));

			setContentSize(CCSizeMake(sprite->getContentSize().width, 550));

			CCSprite* pincode_back = CCSprite::create("pin.png");
			pincode_back->setPosition(ccp(164,285));
			pincode_back->setZOrder(1);
			this->addChild(pincode_back);

			CCLabelTTF* label_pincode = CCLabelTTF::create("", "Arial", 36.0);
			label_pincode->setPosition(ccp(164,285));
			label_pincode->setAnchorPoint(ccp(0.5,0.5));
			label_pincode->setZOrder(1);
			label_pincode->setTag(0);
			this->addChild(label_pincode);


			CCLabelTTF* label_name = CCLabelTTF::create("", "Arial", 30.0);
			label_name->setPosition(ccp(164,375));
			label_name->setAnchorPoint(ccp(0.5,0.5));
			label_name->setZOrder(1);
			label_name->setTag(2);
			this->addChild(label_name);

//			UILabel *label_name = UILabel::create();
//			label_name->setFocused(false);
//			label_name->setText("QQ");
//			label_name->setFontSize(30);
////			label_divider->setAnchorPoint(ccp(0,0));
//			label_name->setPosition(ccp(155,195));
//			m_uiLayer->addWidget(label_name);
			bRet = true;
		} while (0);
		return bRet;
}

void PincodeLayer::setSelected(bool isSelected) {
}
void PincodeLayer::runBreath(CCTime dt) {
}

void PincodeLayer::setPincode(const char* pincode){
	LOGD("pincode","%s",pincode);
	string buffer(pincode);
	char buff[17] = {};
	for(int i=0; i<buffer.size()*3;){
		if(i==15){
			buff[i]=buffer.at(i/3);
			buff[i+1]='\0';
		}else{
			buff[i]=buffer.at(i/3);
			buff[i+1]=' ';
			buff[i+2]=' ';
		}
		i+=3;
	}
	LOGD("pincode","%s",buff);
	CCLabelTTF* label_pincode = (CCLabelTTF*)this->getChildByTag(0);
	CCString* str = CCString::createWithFormat("%s",buff);
	label_pincode->setString(str->getCString());
//	label_pincode->initWithString(pincode, "Arial", 30.0);
}

void PincodeLayer::updateQQDisplay(const char* name, const char* url) {

	imag_touxiang = CCImageView::createWithNetUrl(url,"defaultphoto.png",CCSizeMake(105,105),false,this);
	imag_touxiang->setPosition(ccp(164,510));
	CCLabelTTF* label_name = (CCLabelTTF*)getChildByTag(2);
	label_name->setString(name);
}

CCSprite*  PincodeLayer::createMaskedSprite(CCImageView* src, const char* maskFile)
{
    CCSprite * mask = CCSprite::create(maskFile);

    assert(src);
    assert(mask);

    CCSize srcContent = src->getBoundSize();
    CCSize maskContent = mask->getContentSize();

    CCRenderTexture * rt = CCRenderTexture::create(srcContent.width, srcContent.height, kTexture2DPixelFormat_RGBA8888);

    float ratiow = srcContent.width / maskContent.width;
    float ratioh = srcContent.height / maskContent.height;
    mask->setScaleX(ratiow);
    mask->setScaleY(ratioh);

    mask->setPosition(ccp(srcContent.width / 2, srcContent.height / 2));
    src->setPosition(ccp(srcContent.width / 2, srcContent.height / 2));

    ccBlendFunc blendFunc2 = { GL_ONE, GL_ZERO };
    mask->setBlendFunc(blendFunc2);
    ccBlendFunc blendFunc3 = { GL_DST_ALPHA, GL_ZERO };
    src->setBlendFunc(blendFunc3);

    rt->begin();
    mask->visit();
    src->visit();
    rt->end();

    CCSprite * retval = CCSprite::createWithTexture(rt->getSprite()->getTexture());
    retval->setFlipY(true);
    return retval;
}

void PincodeLayer::onResult(const char* url, bool isSucced) {
	removeChildByTag(1);
	CCSprite* imag_touxiang_1 = createMaskedSprite(imag_touxiang,"yuan.png");
	imag_touxiang_1->setPosition(ccp(164,510));
	imag_touxiang_1->setTag(1);
	imag_touxiang_1->setZOrder(0);
	addChild(imag_touxiang_1);
}


