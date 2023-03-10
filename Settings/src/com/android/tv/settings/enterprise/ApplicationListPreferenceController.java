/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.tv.settings.enterprise;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.IconDrawableFactory;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.AppPreference;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.library.enterprise.apps.ApplicationFeatureProvider;
import com.android.tv.settings.library.enterprise.apps.UserAppInfo;

import java.util.List;

/**
 * PreferenceController that builds a dynamic list of applications provided by
 * {@link ApplicationListBuilder} instance.
 *
 * Forked from:
 * Settings/src/com/android/settings/enterprise/ApplicationListPreferenceController.java
 */
public class ApplicationListPreferenceController extends AbstractPreferenceController implements
        ApplicationFeatureProvider.ListOfAppsCallback {

    private static final String KEY_APP_LIST_DISCLOSURE_SETTINGS = "app_list_disclosure_settings";

    private final PackageManager mPm;
    private SettingsPreferenceFragment mParent;

    public ApplicationListPreferenceController(Context context, ApplicationListBuilder builder,
            PackageManager packageManager, SettingsPreferenceFragment parent) {
        super(context);
        mPm = packageManager;
        mParent = parent;
        builder.buildApplicationList(context, this);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_APP_LIST_DISCLOSURE_SETTINGS;
    }

    @Override
    public void onListOfAppsResult(List<UserAppInfo> result) {
        final PreferenceScreen screen = mParent.getPreferenceScreen();
        if (screen == null) {
            return;
        }
        final IconDrawableFactory iconDrawableFactory = IconDrawableFactory.newInstance(mContext);
        final Context prefContext = mParent.getPreferenceManager().getContext();
        for (int position = 0; position < result.size(); position++) {
            final UserAppInfo item = result.get(position);
            final Preference preference = new AppPreference(prefContext);
            preference.setTitle(item.appInfo.loadLabel(mPm));
            preference.setIcon(iconDrawableFactory.getBadgedIcon(item.appInfo));
            preference.setOrder(position);
            preference.setSelectable(false);
            screen.addPreference(preference);
        }
    }

    /**
     * Simple interface for building application list within {
     *
     * @link ApplicationListPreferenceController}
     */
    public interface ApplicationListBuilder {
        void buildApplicationList(Context context,
                ApplicationFeatureProvider.ListOfAppsCallback callback);
    }
}
