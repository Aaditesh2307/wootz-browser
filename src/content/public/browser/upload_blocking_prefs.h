// Copyright 2025 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#ifndef CONTENT_PUBLIC_BROWSER_UPLOAD_BLOCKING_PREFS_H_
#define CONTENT_PUBLIC_BROWSER_UPLOAD_BLOCKING_PREFS_H_

#include <string>
#include <vector>

#include "content/common/content_export.h"

class PrefRegistrySimple;
class PrefService;

namespace user_prefs {
class PrefRegistrySyncable;
}

namespace content {
namespace upload_blocking_prefs {

// Pref names for upload blocking configuration
CONTENT_EXPORT extern const char kBlockedUploadDomains[];

// Register upload blocking preferences for local state (system-wide)
CONTENT_EXPORT void RegisterLocalStatePrefs(PrefRegistrySimple* registry);

// Register upload blocking preferences for user profile
CONTENT_EXPORT void RegisterProfilePrefs(
    user_prefs::PrefRegistrySyncable* registry);

CONTENT_EXPORT std::vector<std::string> GetBlockedDomains(PrefService* prefs);

CONTENT_EXPORT void SetBlockedDomains(PrefService* prefs,
                                      const std::vector<std::string>& domains);

}  // namespace upload_blocking_prefs

#endif  // CONTENT_PUBLIC_BROWSER_UPLOAD_BLOCKING_PREFS_H_
