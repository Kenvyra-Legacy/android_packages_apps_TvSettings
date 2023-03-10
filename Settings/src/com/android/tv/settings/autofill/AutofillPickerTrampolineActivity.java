/*
 * Copyright (C) 2018 The Android Open Source Project
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
package com.android.tv.settings.autofill;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.autofill.AutofillManager;

import androidx.fragment.app.FragmentActivity;

import com.android.tv.settings.library.settingslib.AutofillHelper;

/**
 * Standalone activity used to launch a {@link AutofillPickerActivity" from a
 * {@link android.provider.Settings#ACTION_REQUEST_SET_AUTOFILL_SERVICE} intent.
 *
 * <p>It first checks for cases that can fail fast, then forwards to {@link AutofillPickerActivity}
 * if necessary.
 */
public class AutofillPickerTrampolineActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // First check if the current user's service already belongs to the app...
        final Intent intent = getIntent();
        final String packageName = intent.getData().getSchemeSpecificPart();
        final ComponentName currentService = AutofillHelper.getCurrentAutofillAsComponentName(this);
        if (currentService != null && currentService.getPackageName().equals(packageName)) {
            // ...and succeed right away if it does.
            setResult(RESULT_OK);
            finish();
            return;
        }

        // Then check if the Autofill is available for the current user...
        final AutofillManager afm = getSystemService(AutofillManager.class);
        if (afm == null || !afm.hasAutofillFeature() || !afm.isAutofillSupported()) {
            // ... and fail right away if it is not.
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        // Otherwise, go ahead and show the real UI...
        final Intent newIntent = new Intent(this, AutofillPickerActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
                .setData(intent.getData());
        startActivity(newIntent);
        finish();
    }
}

