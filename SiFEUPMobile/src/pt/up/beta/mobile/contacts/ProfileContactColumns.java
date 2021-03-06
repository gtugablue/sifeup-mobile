/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package pt.up.beta.mobile.contacts;

import pt.up.beta.mobile.content.SigarraContract;
import android.provider.ContactsContract.Data;

/*
 * The standard columns representing contact's info from social apps.
 */
public final class ProfileContactColumns {

    private ProfileContactColumns() {
    }

    /**
     * MIME-type used when storing a profile {@link Data} entry.
     */
    public static final String MIME = SigarraContract.Profiles.CONTENT_ITEM_TYPE;
    public static final String MIME_PROFILE = SigarraContract.Profiles.CONTENT_ITEM_TYPE;
    public static final String MIME_SCHEDULE = SigarraContract.Schedule.CONTENT_ITEM_TYPE;

    public static final String DATA_CODE = Data.DATA1;

    public static final String DATA_SUMMARY = Data.DATA2;

    public static final String DATA_DETAIL = Data.DATA3;

    public static final String DATA_TYPE = Data.DATA4;
    
    public static final String DATA_NAME = Data.DATA5;
}
