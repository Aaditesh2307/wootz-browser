// Copyright 2025 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#include "content/browser/upload/upload_blocked_dialog_bridge.h"

#include "base/android/jni_android.h"
#include "base/android/jni_string.h"
#include "base/android/scoped_java_ref.h"
#include "base/logging.h"
#include "content/public/browser/web_contents.h"
#include "ui/android/window_android.h"

// Forward declaration for JNI header
namespace content {
class UploadBlockedDialogBridge;
}
using UploadBlockedDialogBridge = content::UploadBlockedDialogBridge;

#include "content/public/android/content_jni_headers/UploadBlockedDialogBridge_jni.h"

using base::android::AttachCurrentThread;
using base::android::ConvertUTF8ToJavaString;
using base::android::JavaParamRef;
using base::android::ScopedJavaLocalRef;

namespace content {

UploadBlockedDialogBridge::UploadBlockedDialogBridge() {
  JNIEnv* env = AttachCurrentThread();
  java_object_.Reset(Java_UploadBlockedDialogBridge_create(
      env, reinterpret_cast<intptr_t>(this)));
}

UploadBlockedDialogBridge::~UploadBlockedDialogBridge() {
  Java_UploadBlockedDialogBridge_destroy(AttachCurrentThread(), java_object_);
}
// static
void UploadBlockedDialogBridge::ShowDialog(WebContents* web_contents,
                                           const std::string& message) {
  if (!web_contents) {
    LOG(ERROR)
        << "UploadBlockedDialogBridge: No WebContents available for dialog";
    return;
  }

  JNIEnv* env = AttachCurrentThread();
  ScopedJavaLocalRef<jstring> java_message =
      ConvertUTF8ToJavaString(env, message);

  LOG(INFO) << "UploadBlockedDialogBridge: Showing dialog with message: "
            << message;

  // Call static Java method to show dialog
  Java_UploadBlockedDialogBridge_ShowDialog(
      env, web_contents->GetJavaWebContents(), java_message);
}
}  // namespace content
