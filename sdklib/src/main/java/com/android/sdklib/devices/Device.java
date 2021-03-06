/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.android.sdklib.devices;

import com.android.annotations.NonNull;
import com.android.annotations.Nullable;
import com.android.dvlib.DeviceSchema;
import com.android.resources.ScreenOrientation;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Instances of this class contain the specifications for a device. Use the
 * {@link Builder} class to construct a Device object, or the
 * {@link DeviceParser} if constructing device objects from XML conforming to
 * the {@link DeviceSchema} standards.
 */
public final class Device {
    /** Name of the device */
    @NonNull
    private final String mName;

    /** ID of the device */
    @NonNull
    private final String mId;

    /** Manufacturer of the device */
    @NonNull
    private final String mManufacturer;

    /** A list of software capabilities, one for each API level range */
    @NonNull
    private final List<Software> mSoftware;

    /** A list of phone states (landscape, portrait with keyboard out, etc.) */
    @NonNull
    private final List<State> mState;

    /** Meta information such as icon files and device frames */
    @NonNull
    private final Meta mMeta;

    /** Default state of the device */
    @NonNull
    private final State mDefaultState;

    /**
     * Returns the name of the {@link Device}. This is intended to be displayed by the user and
     * can vary over time. For a stable internal name of the device, use {@link #getId} instead.
     *
     * @deprecated Use {@link #getId()} or {@link #getDisplayName()} instead based on whether
     *     a stable identifier or a user visible name is needed
     * @return The name of the {@link Device}.
     */
    @NonNull
    @Deprecated
    public String getName() {
        return mName;
    }

    /**
     * Returns the user visible name of the {@link Device}. This is intended to be displayed by the
     * user and can vary over time. For a stable internal name of the device, use {@link #getId}
     * instead.
     *
     * @return The name of the {@link Device}.
     */
    @NonNull
    public String getDisplayName() {
        return mName;
    }

    /**
     * Returns the id of the {@link Device}.
     *
     * @return The id of the {@link Device}.
     */
    @NonNull
    public String getId() {
        return mId;
    }

    /**
     * Returns the manufacturer of the {@link Device}.
     *
     * @return The name of the manufacturer of the {@link Device}.
     */
    @NonNull
    public String getManufacturer() {
        return mManufacturer;
    }

    /**
     * Returns all of the {@link Software} configurations of the {@link Device}.
     *
     * @return A list of all the {@link Software} configurations.
     */
    @NonNull
    public List<Software> getAllSoftware() {
        return mSoftware;
    }

    /**
     * Returns all of the {@link State}s the {@link Device} can be in.
     *
     * @return A list of all the {@link State}s.
     */
    @NonNull
    public List<State> getAllStates() {
        return mState;
    }

    /**
     * Returns the default {@link Hardware} configuration for the device. This
     * is really just a shortcut for getting the {@link Hardware} on the default
     * {@link State}
     *
     * @return The default {@link Hardware} for the device.
     */
    @NonNull
    public Hardware getDefaultHardware() {
        return mDefaultState.getHardware();
    }

    /**
     * Returns the {@link Meta} object for the device, which contains meta
     * information about the device, such as the location of icons.
     *
     * @return The {@link Meta} object for the {@link Device}.
     */
    @NonNull
    public Meta getMeta() {
        return mMeta;
    }

    /**
     * Returns the default {@link State} of the {@link Device}.
     *
     * @return The default {@link State} of the {@link Device}.
     */
    @NonNull
    public State getDefaultState() {
        return mDefaultState;
    }

    /**
     * Returns the software configuration for the given API version.
     *
     * @param apiVersion
     *            The API version requested.
     * @return The Software instance for the requested API version or null if
     *         the API version is unsupported for this device.
     */
    @Nullable
    public Software getSoftware(int apiVersion) {
        for (Software s : mSoftware) {
            if (apiVersion >= s.getMinSdkLevel() && apiVersion <= s.getMaxSdkLevel()) {
                return s;
            }
        }
        return null;
    }

    /**
     * Returns the state of the device with the given name.
     *
     * @param name
     *            The name of the state requested.
     * @return The State object requested or null if there's no state with the
     *         given name.
     */
    @Nullable
    public State getState(String name) {
        for (State s : getAllStates()) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }

    @SuppressWarnings("SuspiciousNameCombination") // Deliberately swapping orientations
    @Nullable
    public Dimension getScreenSize(@NonNull ScreenOrientation orientation) {
        Screen screen = getDefaultHardware().getScreen();
        if (screen == null) {
            return null;
        }

        // compute width and height to take orientation into account.
        int x = screen.getXDimension();
        int y = screen.getYDimension();
        int screenWidth, screenHeight;

        if (x > y) {
            if (orientation == ScreenOrientation.LANDSCAPE) {
                screenWidth = x;
                screenHeight = y;
            }
            else {
                screenWidth = y;
                screenHeight = x;
            }
        }
        else {
            if (orientation == ScreenOrientation.LANDSCAPE) {
                screenWidth = y;
                screenHeight = x;
            }
            else {
                screenWidth = x;
                screenHeight = y;
            }
        }

        return new Dimension(screenWidth, screenHeight);
    }

    public static class Builder {
        private String mName;
        private String mId;
        private String mManufacturer;
        private final List<Software> mSoftware = new ArrayList<Software>();
        private final List<State> mState = new ArrayList<State>();
        private Meta mMeta;
        private State mDefaultState;

        public Builder() { }

        public Builder(Device d) {
            mName = d.getDisplayName();
            mId = d.getId();
            mManufacturer = d.getManufacturer();
            for (Software s : d.getAllSoftware()) {
                mSoftware.add(s.deepCopy());
            }
            for (State s : d.getAllStates()) {
                mState.add(s.deepCopy());
            }
            mSoftware.addAll(d.getAllSoftware());
            mState.addAll(d.getAllStates());
            mMeta = d.getMeta();
            mDefaultState = d.getDefaultState();
        }

        public void setName(@NonNull String name) {
            mName = name;
        }

        public void setId(@NonNull String id) {
            mId = id;
        }

        public void setManufacturer(@NonNull String manufacturer) {
            mManufacturer = manufacturer;
        }

        public void addSoftware(@NonNull Software sw) {
            mSoftware.add(sw);
        }

        public void addAllSoftware(@NonNull Collection<? extends Software> sw) {
            mSoftware.addAll(sw);
        }

        public void addState(State state) {
            mState.add(state);
        }

        public void addAllState(@NonNull Collection<? extends State> states) {
            mState.addAll(states);
        }

        /**
         * Removes the first {@link State} with the given name
         * @param stateName The name of the {@link State} to remove.
         * @return Whether a {@link State} was removed or not.
         */
        public boolean removeState(@NonNull String stateName) {
            for (int i = 0; i < mState.size(); i++) {
                if (stateName != null && stateName.equals(mState.get(i).getName())) {
                    mState.remove(i);
                    return true;
                }
            }
            return false;
        }

        public void setMeta(@NonNull Meta meta) {
            mMeta = meta;
        }

        public Device build() {
            if (mName == null) {
                throw generateBuildException("Device missing name");
            } else if (mManufacturer == null) {
                throw generateBuildException("Device missing manufacturer");
            } else if (mSoftware.size() <= 0) {
                throw generateBuildException("Device software not configured");
            } else if (mState.size() <= 0) {
                throw generateBuildException("Device states not configured");
            }

            if (mId == null) {
                mId = mName;
            }

            if (mMeta == null) {
                mMeta = new Meta();
            }
            for (State s : mState) {
                if (s.isDefaultState()) {
                    mDefaultState = s;
                    break;
                }
            }
            if (mDefaultState == null) {
                throw generateBuildException("Device missing default state");
            }
            return new Device(this);
        }

        private IllegalStateException generateBuildException(String err) {
            String device = "";
            if (mManufacturer != null) {
                device = mManufacturer + ' ';
            }
            if (mName != null) {
                device += mName;
            } else {
                device = "Unknown " + device +"Device";
            }

            return new IllegalStateException("Error building " + device + ": " +err);
        }
    }

    private Device(Builder b) {
        mName = b.mName;
        mId = b.mId;
        mManufacturer = b.mManufacturer;
        mSoftware = Collections.unmodifiableList(b.mSoftware);
        mState = Collections.unmodifiableList(b.mState);
        mMeta = b.mMeta;
        mDefaultState = b.mDefaultState;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Device)) {
            return false;
        }
        Device d = (Device) o;
        return mName.equals(d.getDisplayName())
                && mManufacturer.equals(d.getManufacturer())
                && mSoftware.equals(d.getAllSoftware())
                && mState.equals(d.getAllStates())
                && mMeta.equals(d.getMeta())
                && mDefaultState.equals(d.getDefaultState());
    }

    @Override
    /** A hash that's stable across JVM instances */
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + mName.hashCode();
        hash = 31 * hash + mManufacturer.hashCode();
        hash = 31 * hash + mSoftware.hashCode();
        hash = 31 * hash + mState.hashCode();
        hash = 31 * hash + mMeta.hashCode();
        hash = 31 * hash + mDefaultState.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return mName;
    }
}
