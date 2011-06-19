LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := helloneon

LOCAL_SRC_FILES := helloneon.c dtw.cpp ivy_mike/src/time.cpp
LOCAL_ARM_NEON := true
TARGET_ARCH_ABI := armeabi-v7a

ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
#    LOCAL_CFLAGS := -DHAVE_NEON=1
#    LOCAL_CPPFLAGS := -mfpu=neon -O3
    LOCAL_SRC_FILES += helloneon-intrinsics.c.neon
endif
LOCAL_C_INCLUDES += $(LOCAL_PATH)/ivy_mike/src
LOCAL_STATIC_LIBRARIES := cpufeatures

LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)

$(call import-module,cpufeatures)
