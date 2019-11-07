LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := cocos2dlua_shared

LOCAL_MODULE_FILENAME := libcocos2dlua

LOCAL_LDLIBS := -llog

LOCAL_C_INCLUDES  += system/core/include/cutils

LOCAL_SHARED_LIBRARIES := libcutils

LOCAL_SRC_FILES := hellolua/main.cpp \

include $(BUILD_SHARED_LIBRARY)

