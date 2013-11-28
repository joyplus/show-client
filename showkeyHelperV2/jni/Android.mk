LOCAL_PATH := $(call my-dir)



include $(CLEAR_VARS)

LOCAL_MODULE :=	helper_shared 

LOCAL_MODULE_FILENAME := libhelper

LOCAL_SRC_FILES := showkeyhelper/main.cpp \
                   showkeyhelper/classes/AppDelegate.cpp \
                   showkeyhelper/classes/MainScene.cpp\
                   showkeyhelper/classes/PageLayer.cpp\
                   showkeyhelper/classes/MainItemLayer.cpp\
                   showkeyhelper/classes/BannerLayer.cpp\
                   showkeyhelper/classes/PincodeLayer.cpp\
                   showkeyhelper/classes/HistoryScnce.cpp\
                   showkeyhelper/classes/XunLeiYunSence.cpp\
                   showkeyhelper/classes/BaiduYunSence.cpp\
                   showkeyhelper/classes/HistoryBtDetailsSence.cpp\
                   showkeyhelper/classes/SettingSence.cpp\
                   showkeyhelper/classes/XunLeiBTdetailSence.cpp\
                   showkeyhelper/classes/TransitionScenes.cpp\
                   showkeyhelper/classes/ui/CCImageView.cpp\
                   showkeyhelper/classes/ui/CCListView.cpp\
                   showkeyhelper/classes/ui/CCTableCellForHistory.cpp\
                   showkeyhelper/classes/ui/CCButtonView.cpp\
                   showkeyhelper/classes/ui/CCButton.cpp

LOCAL_C_INCLUDES := $(LOCAL_PATH)/showkeyhelper/classes \
					$(LOCAL_PATH)cocos2dx-2.2/extensions

LOCAL_WHOLE_STATIC_LIBRARIES := cocos2dx_static cocos_extension_static

LOCAL_LDLIBS    := -ljnigraphics

include $(BUILD_SHARED_LIBRARY)


$(call import-add-path,$(LOCAL_PATH)/cocos2dx-2.2)
$(call import-module,cocos2dx)\
$(call import-module,extensions)

