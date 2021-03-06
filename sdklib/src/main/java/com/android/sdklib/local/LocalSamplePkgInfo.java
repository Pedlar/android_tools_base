/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.android.sdklib.local;

import com.android.annotations.NonNull;
import com.android.annotations.Nullable;
import com.android.sdklib.AndroidVersion;
import com.android.sdklib.internal.repository.packages.Package;
import com.android.sdklib.internal.repository.packages.SamplePackage;
import com.android.sdklib.repository.MajorRevision;

import java.io.File;
import java.util.Properties;

/**
 * Local sample package, for a given platform's {@link AndroidVersion}.
 * The package itself has a major revision.
 * There should be only one for a given android platform version.
 */
public class LocalSamplePkgInfo extends LocalAndroidVersionPkgInfo {

    @NonNull
    private final MajorRevision mRevision;

    public LocalSamplePkgInfo(@NonNull LocalSdk localSdk,
                              @NonNull File localDir,
                              @NonNull Properties sourceProps,
                              @NonNull AndroidVersion version,
                              @NonNull MajorRevision revision) {
        super(localSdk, localDir, sourceProps, version);
        mRevision = revision;
    }

    @Override
    public int getType() {
        return LocalSdk.PKG_SAMPLES;
    }

    @Override
    public boolean hasMajorRevision() {
        return true;
    }

    @NonNull
    @Override
    public MajorRevision getMajorRevision() {
        return mRevision;
    }

    @Nullable
    @Override
    public Package getPackage() {
        Package pkg = super.getPackage();
        if (pkg == null) {
            try {
                pkg = SamplePackage.create(getLocalDir().getPath(), getSourceProperties());
                setPackage(pkg);
            } catch (Exception e) {
                appendLoadError("Failed to parse package: %1$s", e.toString());
            }
        }
        return pkg;
    }
}
