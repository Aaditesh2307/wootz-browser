// Copyright 2025 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#include "content/public/browser/upload_blocking_prefs.h"

#include <algorithm>
#include <string>
#include <vector>

#include "base/values.h"
#include "components/pref_registry/pref_registry_syncable.h"
#include "components/prefs/pref_registry_simple.h"
#include "components/prefs/pref_service.h"
#include "components/user_prefs/user_prefs.h"

namespace content {

namespace upload_blocking_prefs {

// Pref name constants
const char kBlockedUploadDomains[] = "upload_blocking.blocked_domains";

void RegisterLocalStatePrefs(PrefRegistrySimple* registry) {
  // Register list preferences for blocked domains and URLs
  registry->RegisterListPref(kBlockedUploadDomains);
  registry->RegisterListPref(kBlockedUploadUrls);
  registry->RegisterBooleanPref(kUploadBlockingEnabled, true);
}

void RegisterProfilePrefs(user_prefs::PrefRegistrySyncable* registry) {
  // Register list preferences for blocked domains and URLs
  registry->RegisterListPref(kBlockedUploadDomains);
  registry->RegisterListPref(kBlockedUploadUrls);
  registry->RegisterBooleanPref(kUploadBlockingEnabled, true);
}

std::vector<std::string> GetBlockedDomains(PrefService* prefs) {
  std::vector<std::string> domains;
  const base::Value::List& domain_list = prefs->GetList(kBlockedUploadDomains);

  for (const auto& domain_value : domain_list) {
    if (domain_value.is_string()) {
      domains.push_back(domain_value.GetString());
    }
  }

  return domains;
}

bool IsUploadBlockingEnabled(PrefService* prefs) {
  return prefs->GetBoolean(kUploadBlockingEnabled);
}

void SetBlockedDomains(PrefService* prefs,
                       const std::vector<std::string>& domains) {
  base::Value::List domain_list;
  for (const auto& domain : domains) {
    if (!domain.empty()) {
      domain_list.Append(domain);
    }
  }
  prefs->SetList(kBlockedUploadDomains, std::move(domain_list));
}




}  // namespace upload_blocking_prefs
}  // namespace content
